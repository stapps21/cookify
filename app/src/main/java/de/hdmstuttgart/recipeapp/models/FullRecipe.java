package de.hdmstuttgart.recipeapp.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;


public class FullRecipe {
    @Embedded
    public Recipe recipe;

    @Relation(
            parentColumn = "recipe_id",
            entityColumn = "recipe_fk"
    )
    public List<Ingredient> ingredients;

    @Relation(
            parentColumn = "recipe_id",
            entityColumn = "recipe_fk"
    )
    public List<Step> steps;

    @Relation(
            parentColumn = "recipe_id",
            entityColumn = "recipe_fk"
    )
    public List<Filter> filters;

    public FullRecipe(Recipe recipe, List<Ingredient> ingredients, List<Step> steps, List<Filter> filters) {
        this.recipe = recipe;
        this.ingredients = ingredients;
        this.steps = steps;
        this.filters = filters;
    }
}
