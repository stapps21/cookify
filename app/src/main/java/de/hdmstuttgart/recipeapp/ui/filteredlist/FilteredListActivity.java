package de.hdmstuttgart.recipeapp.ui.filteredlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdmstuttgart.recipeapp.R;
import de.hdmstuttgart.recipeapp.adapter.FilterListAdapter;
import de.hdmstuttgart.recipeapp.adapter.HomeListAdapter;
import de.hdmstuttgart.recipeapp.databinding.ActivityFilteredListBinding;
import de.hdmstuttgart.recipeapp.interfaces.IAdapterItemClickListener;
import de.hdmstuttgart.recipeapp.models.Recipe;
import de.hdmstuttgart.recipeapp.ui.recipedetail.RecipeDetailActivity;
import de.hdmstuttgart.recipeapp.utils.ConverterDpPx;
import de.hdmstuttgart.recipeapp.utils.GridSpacingItemDecoration;

public class FilteredListActivity extends AppCompatActivity {

    public static final String TAG = "FilteredListActivity";
    public static final String FILTERED_LIST_EXTRA = "extra_filtered_list";

    private ActivityFilteredListBinding mBinding;
    private FilteredListViewModel mFilteredListViewModel;

    private FilterListAdapter mAdapter;

    // recipe click listener
    private final IAdapterItemClickListener mRecipeClickListener = id -> {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, id);
        startActivity(intent);
    };

    /////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    /////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFilteredListViewModel = new ViewModelProvider(this).get(FilteredListViewModel.class);

        mBinding = ActivityFilteredListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Get IDs
        List<Long> myList = (ArrayList<Long>) getIntent().getSerializableExtra(FILTERED_LIST_EXTRA);
        long[] ids = myList.stream().mapToLong(Long::longValue).toArray();

        setupToolbar(ids.length);
        setupRecyclerview();

        mFilteredListViewModel.getRecipesByIDs(ids).observe(this, recipes -> {
            Log.d(TAG, "Number of filtered recipes: " + recipes.size());
            mAdapter.submitList(recipes);
        });
    }

    /////////////////////////////////////////////////////////////////////////
    // SETUP
    /////////////////////////////////////////////////////////////////////////

    private void setupRecyclerview() {
        RecyclerView rvFilteredList = mBinding.rvFilteredList;
        rvFilteredList.setItemAnimator(null);
        rvFilteredList.setLayoutManager(new GridLayoutManager(this, 2));

        int spacing = ConverterDpPx.dpToPx(this, 16);
        rvFilteredList.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));

        mAdapter = new FilterListAdapter(new HomeListAdapter.RecipeDiff(), mRecipeClickListener);
        rvFilteredList.setAdapter(mAdapter);
    }

    private void setupToolbar(int numbRecipes) {
        Toolbar toolbar = mBinding.topToolBar;
        setSupportActionBar(toolbar);
        mBinding.topAppBarLayout.addLiftOnScrollListener(((elevation, backgroundColor) -> getWindow().setStatusBarColor(backgroundColor)));
        toolbar.setTitle(MessageFormat.format(getString(R.string.filtered_title), numbRecipes));
    }

    /////////////////////////////////////////////////////////////////////////
    // MENU
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}