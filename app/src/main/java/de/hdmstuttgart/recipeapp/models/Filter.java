package de.hdmstuttgart.recipeapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import de.hdmstuttgart.recipeapp.enums.EFilter;

@Entity(tableName = "filter_table",
        foreignKeys = {
                @ForeignKey(entity = Recipe.class,
                        parentColumns = "recipe_id",
                        childColumns = "recipe_fk",
                        onDelete = ForeignKey.CASCADE)
        })
public class Filter {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "filter_id")
    public long mId;
    @ColumnInfo(name = "filter")
    public EFilter mFilter;
    @ColumnInfo(name = "recipe_fk", index = true)
    private long mRecipeFK;


    public Filter(EFilter filter) {
        this.mFilter = filter;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public EFilter getFilter() {
        return mFilter;
    }

    public void setFilter(EFilter filter) {
        this.mFilter = filter;
    }

    public long getRecipeFK() {
        return mRecipeFK;
    }

    public void setRecipeFK(long recipeFK) {
        this.mRecipeFK = recipeFK;
    }
}
