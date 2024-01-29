package de.hdmstuttgart.recipeapp.ui.main.filter;

import static de.hdmstuttgart.recipeapp.ui.filteredlist.FilteredListActivity.FILTERED_LIST_EXTRA;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdmstuttgart.recipeapp.R;
import de.hdmstuttgart.recipeapp.databinding.FragmentFilterBinding;
import de.hdmstuttgart.recipeapp.enums.EFilter;
import de.hdmstuttgart.recipeapp.ui.filteredlist.FilteredListActivity;
import de.hdmstuttgart.recipeapp.ui.main.MainActivity;


public class FilterFragment extends Fragment implements MenuProvider {

    private final static String TAG = "SearchFragment";
    private final List<EFilter> mCheckedFilters = new ArrayList<>();
    ExtendedFloatingActionButton mFAB;
    private FragmentFilterBinding mBinding;
    private FilterViewModel mFilterViewModel;
    private int mNumbCheckedFilters;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFilterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        mBinding = FragmentFilterBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        // TODO: implement functionality for reset (Don not inflate menu without implementation)
        // Setup MenuProvider
        // MenuHost menuHost = requireActivity();
        // menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.STARTED);

        // FAB
        mFAB = mBinding.fabAddRecipe;
        mFAB.setOnClickListener(null);

        // Dynamically load filter chips from enum
        setupFilters();

        return root;
    }

    /////////////////////////////////////////////////////////////////////////
    // SETUP
    /////////////////////////////////////////////////////////////////////////

    private void setupFilters() {
        final ChipGroup[] chipGroups = {mBinding.cgMeals, mBinding.cgDiets};
        final int[][] range = {
                {EFilter.START_MEALS_ENUM.ordinal() + 1, EFilter.END_MEALS_ENUM.ordinal() - 1},
                {EFilter.START_DIETS_ENUM.ordinal() + 1, EFilter.END_DIETS_ENUM.ordinal() - 1}
        };

        // FilterGroup {Meals, Diets}
        for (int chipGroup = 0; chipGroup < chipGroups.length; chipGroup++) {

            // Filters from FilterGroup
            for (int i = range[chipGroup][0]; i < range[chipGroup][1]; i++) {
                String filterName = getResources().getString(EFilter.values()[i].getNameRes());

                // create chip view
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.component_chip_layout_single, chipGroups[chipGroup], false);
                chip.setText(filterName);
                chip.setTag(i);
                chip.setVisibility(View.VISIBLE);
                chip.setOnClickListener(v -> handleFilterChange((Chip) v));

                // add chip view to parent view
                chipGroups[chipGroup].addView(chip);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // LOGIC
    /////////////////////////////////////////////////////////////////////////

    private void handleFilterChange(Chip chip) {
        if (chip.isChecked()) {
            mCheckedFilters.add(EFilter.values()[(int) chip.getTag()]);
        } else {
            mCheckedFilters.remove(EFilter.values()[(int) chip.getTag()]);
        }

        // update number of checked filters
        mNumbCheckedFilters += chip.isChecked() ? 1 : -1;

        // If no filter is checked => hide FAB
        if (mNumbCheckedFilters == 0) {
            mFAB.setOnClickListener(null);
            mFAB.setVisibility(View.GONE);
            return;
        }

        // Get recipe_ids from database which is matching all filters
        LiveData<List<Long>> number = mFilterViewModel.getRecipeIDsByFilters(mCheckedFilters);
        number.observe(getViewLifecycleOwner(), idObjs -> {
            // Show FAB and number of recipes found
            if (idObjs.size() > 1) {
                mFAB.setText(MessageFormat.format(getString(R.string.fab_show_numb_filter_results), idObjs.size()));
            } else if (idObjs.size() == 1){
                mFAB.setText(MessageFormat.format(getString(R.string.fab_show_numb_filter_result), 1));
            } else {
                mFAB.setText(getString(R.string.fab_no_recipes_found));
            }
            mFAB.setVisibility(View.VISIBLE);

            // Add click functionality
            mFAB.setOnClickListener(v -> {
                if ( idObjs.size() == 0)
                    return;
                // Open filtered list activity
                Intent intent = new Intent(getActivity(), FilteredListActivity.class);
                ArrayList<Long> recipeIDs = new ArrayList<>(idObjs);
                intent.putExtra(FILTERED_LIST_EXTRA, recipeIDs);
                startActivity(intent);
            });

        });

    }

    /////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    /////////////////////////////////////////////////////////////////////////

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // MENU
    /////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.top_toolbar_filter_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return menuItem.getItemId() == R.id.reset_filter;
    }
}

