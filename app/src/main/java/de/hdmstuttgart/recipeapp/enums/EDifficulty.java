package de.hdmstuttgart.recipeapp.enums;

import androidx.annotation.StringRes;

import de.hdmstuttgart.recipeapp.R;

public enum EDifficulty {

    EASY(R.string.difficulty_easy),
    MEDIUM(R.string.difficulty_medium),
    HARD(R.string.difficulty_hard);

    @StringRes
    private final int mNameRes;

    EDifficulty(@StringRes int nameRes) {
        this.mNameRes = nameRes;
    }

    @StringRes
    public int getNameRes() {
        return mNameRes;
    }

}
