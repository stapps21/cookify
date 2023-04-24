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
import de.hdmstuttgart.recipeapp.adapter.AddIngredientListAdapter;
import de.hdmstuttgart.recipeapp.databinding.DialogInputIngredientsBinding;
import de.hdmstuttgart.recipeapp.databinding.FragmentAddIngredientsBinding;
import de.hdmstuttgart.recipeapp.interfaces.IOnContinueClickListener;
import de.hdmstuttgart.recipeapp.models.Ingredient;
import de.hdmstuttgart.recipeapp.ui.newrecipe.SharedPageViewModel;
import de.hdmstuttgart.recipeapp.utils.SimplifiedTextWatcherOnTextChanged;


/**
 * A placeholder fragment containing a simple view.
 */
public class AddIngredientsFragment extends Fragment implements IOnContinueClickListener {

    // logging
    private static final String TAG = "AddIngredientsFragment";

    // general
    private FragmentAddIngredientsBinding mBinding;
    private SharedPageViewModel mSharedPageViewModel;

    // views
    private EditText etIngredientName, etAmount, etUnit;
    private Button dialogPositiveButton;

    // Dialog
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;

    // Adapter
    private AddIngredientListAdapter mAdapter;

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

        mBinding = FragmentAddIngredientsBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        // Input Dialog
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(root.getContext());

        // Set up the RecyclerView
        final RecyclerView rvIngredients = mBinding.rvIngredients;
        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new AddIngredientListAdapter(new AddIngredientListAdapter.IngredientDiff(), position -> {
            List<Ingredient> ingredients = new ArrayList<>(mAdapter.getCurrentList());
            ingredients.remove(position);
            mAdapter.submitList(ingredients);
        });
        rvIngredients.setAdapter(mAdapter);
        mAdapter.submitList(new ArrayList<>());

        Button btnAddIngr = mBinding.btnAddIngredient;
        btnAddIngr.setOnClickListener(v -> launchAlertDialog());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // DIALOG
    /////////////////////////////////////////////////////////////////////////

    private void launchAlertDialog() {
        // ViewBinding for dialog
        DialogInputIngredientsBinding dialogAddIngredientBinding = DialogInputIngredientsBinding.inflate(LayoutInflater.from(mBinding.getRoot().getContext()));

        // init views
        etIngredientName = dialogAddIngredientBinding.etIngredientName;
        etAmount = dialogAddIngredientBinding.etAmount;
        etUnit = dialogAddIngredientBinding.etUnit;

        // create dialog builder
        AlertDialog alertDialog = materialAlertDialogBuilder.setView(dialogAddIngredientBinding.getRoot())
                .setTitle(R.string.dialog_title_new_ingredient)
                .setPositiveButton(R.string.dialog_btn_add, (dialog, which) -> addIngredientToList())
                .setNegativeButton(R.string.dialog_btn_cancel, (dialog, which) -> dialog.dismiss()).create();

        // Override the standard behavior of the positive button => Just visible if all inputs were made
        alertDialog.setOnShowListener(dialog -> {
            AlertDialog alertDialog1 = ((AlertDialog) dialog);
            dialogPositiveButton = alertDialog1.getButton(AlertDialog.BUTTON_POSITIVE);
            dialogPositiveButton.setEnabled(false);
            etIngredientName.addTextChangedListener(simplifiedTextWatcherOnTextChanged);
            etAmount.addTextChangedListener(simplifiedTextWatcherOnTextChanged);
            etUnit.addTextChangedListener(simplifiedTextWatcherOnTextChanged);

            // Set focus on first edit text and open keyboard
            if (etIngredientName.requestFocus()) {
                alertDialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        alertDialog.show();
    }

    private void addIngredientToList() {
        List<Ingredient> ingredients = new ArrayList<>(mAdapter.getCurrentList());
        Ingredient ingredient = new Ingredient(
                etIngredientName.getText().toString().trim(),
                Float.parseFloat(etAmount.getText().toString().trim()),
                etUnit.getText().toString().trim()
        );
        ingredients.add(ingredient);
        mAdapter.submitList(ingredients);
    }

    SimplifiedTextWatcherOnTextChanged simplifiedTextWatcherOnTextChanged = new SimplifiedTextWatcherOnTextChanged() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = etIngredientName.getText().toString().trim();
            String sAmount = etAmount.getText().toString().trim();
            String unit = etUnit.getText().toString();

            // Parse String amount to type float
            float amount = sAmount.isEmpty() ? 0f : Float.parseFloat(sAmount);

            boolean buttonEnabled = !name.isEmpty() && amount != 0f && !unit.isEmpty();
            dialogPositiveButton.setEnabled(buttonEnabled);
        }
    };


    /////////////////////////////////////////////////////////////////////////
    // IOnContinueClickListener
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onClickContinue() {
        if (requiredFieldFilled()) {
            mSharedPageViewModel.setIngredients(mAdapter.getCurrentList());
            return true;
        }
        return false;
    }

    private boolean requiredFieldFilled() {
        if (mAdapter.getCurrentList().size() < 2) {
            Snackbar.make(mBinding.getRoot(), "Füge mindestens zwei Zutaten hinzu", Snackbar.LENGTH_LONG)
                    .setAction("Hinzufügen", v -> launchAlertDialog())
                    .show();
            return false;
        }

        return true;
    }
}