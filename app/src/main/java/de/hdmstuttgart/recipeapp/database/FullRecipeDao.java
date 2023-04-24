package de.hdmstuttgart.recipeapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import de.hdmstuttgart.recipeapp.enums.EFilter;
import de.hdmstuttgart.recipeapp.models.Filter;
import de.hdmstuttgart.recipeapp.models.FullRecipe;
import de.hdmstuttgart.recipeapp.models.Ingredient;
import de.hdmstuttgart.recipeapp.models.Recipe;
import de.hdmstuttgart.recipeapp.models.Step;

@Dao
public interface FullRecipeDao {

    /////////////////////////////////////////////////////////////////////////
    // INSERT
    /////////////////////////////////////////////////////////////////////////

    @Insert
    long insertRecipe(Recipe recipe);

    @Insert
    void insertIngredients(List<Ingredient> ingredients);

    @Insert
    void insertSteps(List<Step> steps);

    @Insert
    void insertFilters(List<Filter> filters);

    /////////////////////////////////////////////////////////////////////////
    // READ
    /////////////////////////////////////////////////////////////////////////

    @Query("SELECT * FROM recipe_table ORDER BY LOWER(name) ASC")
    LiveData<List<Recipe>> getAlphabetizedRecipes();

    @Query("SELECT * FROM recipe_table WHERE LOWER(name) LIKE '%' || LOWER(:searchText) || '%' ORDER BY LOWER(name) ASC")
    LiveData<List<Recipe>> getRecipesBySearchText(String searchText);

    @Query("SELECT * FROM recipe_table WHERE favorite = '1'")
    LiveData<List<Recipe>> getFavouriteRecipes();

    @Query("SELECT * FROM recipe_table ORDER BY recipe_id DESC")
    LiveData<List<Recipe>> getRecentlyAddedRecipes();

    @Query("SELECT * FROM recipe_table WHERE recipe_id IN (SELECT recipe_fk FROM filter_table WHERE filter=:eFilter)")
    LiveData<List<Recipe>> getCategorieRecipes(EFilter eFilter);

    @Query("SELECT * FROM recipe_table WHERE recipe_id IN (:ids)")
    LiveData<List<Recipe>> getRecipesByIDs(long[] ids);

    @Transaction
    @Query("SELECT * FROM recipe_table WHERE recipe_id=:id LIMIT 1")
    LiveData<FullRecipe> getFullRecipeById(long id);


    @Query("SELECT DISTINCT recipe_fk FROM filter_table WHERE filter_table.filter IN (:filters) GROUP BY recipe_fk HAVING COUNT(DISTINCT filter) = :filterCount;")
    LiveData<List<Long>> getRecipeIDsByFilters(List<EFilter> filters, int filterCount);

    /////////////////////////////////////////////////////////////////////////
    // UPDATE
    /////////////////////////////////////////////////////////////////////////

    @Query("UPDATE recipe_table SET favorite=:favorite WHERE recipe_id=:recipeID")
    void updateFavorite(long recipeID, boolean favorite);

    /////////////////////////////////////////////////////////////////////////
    // DELETE
    /////////////////////////////////////////////////////////////////////////

    @Transaction
    @Query("DELETE FROM recipe_table")
    void deleteAll();

    @Delete
    void deleteRecipeByID(Recipe recipe);
}
