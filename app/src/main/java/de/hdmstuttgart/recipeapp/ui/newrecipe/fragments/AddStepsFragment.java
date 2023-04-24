package de.hdmstuttgart.recipeapp.ui.newrecipe.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import de.hdmstuttgart.recipeapp.R;
import de.hdmstuttgart.recipeapp.adapter.AddStepListAdapter;
import de.hdmstuttgart.recipeapp.databinding.DialogInputStepsBinding;
import de.hdmstuttgart.recipeapp.databinding.FragmentAddStepsBinding;
import de.hdmstuttgart.recipeapp.interfaces.IOnContinueClickListener;
import de.hdmstuttgart.recipeapp.models.Step;
import de.hdmstuttgart.recipeapp.ui.newrecipe.SharedPageViewModel;
import de.hdmstuttgart.recipeapp.utils.SimplifiedTextWatcherOnTextChanged;


/**
 * A placeholder fragment containing a simple view.
 */
public class AddStepsFragment extends Fragment implements IOnContinueClickListener {

    private static final String TAG = "AddStepsFragment";

    private FragmentAddStepsBinding mBinding;
    private SharedPageViewModel mSharedPageViewModel;

    // Dialog builder
    private MaterialAlertDialogBuilder mMaterialAlertDialogBuilder;

    // Views
    private EditText metStep;
    private Button mDialogPositiveButton;

    // Adapter
    private AddStepListAdapter mAdapter;

    /////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    /////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPageViewModel = new ViewModelProvider(requireActivity()).get(SharedPageViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mBinding = FragmentAddStepsBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        // init dialog builder
        mMaterialAlertDialogBuilder = new MaterialAlertDialogBuilder(root.getContext());

        // Setup
        setupStepsRecyclerview();

        // button open dialog
        mBinding.btnAddIngredient.setOnClickListener(v -> launchAlertDialog());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // SETUP
    /////////////////////////////////////////////////////////////////////////

    private void setupStepsRecyclerview() {
        final RecyclerView mrvSearchResults = mBinding.rvSteps;
        mrvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new AddStepListAdapter(new AddStepListAdapter.StepDiff(), position -> {
            List<Step> steps = new ArrayList<>(mAdapter.getCurrentList());
            steps.remove(position);
            mAdapter.submitList(steps);
        });
        mrvSearchResults.setAdapter(mAdapter);
        mAdapter.submitList(new ArrayList<>());
    }

    /////////////////////////////////////////////////////////////////////////
    // DIALOG
    /////////////////////////////////////////////////////////////////////////

    private void launchAlertDialog() {
        // ViewBinding for dialog
        DialogInputStepsBinding dialogAddIngredientBinding = DialogInputStepsBinding.inflate(LayoutInflater.from(mBinding.getRoot().getContext()));

        metStep = dialogAddIngredientBinding.etStep;

        // Create Dialog
        AlertDialog alertDialog = mMaterialAlertDialogBuilder.setView(dialogAddIngredientBinding.getRoot())
                .setTitle(R.string.dialog_title_new_step)
                .setPositiveButton(R.string.dialog_btn_add, (dialog, which) -> addStepToList())
                .setNegativeButton(R.string.dialog_btn_cancel, (dialog, which) -> dialog.dismiss()).create();


        // Override the standard behavior of the positive button => Just visible if all inputs were made
        alertDialog.setOnShowListener(dialog -> {
            AlertDialog alertDialog1 = ((AlertDialog) dialog);
            mDialogPositiveButton = alertDialog1.getButton(AlertDialog.BUTTON_POSITIVE);
            mDialogPositiveButton.setEnabled(false);
            metStep.addTextChangedListener(mSimplifiedTextWatcherOnTextChanged);

            // Set focus on Edittext and open Keyboard
            if (metStep.requestFocus()) {
                alertDialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        alertDialog.show();
    }

    /////////////////////////////////////////////////////////////////////////
    // HELPER METHODS
    /////////////////////////////////////////////////////////////////////////

    private void addStepToList() {
        List<Step> steps = new ArrayList<>(mAdapter.getCurrentList());
        Step step = new Step(mAdapter.getCurrentList().size() + 1, metStep.getText().toString().trim());
        steps.add(step);
        mAdapter.submitList(steps);
    }

    /////////////////////////////////////////////////////////////////////////
    // LISTENER
    /////////////////////////////////////////////////////////////////////////

    SimplifiedTextWatcherOnTextChanged mSimplifiedTextWatcherOnTextChanged = new SimplifiedTextWatcherOnTextChanged() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = metStep.getText().toString().trim();
            mDialogPositiveButton.setEnabled(!name.isEmpty());
        }
    };

    /////////////////////////////////////////////////////////////////////////
    // IOnContinueClickListener - required fields check
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onClickContinue() {
        if (requiredFieldFilled()) {
            mSharedPageViewModel.setSteps(mAdapter.getCurrentList());
            return true;
        }
        return false;
    }

    private boolean requiredFieldFilled() {
        if (mAdapter.getCurrentList().size() < 1) {
            Snackbar.make(mBinding.getRoot(), "Füge mindestens einen Schritt hinzu", Snackbar.LENGTH_LONG)
                    .setAction("Hinzufügen", v -> launchAlertDialog())
                    .show();
            return false;
        }

        return true;
    }
}