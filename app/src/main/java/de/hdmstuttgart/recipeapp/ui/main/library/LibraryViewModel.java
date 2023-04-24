package de.hdmstuttgart.recipeapp.ui.main.library;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.hdmstuttgart.recipeapp.database.RecipeRepository;
import de.hdmstuttgart.recipeapp.models.Recipe;

public class LibraryViewModel extends AndroidViewModel {

    private final static String TAG = "LibraryViewModel";

    private final RecipeRepository mRepository;


    public LibraryViewModel(Application application) {
        super(application);
        mRepository = new RecipeRepository(application);
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mRepository.getAllRecipes();
    }

    public LiveData<List<Recipe>> getRecipesBySearchText(String query) {
        return mRepository.getRecipesBySearchText(query);
    }

}