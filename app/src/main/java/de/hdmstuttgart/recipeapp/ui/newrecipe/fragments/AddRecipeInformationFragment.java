package de.hdmstuttgart.recipeapp.ui.newrecipe.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.hdmstuttgart.recipeapp.databinding.FragmentAddRecipeInformationBinding;
import de.hdmstuttgart.recipeapp.enums.EDifficulty;
import de.hdmstuttgart.recipeapp.exceptions.InvalidDifficultyException;
import de.hdmstuttgart.recipeapp.interfaces.IOnContinueClickListener;
import de.hdmstuttgart.recipeapp.ui.newrecipe.SharedPageViewModel;


/**
 * A placeholder fragment containing a simple view.
 */
public class AddRecipeInformationFragment extends Fragment implements IOnContinueClickListener {

    private SharedPageViewModel sharedPageViewModel;
    private FragmentAddRecipeInformationBinding binding;
    private EditText etName, etDescription, etTime, etServes;
    private AutoCompleteTextView etDifficulty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPageViewModel = new ViewModelProvider(requireActivity()).get(SharedPageViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentAddRecipeInformationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        etName = binding.etName;
        etDescription = binding.etDescription;
        etTime = binding.etTime;
        etDifficulty = binding.etDifficulty;
        etServes = binding.etServes;

        List<String> enumValues = Arrays.stream(EDifficulty.values()).map(difficulty -> getResources().getString(difficulty.getNameRes())).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, enumValues);
        etDifficulty.setAdapter(adapter);
        etDifficulty.setOnItemClickListener((parent, view, position, id) -> {
            etDifficulty.setError(null);
        });
        etDifficulty.setOnFocusChangeListener((v, hasFocus) -> {
            Activity activity = getActivity();
            if (activity == null) return;

            // Close SoftKeyboardInput
            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });

        return root;
    }

    private EDifficulty getSelectedDifficulty() throws InvalidDifficultyException {
        final String difficultyString = etDifficulty.getText().toString().trim();

        EDifficulty difficulty = Arrays.stream(EDifficulty.values())
                .filter(d -> getResources().getString(d.getNameRes()).equals(difficultyString))
                .findFirst()
                .orElse(null);

        if (difficulty == null) {
            throw new InvalidDifficultyException(difficultyString);
        }

        return difficulty;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public boolean onClickContinue() {
        if (requiredFieldFilled()) {
            sharedPageViewModel.setName(etName.getText().toString().trim());
            sharedPageViewModel.setDescription(etDescription.getText().toString().trim());
            int time = etTime.getText().toString().isEmpty() ?
                    0 : Integer.parseInt(etTime.getText().toString());
            sharedPageViewModel.setTime(time);
            sharedPageViewModel.setDifficulty(getSelectedDifficulty());
            int serves = etServes.getText().toString().isEmpty() ?
                    0 : Integer.parseInt(etServes.getText().toString());
            sharedPageViewModel.setServes(serves);
            return true;
        }
        return false;
    }

    private boolean requiredFieldFilled() {
        boolean requiredFieldsFilled = true;

        String name = etName.getText().toString().trim();
        if (name.length() < 3 || name.length() > 50) {
            requiredFieldsFilled = false;
            etName.setError("Der Rezeptname muss zwischen 3 und 50 Zeichen haben.");
        }

        boolean isTimeEmpty = etTime.getText().toString().isEmpty();
        if (isTimeEmpty) {
            requiredFieldsFilled = false;
            etTime.setError("Bitte geben eine Zeit an.");
        }

        boolean isDifficultyEmpty = etDifficulty.getText().toString().trim().isEmpty();
        if (isDifficultyEmpty) {
            requiredFieldsFilled = false;
            etDifficulty.setError("Bitte wähle eine Schwierigkeit aus.");
        }

        int serves = etServes.getText().toString().isEmpty() ? 0 : Integer.parseInt(etServes.getText().toString());
        if (serves <= 0) {
            requiredFieldsFilled = false;
            etServes.setError("Bitte geben eine Zahl größer als 1 an.");
        }

        return requiredFieldsFilled;
    }
}