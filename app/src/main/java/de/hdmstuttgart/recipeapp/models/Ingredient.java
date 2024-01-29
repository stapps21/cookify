package de.hdmstuttgart.recipeapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredient_table",
        foreignKeys = {
                @ForeignKey(entity = Recipe.class,
                        parentColumns = "recipe_id",
                        childColumns = "recipe_fk",
                        onDelete = ForeignKey.CASCADE)
        })
public class Ingredient {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ingredient_id")
    public long mId;
    @ColumnInfo(name = "name")
    public String mName;
    @ColumnInfo(name = "amount")
    public float mAmount;
    @ColumnInfo(name = "unit")
    public String mUnit;
    @ColumnInfo(name = "recipe_fk", index = true)
    private long mRecipeFK;

    public Ingredient(String name, float amount, String unit) {
        this.mName = name;
        this.mAmount = amount;
        this.mUnit = unit;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public float getAmount() {
        return mAmount;
    }

    public void setAmount(float amount) {
        this.mAmount = amount;
    }

    public String getUnit() {
        return mUnit;
    }

    public void setUnit(String unit) {
        this.mUnit = unit;
    }

    public long getRecipeFK() {
        return mRecipeFK;
    }

    public void setRecipeFK(long recipeFK) {
        this.mRecipeFK = recipeFK;
    }
}
