package de.hdmstuttgart.recipeapp.ui.filteredlist;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.hdmstuttgart.recipeapp.database.RecipeRepository;
import de.hdmstuttgart.recipeapp.models.Recipe;

public class FilteredListViewModel extends AndroidViewModel {

    private final static String TAG = "FilteredListViewModel";

    private final RecipeRepository mRepository;


    public FilteredListViewModel(Application application) {
        super(application);
        mRepository = new RecipeRepository(application);
    }

    public LiveData<List<Recipe>> getRecipesByIDs(long[] ids) {
        return mRepository.getRecipesByIDs(ids);
    }

}