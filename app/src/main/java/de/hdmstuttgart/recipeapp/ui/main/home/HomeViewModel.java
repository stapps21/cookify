package de.hdmstuttgart.recipeapp.ui.main.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdmstuttgart.recipeapp.database.RecipeRepository;
import de.hdmstuttgart.recipeapp.enums.EFilter;
import de.hdmstuttgart.recipeapp.models.Recipe;

public class HomeViewModel extends AndroidViewModel {

    private final RecipeRepository mRepository;
    private final Set<Integer> finishedObservers = new HashSet<>();

    public HomeViewModel(Application application) {
        super(application);
        mRepository = new RecipeRepository(application);
    }

    public boolean isObserverFinished(int id) {
        return finishedObservers.contains(id);
    }

    public void addFinishedObserver(int id) {
        finishedObservers.add(id);
    }

    public int finishedObserversCount() {
        return finishedObservers.size();
    }

    public LiveData<List<Recipe>> getFavouriteRecipes() {
        return mRepository.getFavouriteRecipes();
    }

    public LiveData<List<Recipe>> getRecentlyAddedRecipes() {
        return mRepository.getRecentlyAddedRecipes();
    }

    public LiveData<List<Recipe>> getCategorieRecipes(EFilter eFilter) {
        return mRepository.getCategorieRecipes(eFilter);
    }

    public LiveData<List<Long>> getRecipeIDsByFilters(List<EFilter> filters) {
        return mRepository.getRecipeIDsByFilters(filters);
    }
}
