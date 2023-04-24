package de.hdmstuttgart.recipeapp.ui.recipedetail;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdmstuttgart.recipeapp.R;
import de.hdmstuttgart.recipeapp.adapter.IngredientListAdapter;
import de.hdmstuttgart.recipeapp.adapter.StepListAdapter;
import de.hdmstuttgart.recipeapp.databinding.ActivityRecipeDetailBinding;
import de.hdmstuttgart.recipeapp.models.Filter;
import de.hdmstuttgart.recipeapp.models.FullRecipe;
import de.hdmstuttgart.recipeapp.models.Ingredient;
import de.hdmstuttgart.recipeapp.models.Recipe;
import de.hdmstuttgart.recipeapp.models.Step;
import de.hdmstuttgart.recipeapp.utils.RecipeImageHelper;

public class RecipeDetailActivity extends AppCompatActivity implements MenuProvider {

    // const
    private final static String TAG = "RecipeDetailActivity";
    public final static int MIN_SERVINGS = 1;
    public final static String EXTRA_RECIPE_ID = "de.hdmstuttgart.recipeapp.RECIPEID";

    // general
    private ActivityRecipeDetailBinding mBinding;
    private RecipeDetailViewModel mRecipeDetailViewModel;

    // Ingredient Lists
    private final List<Ingredient> mIngredientsCalculatedServings = new ArrayList<>();
    private final List<Ingredient> mIngredientsDefault = new ArrayList<>();

    // Adapter
    private IngredientListAdapter mIngredientsAdapter;
    private StepListAdapter mStepsAdapter;

    // vars
    private FullRecipe mFullRecipe;
    private boolean mIsFavorite;
    private int mServes = 2;
    private int mDefaultServes = 2;

    // Views
    private Button mbtnRemovePortion, mbtnAddPortion;
    private ImageView mivExpandedToolbar;
    private TextView mtvServes, mtvName, mtvDesc, mtvTime, mtvDifficulty;
    private FloatingActionButton mbtnFavorite;
    private ChipGroup mcgFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mBinding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // receive recipe ID and pass it to the ViewModel (Factory)
        long recipeID = getIntent().getExtras().getLong(EXTRA_RECIPE_ID);
        mRecipeDetailViewModel = new ViewModelProvider(this, new RecipeDetailViewModel.Factory(getApplication(), recipeID)).get(RecipeDetailViewModel.class);

        // Menu
        addMenuProvider(this, this, Lifecycle.State.STARTED);
        setupToolbar();

        // setup views
        initViews();
        setupRecyclerviewIngredients();
        setupRecyclerviewSteps();
        setupObserver();
    }

    /////////////////////////////////////////////////////////////////////////
    // INIT
    /////////////////////////////////////////////////////////////////////////

    private void initViews() {
        mbtnRemovePortion = mBinding.btnRemoveServing;
        mbtnAddPortion = mBinding.btnAddServing;
        mtvServes = mBinding.tvNumberServings;
        mivExpandedToolbar = mBinding.expandedImage;
        mtvName = mBinding.tvName;
        mtvDesc = mBinding.tvDescription;
        mtvTime = mBinding.tvTime;
        mtvDifficulty = mBinding.tvDifficulty;
        mbtnFavorite = mBinding.fabFavorite;
        mcgFilters = mBinding.cgFilters;

        mbtnAddPortion.setOnClickListener(v -> handleServingNumberChange(+1));
        mbtnRemovePortion.setOnClickListener(v -> handleServingNumberChange(-1));
    }

    /////////////////////////////////////////////////////////////////////////
    // SETUP - general
    /////////////////////////////////////////////////////////////////////////

    private void setupToolbar() {
        Toolbar mToolbar = mBinding.toolbar;
        setSupportActionBar(mToolbar);
    }

    private void setupCollapsingToolbarImage(String imageName) {
        RecipeImageHelper rimgHelper = new RecipeImageHelper(this);
        final Uri imgUri = rimgHelper.getImageUri(imageName, false);
        if (imgUri != Uri.EMPTY) {
            mivExpandedToolbar.setImageURI(imgUri);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // SETUP - recyclerviews
    /////////////////////////////////////////////////////////////////////////

    private void setupRecyclerviewSteps() {
        final RecyclerView rvSteps = mBinding.rvSteps;
        mStepsAdapter = new StepListAdapter(new StepListAdapter.StepDiff());
        rvSteps.setAdapter(mStepsAdapter);
        rvSteps.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupRecyclerviewIngredients() {
        final RecyclerView rvIngredients = mBinding.rvIngredients;
        rvIngredients.setItemAnimator(null);
        mIngredientsAdapter = new IngredientListAdapter(new IngredientListAdapter.IngredientDiff());
        rvIngredients.setAdapter(mIngredientsAdapter);
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
    }

    /////////////////////////////////////////////////////////////////////////
    // SETUP - observer
    /////////////////////////////////////////////////////////////////////////

    /**
     * Observer just runs once!
     * After received data from database, pass information to the views
     */
    private void setupObserver() {
        LiveData<FullRecipe> rec = mRecipeDetailViewModel.getRecipe();
        rec.observe(this, fullRecipe -> {
            mFullRecipe = fullRecipe;

            Recipe recipe = fullRecipe.recipe;
            List<Ingredient> ingredients = fullRecipe.ingredients;
            List<Step> steps = fullRecipe.steps;

            setupCollapsingToolbarImage(fullRecipe.recipe.getImageName());

            // Basic view
            mtvName.setText(recipe.getName());
            mtvDesc.setText(recipe.getDescription());
            mtvTime.setText(MessageFormat.format("{0} min", recipe.getTime()));
            mtvDifficulty.setText(recipe.getDifficulty().getNameRes());
            mDefaultServes = recipe.getServes();
            mServes = recipe.getServes();
            mtvServes.setText(String.valueOf(mServes));

            // calculate servings
            mIngredientsDefault.addAll(ingredients);
            mIngredientsCalculatedServings.addAll(ingredients);

            // init adapters
            mIngredientsAdapter.submitList(mIngredientsCalculatedServings);
            mStepsAdapter.submitList(steps);

            // Favorite
            mIsFavorite = recipe.getFavorite();
            mbtnFavorite.setImageDrawable(getFavoriteDrawable());
            mbtnFavorite.setOnClickListener(v -> {
                mIsFavorite = !mIsFavorite;
                mRecipeDetailViewModel.updateFavorite(mIsFavorite);
                mbtnFavorite.setImageDrawable(getFavoriteDrawable());
            });


            setupFilters(fullRecipe.filters);

            // Only execute once!
            rec.removeObservers(this);
        });
    }

    private void setupFilters(List<Filter> filters) {
        if (filters.size() == 0) {
            // No filters => hide
            mcgFilters.setVisibility(View.GONE);
        } else {
            // inflate all filters to chips
            for (Filter filter : filters) {
                String filterName = getResources().getString(filter.getFilter().getNameRes());
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.component_chip_layout_single_read_only, mcgFilters, false);
                chip.setText(filterName);
                mcgFilters.addView(chip);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // HELPERMETHODS
    /////////////////////////////////////////////////////////////////////////

    /**
     * returns favorite Drawable
     * @return Drawable of heart (if recipe is favorite it's filled, otherwise it's not)
     */
    private Drawable getFavoriteDrawable() {
        return mIsFavorite ?
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24) :
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_border_24);
    }

    /**
     * @param change difference of current number of servings
     *               +X adds X servings
     *               -X removes X servings
     */
    private void handleServingNumberChange(int change) {

        int tmpNbrServings = mServes + change;
        if (tmpNbrServings < MIN_SERVINGS) {
            return;
        }

        mServes += change;
        mtvServes.setText(String.valueOf(mServes));
        mbtnRemovePortion.setEnabled(mServes != MIN_SERVINGS);

        List<Ingredient> newCalculated = calculateServings();
        mIngredientsCalculatedServings.clear();
        mIngredientsCalculatedServings.addAll(newCalculated);
        mIngredientsAdapter.notifyItemRangeChanged(0, mIngredientsCalculatedServings.size());
    }

    /**
     *
     * @return List with ingredients which includes new calculated servings
     */
    private List<Ingredient> calculateServings() {
        List<Ingredient> newCalculated = new ArrayList<>();
        for (Ingredient i : mIngredientsDefault) {
            float singleAmount = i.getAmount() / mDefaultServes; // single Amount
            float newAmount = singleAmount * mServes;
            newCalculated.add(new Ingredient(i.getName(), newAmount, i.getUnit()));
        }
        return newCalculated;
    }

    /**
     * Deletes the recipe from the database and the images from the storage
     */
    private void deleteRecipe() {
        // Check if recipe is already loaded
        if (mFullRecipe != null) {

            // Delete recipe from database
            mRecipeDetailViewModel.deleteRecipe(mFullRecipe);

            // Delete recipe image from storage (if available)
            String imgName = mFullRecipe.recipe.getImageName();
            if (imgName != null) {
                RecipeImageHelper recipeImageHelper = new RecipeImageHelper(this);
                recipeImageHelper.deleteImage(imgName, true);
            }

            // Message successful deletion
            Toast.makeText(this, "Recipe \"" + mFullRecipe.recipe.getName() + "\" was successfully deleted", Toast.LENGTH_SHORT).show();
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // MENU
    /////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.top_toolbar_recipe_detail_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete) {
            deleteRecipe();
            finish();
            return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}