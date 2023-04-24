package de.hdmstuttgart.recipeapp.ui.main.home;

import static de.hdmstuttgart.recipeapp.enums.EFilter.VEGAN;
import static de.hdmstuttgart.recipeapp.enums.EFilter.VEGETARIAN;
import static de.hdmstuttgart.recipeapp.ui.filteredlist.FilteredListActivity.FILTERED_LIST_EXTRA;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdmstuttgart.recipeapp.R;
import de.hdmstuttgart.recipeapp.adapter.HomeListAdapter;
import de.hdmstuttgart.recipeapp.databinding.FragmentHomeBinding;
import de.hdmstuttgart.recipeapp.enums.EFilter;
import de.hdmstuttgart.recipeapp.models.Recipe;
import de.hdmstuttgart.recipeapp.ui.filteredlist.FilteredListActivity;
import de.hdmstuttgart.recipeapp.ui.main.MainActivity;
import de.hdmstuttgart.recipeapp.ui.newrecipe.NewRecipeActivity;
import de.hdmstuttgart.recipeapp.ui.recipedetail.RecipeDetailActivity;
import de.hdmstuttgart.recipeapp.ui.settings.SettingsActivity;

public class HomeFragment extends Fragment implements MenuProvider {

    private static final String TAG = "HomeFragment";
    private static final int NUMB_OF_OBSERVERS = 5;

    private FragmentHomeBinding mBinding;
    private HomeViewModel mHomeViewModel;
    private LinearLayout mllNoRecipes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHomeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        mllNoRecipes = mBinding.incNoRecipes.llNoRecipesContainer;
        mBinding.incNoRecipes.btnAddRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewRecipeActivity.class);
            startActivity(intent);
        });

        // Set up buttons and RecyclerViews
        EFilter[] filters = {EFilter.BREAKFAST, EFilter.LUNCH, EFilter.DINNER, EFilter.SNACK, EFilter.DESSERT, EFilter.SALAD};
        LinearLayout[] buttons = {mBinding.llBreakfast, mBinding.llLunch, mBinding.llDinner, mBinding.llSnack, mBinding.llDessert, mBinding.llSalad};
        setUpButtons(buttons, filters);

        // Setup RecyclerViews
        List<Pair<ConstraintLayout, Pair<RecyclerView, LiveData<List<Recipe>>>>> recyclerViewData = Arrays.asList(
                new Pair<>(mBinding.clFavorites, new Pair<>(mBinding.rvFavorites, mHomeViewModel.getFavouriteRecipes())),
                new Pair<>(mBinding.clRecentlyAdded, new Pair<>(mBinding.rvRecentlyAdded, mHomeViewModel.getRecentlyAddedRecipes())),
                new Pair<>(mBinding.clVegan, new Pair<>(mBinding.rvVegan, mHomeViewModel.getCategorieRecipes(VEGAN))),
                new Pair<>(mBinding.clVegetarian, new Pair<>(mBinding.rvVegetarian, mHomeViewModel.getCategorieRecipes(VEGETARIAN))),
                new Pair<>(mBinding.clLactoseFree, new Pair<>(mBinding.rvLactoseFree, mHomeViewModel.getCategorieRecipes(EFilter.LACTOSE_FREE)))
        );

        setUpRecyclerViews(recyclerViewData);
        checkIfAnyRecipesAvailable(recyclerViewData);


        // Set up MenuProvider
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.STARTED);

        return root;
    }

    private void checkIfAnyRecipesAvailable(List<Pair<ConstraintLayout, Pair<RecyclerView, LiveData<List<Recipe>>>>> recyclerViewData) {
        AtomicBoolean anyRecipesAvailable = new AtomicBoolean(false);

        for (Pair<ConstraintLayout, Pair<RecyclerView, LiveData<List<Recipe>>>> pair : recyclerViewData) {
            LiveData<List<Recipe>> recipeList = pair.second.second;

            recipeList.observe(getViewLifecycleOwner(), recipes -> {
                if (!recipes.isEmpty()) {
                    anyRecipesAvailable.set(true);
                }

                if (anyRecipesAvailable.get()) {
                    // There are recipes available
                    mllNoRecipes.setVisibility(View.GONE);
                } else {
                    // There are no recipes available
                    mllNoRecipes.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void setUpButtons(LinearLayout[] buttons, EFilter[] filters) {
        for (int i = 0; i < buttons.length; i++) {
            EFilter filter = filters[i];
            LiveData<List<Long>> recipes = mHomeViewModel.getRecipeIDsByFilters(Collections.singletonList(filter));
            LinearLayout button = buttons[i];
            recipes.observe(getViewLifecycleOwner(), idObjs -> button.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), FilteredListActivity.class);
                ArrayList<Long> recipeIDs = new ArrayList<>(idObjs);
                intent.putExtra(FILTERED_LIST_EXTRA, recipeIDs);
                startActivity(intent);
            }));
        }
    }

    private void setUpRecyclerViews(List<Pair<ConstraintLayout, Pair<RecyclerView, LiveData<List<Recipe>>>>> recyclerViewData) {
        for (Pair<ConstraintLayout, Pair<RecyclerView, LiveData<List<Recipe>>>> pair : recyclerViewData) {
            ConstraintLayout constraintLayout = pair.first;
            RecyclerView recyclerView = pair.second.first;
            LiveData<List<Recipe>> recipeList = pair.second.second;

            recyclerView.setItemAnimator(null);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            HomeListAdapter adapter = new HomeListAdapter(new HomeListAdapter.RecipeDiff(), id -> {
                Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, id);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);

            recipeList.observe(getViewLifecycleOwner(), recipes -> {
                constraintLayout.setVisibility(recipes.isEmpty() ? View.GONE : View.VISIBLE);
                adapter.submitList(recipes);
                observerRan(recyclerView.getId());
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set toolbar title
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(R.string.welcome);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void observerRan(int id) {
        if (!mHomeViewModel.isObserverFinished(id)) {
            mHomeViewModel.addFinishedObserver(id);
        }

        if (mHomeViewModel.finishedObserversCount() >= NUMB_OF_OBSERVERS) {
            mBinding.progressCircular.setVisibility(View.GONE);
            mBinding.tvLoading.setVisibility(View.GONE);
            mBinding.nsv.setVisibility(View.VISIBLE);

            Log.i(TAG, "Everything is loaded and ready!");
        }
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.top_toolbar_home_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}