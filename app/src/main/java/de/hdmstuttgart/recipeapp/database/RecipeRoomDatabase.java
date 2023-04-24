package de.hdmstuttgart.recipeapp.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdmstuttgart.recipeapp.enums.EDifficulty;
import de.hdmstuttgart.recipeapp.enums.EFilter;
import de.hdmstuttgart.recipeapp.models.Filter;
import de.hdmstuttgart.recipeapp.models.Ingredient;
import de.hdmstuttgart.recipeapp.models.Recipe;
import de.hdmstuttgart.recipeapp.models.Step;

@Database(entities = {Recipe.class, Ingredient.class, Step.class, Filter.class}, version = 1, exportSchema = false)
public abstract class RecipeRoomDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService sDatabaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile RecipeRoomDatabase INSTANCE;

    /**
     * Override the onCreate method to populate the database.
     * For this sample, we clear the database every time it is created.
     */
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            sDatabaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                FullRecipeDao dao = INSTANCE.fullRecipeDao();
                dao.deleteAll(); // TODO: remove on deployment

                // Placeholder Recipes // TODO: remove on deployment
                List<Ingredient> ingredients = new ArrayList<>();
                ingredients.add(new Ingredient("Ingredient", 1, "kg"));
                ingredients.add(new Ingredient("Ingredient", 750, "g"));
                ingredients.add(new Ingredient("Ingredient", 1, "L"));

                List<Step> steps = new ArrayList<>();
                steps.add(new Step(1, "Step 1"));
                steps.add(new Step(2, "Step 2"));
                steps.add(new Step(3, "Step 3"));

                List<Filter> filters = new ArrayList<>();
                filters.add(new Filter(EFilter.LUNCH));
                filters.add(new Filter(EFilter.VEGAN));
                filters.add(new Filter(EFilter.VEGETARIAN));

                List<Recipe> recipes = new ArrayList<>();
                recipes.add(new Recipe(null, "Recipe Name", "This is a short description of the recipe", 20, EDifficulty.MEDIUM, 2, true));
                recipes.add(new Recipe(null, "Recipe Name", "This is a short description of the recipe", 20, EDifficulty.MEDIUM, 2, true));
                recipes.add(new Recipe(null, "Recipe Name", "This is a short description of the recipe", 20, EDifficulty.MEDIUM, 2, true));


                for (Recipe recipe : recipes) {
                    long id = dao.insertRecipe(recipe);
                    for (Ingredient ingredient : ingredients) {
                        ingredient.setRecipeFK(id);
                    }
                    for (Step step : steps) {
                        step.setRecipeFK(id);
                    }
                    for (Filter step : filters) {
                        step.setRecipeFK(id);
                    }
                    dao.insertIngredients(ingredients);
                    dao.insertSteps(steps);
                    dao.insertFilters(filters);
                }
            });
        }
    };

    static RecipeRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecipeRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    RecipeRoomDatabase.class, "recipe_database")
                            //.addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract FullRecipeDao fullRecipeDao();
}
