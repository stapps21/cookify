package de.hdmstuttgart.recipeapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.MessageFormat;

import de.hdmstuttgart.recipeapp.databinding.ItemRecyclerviewAddIngredientsBinding;
import de.hdmstuttgart.recipeapp.interfaces.IOnClickDeleteListener;
import de.hdmstuttgart.recipeapp.models.Ingredient;

public class AddIngredientListAdapter extends ListAdapter<Ingredient, AddIngredientListAdapter.IngredientViewHolder> {

    private final static String TAG = "AddIngredientListAdapter";
    private final IOnClickDeleteListener onClickDeleteListener;

    public AddIngredientListAdapter(@NonNull DiffUtil.ItemCallback<Ingredient> diffCallback, IOnClickDeleteListener onClickDeleteListener) {
        super(diffCallback);
        this.onClickDeleteListener = onClickDeleteListener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecyclerviewAddIngredientsBinding itemBinding = ItemRecyclerviewAddIngredientsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new IngredientViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        IngredientViewHolder ingredientViewHolder = holder;
        Ingredient current = getItem(position);
        holder.bind(current);

        holder.ibtnDelete.setOnClickListener(v -> {
            onClickDeleteListener.onClickDelete(position);
        });
    }

    /**
     * ViewHolder
     */
    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvIngredientInfo;
        private final ImageButton ibtnDelete;

        public IngredientViewHolder(ItemRecyclerviewAddIngredientsBinding itemBinding) {
            super(itemBinding.getRoot());
            tvIngredientInfo = itemBinding.tvIngredientInfo;
            ibtnDelete = itemBinding.ibtnDelete;
        }

        public void bind(Ingredient ingredient) {
            tvIngredientInfo.setText(MessageFormat.format("{0} ({1} {2})", ingredient.getName(), ingredient.getAmount(), ingredient.getUnit()));
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
            return oldItem.getAmount() == newItem.getAmount()
                    && oldItem.getUnit().equals(newItem.getUnit())
                    && oldItem.getName().equals(newItem.getName());
        }
    }
}

