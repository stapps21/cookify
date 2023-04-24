package de.hdmstuttgart.recipeapp.ui.newrecipe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import de.hdmstuttgart.recipeapp.R;
import de.hdmstuttgart.recipeapp.database.RecipeRepository;
import de.hdmstuttgart.recipeapp.databinding.ActivityNewRecipeBinding;
import de.hdmstuttgart.recipeapp.interfaces.IOnContinueClickListener;
import de.hdmstuttgart.recipeapp.models.FullRecipe;
import de.hdmstuttgart.recipeapp.models.Recipe;
import de.hdmstuttgart.recipeapp.utils.RecipeImageHelper;


public class NewRecipeActivity extends AppCompatActivity {

    private final static String TAG = "NewRecipeActivity";

    SharedPageViewModel mSharedPageViewModel;
    private ActivityNewRecipeBinding mBinding;
    private ViewPager2 mViewPager;
    private SectionsPagerAdapter mAdapter;
    private Button mbtnPrevious, mbtnNext;

    private enum Direction {
        PREVIOUS,
        NEXT
    }

    /////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    /////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPageViewModel = new ViewModelProvider(this).get(SharedPageViewModel.class);

        mBinding = de.hdmstuttgart.recipeapp.databinding.ActivityNewRecipeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setupToolbar();
        initViews();
        setupViewPager();
        setupControllButtons();
    }

    /////////////////////////////////////////////////////////////////////////
    // INIT
    /////////////////////////////////////////////////////////////////////////

    private void initViews() {
        mbtnNext = mBinding.btnNext;
        mbtnPrevious = mBinding.btnPrevious;
        mViewPager = mBinding.viewPager;
    }

    /////////////////////////////////////////////////////////////////////////
    // SETUP
    /////////////////////////////////////////////////////////////////////////

    private void setupViewPager() {
        mAdapter = new SectionsPagerAdapter(this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setUserInputEnabled(false);
    }

    private void setupControllButtons() {
        mbtnNext.setOnClickListener(v -> nextFragmentPage());
        mbtnPrevious.setOnClickListener(v -> changeFragment(Direction.PREVIOUS));
        mbtnPrevious.setEnabled(false);
    }

    private void setupToolbar() {
        Toolbar toolbar = mBinding.topToolBar;
        toolbar.setTitle("New Recipe");
        setSupportActionBar(toolbar);
        mBinding.topAppBarLayout.addLiftOnScrollListener(((elevation, backgroundColor) -> getWindow().setStatusBarColor(backgroundColor)));

    }

    /////////////////////////////////////////////////////////////////////////
    // HELPERMETHODS
    /////////////////////////////////////////////////////////////////////////

    private void nextFragmentPage() {
        Fragment fragment = mAdapter.getFragmentAtPosition(mViewPager.getCurrentItem());
        if (fragment instanceof IOnContinueClickListener) {
            boolean requiredFieldsFilled = ((IOnContinueClickListener) fragment).onClickContinue();
            if (requiredFieldsFilled) {
                changeFragment(Direction.NEXT);
            }
        } else {
            throw new ClassCastException(fragment.getClass().getSimpleName() + " must implement OnButtonClickListener");
        }
    }

    private void changeFragment(Direction direction) {

        int currentItem = mViewPager.getCurrentItem();
        int newItem = 0;
        switch (direction) {
            case PREVIOUS:
                if (currentItem == 0) {
                    return;
                }
                newItem = currentItem - 1;
                mbtnNext.setText(R.string.btn_next);
                if (newItem == 0) {
                    mbtnPrevious.setEnabled(false);
                }
                break;
            case NEXT:
                if (currentItem == mAdapter.getItemCount() - 1) {
                    onClickSave();
                    return;
                }
                newItem = currentItem + 1;
                mbtnPrevious.setEnabled(true);
                if (newItem == mAdapter.getItemCount() - 1) {
                    mbtnNext.setText(R.string.btn_save);
                }
                break;
        }
        mViewPager.setCurrentItem(newItem, true);
    }

    /**
     * Saves the recipe to the database and moves the image from cach to external storage
     */
    private void onClickSave() {

        String imgName = null;
        if (mSharedPageViewModel.getImageSource() != SharedPageViewModel.IMAGE_SOURCE.NONE) {
            // Move image from cach to external storage
            final RecipeImageHelper rimgHelper = new RecipeImageHelper(this);
            imgName = rimgHelper.moveCacheToExternalStorage(true);
        }

        // create basic recipe from inputs
        Recipe recipe = new Recipe(
                imgName,
                mSharedPageViewModel.getName(),
                mSharedPageViewModel.getDescription(),
                mSharedPageViewModel.getTime(),
                mSharedPageViewModel.getDifficulty(),
                mSharedPageViewModel.getServes(),
                false
        );

        // Insert recipe into database
        RecipeRepository repository = new RecipeRepository(getApplication());
        repository.insertFullRecipe(new FullRecipe(recipe, mSharedPageViewModel.getIngredients(), mSharedPageViewModel.getSteps(), mSharedPageViewModel.getFilters()));

        Toast.makeText(this, "Rezepe \"" + mSharedPageViewModel.getName() + "\" wurde hinzugefügt!", Toast.LENGTH_SHORT).show();
        finish();
    }

    /////////////////////////////////////////////////////////////////////////
    // MENU
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onSupportNavigateUp() {
        //onBackPressed();
        finish();
        return true;
    }
}