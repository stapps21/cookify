package de.hdmstuttgart.recipeapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.MessageFormat;

import de.hdmstuttgart.recipeapp.databinding.ItemRecyclerviewIngredientsBinding;
import de.hdmstuttgart.recipeapp.models.Ingredient;

public class IngredientListAdapter extends ListAdapter<Ingredient, IngredientListAdapter.IngredientViewHolder> {

    private final static String TAG = "IngredientListAdapter";

    public IngredientListAdapter(@NonNull DiffUtil.ItemCallback<Ingredient> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecyclerviewIngredientsBinding itemBinding = ItemRecyclerviewIngredientsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new IngredientViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        Ingredient current = getItem(position);
        holder.bind(current.getName(), current.getAmount(), current.getUnit());
        Log.d(TAG, "onBindViewHolder: currentAmount(" + current.getAmount() + ")");
    }

    /**
     * ViewHolder
     */
    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvIngredient, tvAmount;

        public IngredientViewHolder(ItemRecyclerviewIngredientsBinding itemBinding) {
            super(itemBinding.getRoot());
            tvIngredient = itemBinding.tvIngredient;
            tvAmount = itemBinding.tvAmount;
        }

        public void bind(String name, float amount, String unit) {
            tvIngredient.setText(name);
            tvAmount.setText(MessageFormat.format("{0} {1}", amount, unit));
        }
    }

    /**
     * DiffUtil Callback
     */
    public static class IngredientDiff extends DiffUtil.ItemCallback<Ingredient> {

        @Override
        public boolean areItemsTheSame(@NonNull Ingredient oldItem, @NonNull Ingredient newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Ingredient oldItem, @NonNull Ingredient newItem) {
            return oldItem.getAmount() == newItem.getAmount() && oldItem.getUnit().equals(newItem.getUnit());
        }
    }
}

