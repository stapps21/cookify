package de.hdmstuttgart.recipeapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "step_table",
        foreignKeys = {
                @ForeignKey(entity = Recipe.class,
                        parentColumns = "recipe_id",
                        childColumns = "recipe_fk",
                        onDelete = ForeignKey.CASCADE)
        })
public class Step {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "step_id")
    public long mId;
    @ColumnInfo(name = "step")
    public int mStep;
    @ColumnInfo(name = "content")
    public String mContent;
    @ColumnInfo(name = "recipe_fk", index = true)
    private long mRecipeFK;

    public Step(int step, String content) {
        this.mStep = step;
        this.mContent = content;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        this.mStep = step;
    }

    public long getRecipeFK() {
        return mRecipeFK;
    }

    public void setRecipeFK(long recipeFK) {
        this.mRecipeFK = recipeFK;
    }
}
