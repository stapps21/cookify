package de.hdmstuttgart.recipeapp.enums;

import androidx.annotation.StringRes;

import de.hdmstuttgart.recipeapp.R;
import de.hdmstuttgart.recipeapp.interfaces.IFilterEnum;

public enum EFilter implements IFilterEnum {

    START_MEALS_ENUM(R.string.filter_category_meals),
    BREAKFAST(R.string.meals_breakfast),
    LUNCH(R.string.meals_lunch),
    DINNER(R.string.meals_dinner),
    SNACK(R.string.meals_snack),
    DESSERT(R.string.meals_dessert),
    SALAD(R.string.meals_salad),
    SHAKE(R.string.meals_shake),
    SOUP(R.string.meals_soup),
    SMOOTHIE(R.string.meals_smoothie),
    ALC_MIX(R.string.meals_alc_mixes),
    END_MEALS_ENUM(-1),

    START_DIETS_ENUM(R.string.filter_category_diets),
    VEGETARIAN(R.string.diets_vegetarian),
    VEGAN(R.string.diets_vegan),
    PESCETARIAN(R.string.diets_pescetarian),
    LACTOSE_FREE(R.string.diets_lactose_free),
    GLUTEN_FREE(R.string.diets_gluten_free),
    LOW_CARB(R.string.diets_low_carb),
    HIGH_PROTEIN(R.string.diets_high_protein),
    END_DIETS_ENUM(-1);


    @StringRes
    private final int mNameRes;

    EFilter(@StringRes int nameRes) {
        this.mNameRes = nameRes;
    }

    @Override
    @StringRes
    public int getNameRes() {
        return mNameRes;
    }
}
