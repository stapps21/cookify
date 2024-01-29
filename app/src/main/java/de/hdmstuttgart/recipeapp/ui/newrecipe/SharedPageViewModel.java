package de.hdmstuttgart.recipeapp.ui.newrecipe;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import de.hdmstuttgart.recipeapp.enums.EDifficulty;
import de.hdmstuttgart.recipeapp.models.Filter;
import de.hdmstuttgart.recipeapp.models.Ingredient;
import de.hdmstuttgart.recipeapp.models.Step;

public class SharedPageViewModel extends ViewModel {

    private String mName, mDescription;
    private int mTime;
    private int mServes;
    private EDifficulty mDifficulty;

    private List<Ingredient> mIngredients = new ArrayList<>();
    private List<Step> mSteps = new ArrayList<>();
    private final List<Filter> mFilters = new ArrayList<>();

    private IMAGE_SOURCE mImageSource;
    public enum IMAGE_SOURCE {
        NONE,
        CAMERA,
        GALLERY
    }

    public IMAGE_SOURCE getImageSource() {
        return mImageSource;
    }

    public void setImageSource(IMAGE_SOURCE imageSource) {
        this.mImageSource = imageSource;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int time) {
        this.mTime = time;
    }

    public List<Ingredient> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.mIngredients = ingredients;
    }

    public List<Step> getSteps() {
        return mSteps;
    }

    public void setSteps(List<Step> steps) {
        this.mSteps = steps;
    }

    public List<Filter> getFilters() {
        return mFilters;
    }

    public void addFilter(Filter filter) {
        mFilters.add(filter);
    }

    public void removeFilter(Filter filter) {
        mFilters.removeIf(f -> f.getFilter() == filter.getFilter());
    }

    public int getServes() {
        return mServes;
    }

    public void setServes(int serves) {
        this.mServes = serves;
    }

    public EDifficulty getDifficulty() {
        return mDifficulty;
    }

    public void setDifficulty(EDifficulty difficulty) {
        this.mDifficulty = difficulty;
    }

}