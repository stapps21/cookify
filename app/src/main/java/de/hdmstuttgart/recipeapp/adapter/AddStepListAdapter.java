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

import de.hdmstuttgart.recipeapp.databinding.ItemRecyclerviewAddStepBinding;
import de.hdmstuttgart.recipeapp.interfaces.IOnClickDeleteListener;
import de.hdmstuttgart.recipeapp.models.Step;

public class AddStepListAdapter extends ListAdapter<Step, AddStepListAdapter.StepViewHolder> {

    private final static String TAG = "AddStepListAdapter";
    private final IOnClickDeleteListener onClickDeleteListener;

    public AddStepListAdapter(@NonNull DiffUtil.ItemCallback<Step> diffCallback, IOnClickDeleteListener onClickDeleteListener) {
        super(diffCallback);
        this.onClickDeleteListener = onClickDeleteListener;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecyclerviewAddStepBinding itemBinding = ItemRecyclerviewAddStepBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StepViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        Step current = getItem(position);
        holder.bind(position + 1, current.getContent());
        holder.ibtnDelete.setOnClickListener(v -> onClickDeleteListener.onClickDelete(position));
    }

    /**
     * ViewHolder
     */
    static class StepViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvStepNumber, tvContent;
        private final ImageButton ibtnDelete;

        public StepViewHolder(ItemRecyclerviewAddStepBinding itemBinding) {
            super(itemBinding.getRoot());
            tvStepNumber = itemBinding.tvStepNumber;
            tvContent = itemBinding.tvStepContent;
            ibtnDelete = itemBinding.ibtnDelete;
        }

        public void bind(int stepNumber, String content) {
            tvStepNumber.setText(MessageFormat.format("{0}", stepNumber));
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

