package de.hdmstuttgart.recipeapp.ui.main.filter;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.hdmstuttgart.recipeapp.database.RecipeRepository;
import de.hdmstuttgart.recipeapp.enums.EFilter;

public class FilterViewModel extends AndroidViewModel {

    private final static String TAG = "SearchViewModel";

    private final RecipeRepository mRepository;

    public FilterViewModel(Application application) {
        super(application);
        mRepository = new RecipeRepository(application);
    }

    public LiveData<List<Long>> getRecipeIDsByFilters(List<EFilter> filters) {
        return mRepository.getRecipeIDsByFilters(filters);
    }
}