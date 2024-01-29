package de.hdmstuttgart.recipeapp.ui.main.library;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import de.hdmstuttgart.recipeapp.R;
import de.hdmstuttgart.recipeapp.adapter.SearchListAdapter;
import de.hdmstuttgart.recipeapp.databinding.FragmentLibraryBinding;
import de.hdmstuttgart.recipeapp.interfaces.IAdapterItemClickListener;
import de.hdmstuttgart.recipeapp.models.Recipe;
import de.hdmstuttgart.recipeapp.ui.newrecipe.NewRecipeActivity;
import de.hdmstuttgart.recipeapp.ui.recipedetail.RecipeDetailActivity;
import de.hdmstuttgart.recipeapp.utils.ConverterDpPx;
import de.hdmstuttgart.recipeapp.utils.GridSpacingItemDecoration;

public class LibraryFragment extends Fragment implements MenuProvider {

    private final static String TAG = "LibraryFragment";
    private final List<Recipe> recipeList = new ArrayList<>();
    private FragmentLibraryBinding mBinding;
    private LibraryViewModel mLibraryViewModel;
    private RecyclerView mrvSearchResults;
    private SearchListAdapter mAdapter;
    private LinearLayout mllNoSearchResults;
    private LinearLayout mllNoRecipes;
    private SearchView.OnQueryTextListener queryTextListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mLibraryViewModel =
                new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);
        mllNoSearchResults = mBinding.incNoSearchResults.llNoSearchResultsContainer;
        mllNoRecipes = mBinding.incNoRecipes.llNoRecipesContainer;
        mBinding.incNoRecipes.btnAddRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewRecipeActivity.class);
            startActivity(intent);
        });

        View root = mBinding.getRoot();
        IAdapterItemClickListener recipeClickListener = id -> {
            Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, id);
            startActivity(intent);
        };

        // Set up the RecyclerView
        mrvSearchResults = mBinding.rvSearch;
        mrvSearchResults.setItemAnimator(null);
        mrvSearchResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
        int spacing = ConverterDpPx.dpToPx(getContext(), 16);
        mrvSearchResults.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
        mAdapter = new SearchListAdapter(new SearchListAdapter.RecipeDiff(), recipeClickListener);
        mrvSearchResults.setAdapter(mAdapter);

        LiveData<List<Recipe>> recipes = mLibraryViewModel.getAllRecipes();
        recipes.observe(getViewLifecycleOwner(), recipes1 -> {
            mAdapter.submitList(recipes1);
            mllNoRecipes.setVisibility(recipes1.size() == 0 ? View.VISIBLE : View.GONE);
        });

        setupSearchViewQueryListener();

        // Setup MenuProvider
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.STARTED);

        ExtendedFloatingActionButton fab = mBinding.fabAddRecipe;
        fab.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_chef, null));
        fab.setText(getString(R.string.new_recipe));
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewRecipeActivity.class);
            startActivity(intent);
        });

        return root;
    }

    private void setupSearchViewQueryListener() {
        queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                List<Recipe> filteredList = recipeList.stream()
                        .filter(recipe ->
                                recipe.getName().toLowerCase(Locale.ROOT).trim()
                                        .contains(newText.toLowerCase(Locale.ROOT))
                        )
                        .collect(Collectors.toList());
                mAdapter.submitList(filteredList);
                if (filteredList.size() == 0 && !newText.trim().isEmpty()) {
                    mllNoSearchResults.setVisibility(View.VISIBLE);
                } else {
                    mllNoSearchResults.setVisibility(View.GONE);
                    mrvSearchResults.scrollToPosition(0);
                }
                return true;
            }
        };
    }

    @Override
    public void onResume() {
        LiveData<List<Recipe>> recipes = mLibraryViewModel.getAllRecipes();
        recipes.observe(getViewLifecycleOwner(), recipes1 -> {
            recipeList.clear();
            recipeList.addAll(recipes1);
        });
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.top_toolbar_library_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(queryTextListener);


    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}