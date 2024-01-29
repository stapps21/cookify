package de.hdmstuttgart.recipeapp.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Arrays;
import java.util.List;

import de.hdmstuttgart.recipeapp.enums.EFilter;
import de.hdmstuttgart.recipeapp.models.Filter;
import de.hdmstuttgart.recipeapp.models.FullRecipe;
import de.hdmstuttgart.recipeapp.models.Ingredient;
import de.hdmstuttgart.recipeapp.models.Recipe;
import de.hdmstuttgart.recipeapp.models.Step;

public class RecipeRepository {

    private final FullRecipeDao recipeDao;
    private final LiveData<List<Recipe>> mAllRecipes;

    public RecipeRepository(Application application) {
        RecipeRoomDatabase database = RecipeRoomDatabase.getDatabase(application);
        recipeDao = database.fullRecipeDao();
        mAllRecipes = recipeDao.getAlphabetizedRecipes();
    }

    public void insertFullRecipe(FullRecipe fullRecipe) {
        RecipeRoomDatabase.sDatabaseWriteExecutor.execute(() -> {
            long identifier = recipeDao.insertRecipe(fullRecipe.recipe);

            for (Ingredient ingredient : fullRecipe.ingredients) {
                ingredient.setRecipeFK(identifier);
            }

            for (Step step : fullRecipe.steps) {
                step.setRecipeFK(identifier);
            }

            for (Filter filter : fullRecipe.filters) {
                filter.setRecipeFK(identifier);
            }

            // insert (entities) in all three tables
            recipeDao.insertIngredients(fullRecipe.ingredients);
            recipeDao.insertSteps(fullRecipe.steps);
            recipeDao.insertFilters(fullRecipe.filters);
        });
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mAllRecipes;
    }

    public LiveData<FullRecipe> getFullRecipeById(long id) {
        return recipeDao.getFullRecipeById(id);
    }

    public LiveData<List<Recipe>> getRecipesBySearchText(String searchText) {
        return recipeDao.getRecipesBySearchText(searchText);
    }


    public LiveData<List<Recipe>> getFavouriteRecipes() {
        return recipeDao.getFavouriteRecipes();
    }

    public LiveData<List<Recipe>> getRecentlyAddedRecipes() {
        return recipeDao.getRecentlyAddedRecipes();
    }

    public LiveData<List<Recipe>> getCategorieRecipes(EFilter eFilter) {
        return recipeDao.getCategorieRecipes(eFilter);
    }

    public void updateFavorite(long recipeID, boolean favorite) {
        RecipeRoomDatabase.sDatabaseWriteExecutor.execute(() ->
                recipeDao.updateFavorite(recipeID, favorite)
        );
    }

    public LiveData<List<Long>> getRecipeIDsByFilters(List<EFilter> filters) {
        return recipeDao.getRecipeIDsByFilters(filters, filters.size());
    }

    public LiveData<List<Recipe>> getRecipesByIDs(long[] ids) {
        return recipeDao.getRecipesByIDs(ids);
    }

    public void deleteRecipeByID(FullRecipe fullRecipe) {
        RecipeRoomDatabase.sDatabaseWriteExecutor.execute(() ->
                recipeDao.deleteRecipeByID(fullRecipe.recipe)
        );
    }
}
