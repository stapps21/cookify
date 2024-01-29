package de.hdmstuttgart.recipeapp.ui.newrecipe.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import de.hdmstuttgart.recipeapp.R;
import de.hdmstuttgart.recipeapp.databinding.FragmentAddFiltersBinding;
import de.hdmstuttgart.recipeapp.enums.EFilter;
import de.hdmstuttgart.recipeapp.interfaces.IOnContinueClickListener;
import de.hdmstuttgart.recipeapp.models.Filter;
import de.hdmstuttgart.recipeapp.ui.newrecipe.SharedPageViewModel;


/**
 * A placeholder fragment containing a simple view.
 */
public class AddFiltersFragment extends Fragment implements IOnContinueClickListener {

    // logging
    private static final String TAG = "AddFiltersFragment";

    // general
    private SharedPageViewModel mSharedPageViewModel;
    private FragmentAddFiltersBinding mBinding;

    /////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    /////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPageViewModel = new ViewModelProvider(requireActivity()).get(SharedPageViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentAddFiltersBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        setupFilters();

        return root;
    }

    private void setupFilters() {
        // FilterGroup {Meals, Diets}
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // HELPER METHODS
    /////////////////////////////////////////////////////////////////////////

    private void handleFilterChange(Chip chip) {
        Filter filter = new Filter(EFilter.values()[(int) chip.getTag()]);
        if (chip.isChecked()) {
            mSharedPageViewModel.addFilter(filter);
        } else {
            mSharedPageViewModel.removeFilter(filter);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // IOnContinueClickListener
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onClickContinue() {
        // No required fields
        return true;
    }
}