package de.hdmstuttgart.recipeapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import de.hdmstuttgart.recipeapp.databinding.ItemRecyclerviewStepsBinding;
import de.hdmstuttgart.recipeapp.models.Step;

public class StepListAdapter extends ListAdapter<Step, StepListAdapter.StepViewHolder> {

    private final static String TAG = "StepListAdapter";

    public StepListAdapter(@NonNull DiffUtil.ItemCallback<Step> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecyclerviewStepsBinding itemBinding = ItemRecyclerviewStepsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StepViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        Step current = getItem(position);
        holder.bind(position + 1, current.getContent());
    }

    /**
     * ViewHolder
     */
    static class StepViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNumber, tvContent;

        public StepViewHolder(ItemRecyclerviewStepsBinding itemBinding) {
            super(itemBinding.getRoot());
            tvNumber = itemBinding.tvStepNumber;
            tvContent = itemBinding.tvStepContent;
        }

        public void bind(int number, String content) {
            tvNumber.setText(String.valueOf(number));
            tvContent.setText(content);
        }
    }

    /**
     * DiffUtil Callback
     */
    public static class StepDiff extends DiffUtil.ItemCallback<Step> {

        @Override
        public boolean areItemsTheSame(@NonNull Step oldItem, @NonNull Step newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Step oldItem, @NonNull Step newItem) {
            return oldItem.getContent().equals(newItem.getContent());
        }
    }
}

