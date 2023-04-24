package de.hdmstuttgart.recipeapp.ui.newrecipe;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.hdmstuttgart.recipeapp.exceptions.OutOfBoundViewPagerFragmentsException;
import de.hdmstuttgart.recipeapp.ui.newrecipe.fragments.AddFiltersFragment;
import de.hdmstuttgart.recipeapp.ui.newrecipe.fragments.AddIngredientsFragment;
import de.hdmstuttgart.recipeapp.ui.newrecipe.fragments.AddPictureFragment;
import de.hdmstuttgart.recipeapp.ui.newrecipe.fragments.AddRecipeInformationFragment;
import de.hdmstuttgart.recipeapp.ui.newrecipe.fragments.AddStepsFragment;


/**
 * A FragmentStateAdapter that returns a fragment corresponding to
 * one of the pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {

    // All Fragments in order of the viewpager
    private final Fragment[] mFragments = new Fragment[]{
            new AddRecipeInformationFragment(),
            new AddIngredientsFragment(),
            new AddStepsFragment(),
            new AddFiltersFragment(),
            new AddPictureFragment(),
    };

    public SectionsPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     *
     * @param position index of mFragments Array
     * @return fragment at a certain position
     */
    public Fragment getFragmentAtPosition(int position) {
        if (position >= mFragments.length) {
            throw new OutOfBoundViewPagerFragmentsException(position);
        }
        return mFragments[position];
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments[position];
    }

    @Override
    public int getItemCount() {
        return mFragments.length;
    }
}