package de.hdmstuttgart.recipeapp.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.hdmstuttgart.recipeapp.enums.EDifficulty;

/**
 * Includes all the information about a recipe
 * All ingredients / amount / cooking steps / ...
 */
@Entity(tableName = "recipe_table")
public class Recipe {

    @ColumnInfo(name = "description")
    private final String mDescription;
    @ColumnInfo(name = "time")
    private final int mTime;
    @ColumnInfo(name = "serves")
    private final int mServes;
    @ColumnInfo(name = "favorite")
    private final boolean mFavorite;
    @Nullable
    @ColumnInfo(name = "image_name")
    private final String mImageName;
    @NonNull
    @ColumnInfo(name = "name")
    private final String mName;
    @ColumnInfo(name = "difficulty")
    private final EDifficulty mDifficulty;
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recipe_id")
    public long id;


    public Recipe(String imageName, @NonNull String name, String description, int time, EDifficulty difficulty, int serves, boolean favorite) {
        this.mImageName = imageName;
        this.mName = name;
        this.mDescription = description;
        this.mDifficulty = difficulty;
        this.mTime = time;
        this.mServes = serves;
        this.mFavorite = favorite;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getTime() {
        return this.mTime;
    }

    public int getServes() {
        return this.mServes;
    }

    public boolean getFavorite() {
        return this.mFavorite;
    }

    public long getId() {
        return this.id;
    }

    public String getImageName() {
        return this.mImageName;
    }

    public String getName() {
        return this.mName;
    }

    public EDifficulty getDifficulty() {
        return this.mDifficulty;
    }
}

