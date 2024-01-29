package de.hdmstuttgart.recipeapp.ui.recipedetail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import de.hdmstuttgart.recipeapp.database.RecipeRepository;
import de.hdmstuttgart.recipeapp.models.FullRecipe;

public class RecipeDetailViewModel extends AndroidViewModel {

    private final static String TAG = "RecipeDetailViewModel";

    private final long mID;
    private final RecipeRepository mRepository;
    private final LiveData<FullRecipe> mRecipe;


    public RecipeDetailViewModel(Application application, long id) {
        super(application);
        mID = id;
        mRepository = new RecipeRepository(application);
        mRecipe = mRepository.getFullRecipeById(id);
    }

    LiveData<FullRecipe> getRecipe() {
        return mRecipe;
    }

    void updateFavorite(boolean favorite) {
        mRepository.updateFavorite(mID, favorite);
    }

    void deleteRecipe(FullRecipe fullRecipe) {
        mRepository.deleteRecipeByID(fullRecipe);
    }


    /**
     * A factory is used to inject the recipe ID into the ViewModel
     */
    static class Factory implements ViewModelProvider.Factory {
        private final Application mApplication;
        private final long mParam;


        public Factory(Application application, long param) {
            mApplication = application;
            mParam = param;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new RecipeDetailViewModel(mApplication, mParam);
        }
    }
}
