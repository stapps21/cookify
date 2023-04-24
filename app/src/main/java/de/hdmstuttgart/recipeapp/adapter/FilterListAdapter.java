package de.hdmstuttgart.recipeapp.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.MessageFormat;

import de.hdmstuttgart.recipeapp.R;
import de.hdmstuttgart.recipeapp.databinding.ItemRecyclerviewGridFilterBinding;
import de.hdmstuttgart.recipeapp.enums.EDifficulty;
import de.hdmstuttgart.recipeapp.interfaces.IAdapterItemClickListener;
import de.hdmstuttgart.recipeapp.models.Recipe;
import de.hdmstuttgart.recipeapp.utils.RecipeImageHelper;


public class FilterListAdapter extends ListAdapter<Recipe, FilterListAdapter.HomeViewHolder> {

    IAdapterItemClickListener mItemClickListener;


    public FilterListAdapter(@NonNull DiffUtil.ItemCallback<Recipe> diffCallback, IAdapterItemClickListener mItemClickListener) {
        super(diffCallback);
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public FilterListAdapter.HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecyclerviewGridFilterBinding itemBinding = ItemRecyclerviewGridFilterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FilterListAdapter.HomeViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Recipe current = getItem(position);
        RecipeImageHelper rimgHelper = new RecipeImageHelper(holder.itemView.getContext());
        holder.bind(rimgHelper.getImageUri(current.getImageName(), true), current.getFavorite(), current.getName(), current.getTime() + "", current.getDifficulty());
        holder.itemView.setOnClickListener(view -> mItemClickListener.onItemClicked(current.id));
    }


    /**
     * ViewHolder
     */
    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivRecipeThumbnail;
        private final TextView tvRecipeName, tvSubcontent, tvDifficulty;
        private final ImageView ivFavorite;

        public HomeViewHolder(ItemRecyclerviewGridFilterBinding itemBinding) {
            super(itemBinding.getRoot());
            ivRecipeThumbnail = itemBinding.ivPreview;
            tvRecipeName = itemBinding.tvName;
            tvSubcontent = itemBinding.tvTime;
            tvDifficulty = itemBinding.tvDifficulty;
            ivFavorite = itemBinding.ivFavorite;
        }

        public void bind(Uri imgURI, boolean favorite, String recipeName, String time, EDifficulty difficulty) {
            // Load image from uri if exists, if does not exist use default background image
            if (imgURI != Uri.EMPTY) {
                ivRecipeThumbnail.setImageURI(imgURI);
            } else {
                ivRecipeThumbnail.setImageResource(R.drawable.default_image);
            }
            tvRecipeName.setText(recipeName);
            ivFavorite.setVisibility(favorite ? View.VISIBLE : View.GONE);
            tvSubcontent.setText(MessageFormat.format("{0} min • {1}", time, tvSubcontent.getContext().getString(difficulty.getNameRes())));
        }
    }

    /**
     * DiffUtil Callback
     */
    public static class RecipeDiff extends DiffUtil.ItemCallback<Recipe> {

        @Override
        public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    }
}


