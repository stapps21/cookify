package de.hdmstuttgart.recipeapp.ui.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import de.hdmstuttgart.recipeapp.R;
import de.hdmstuttgart.recipeapp.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private final static String TAG = "SettingsActivity";

    private ActivitySettingsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setupToolbar();

        // Inflate Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // SETUP
    /////////////////////////////////////////////////////////////////////////

    private void setupToolbar() {
        Toolbar toolbar = mBinding.topToolBar;
        setSupportActionBar(toolbar);
        mBinding.topAppBarLayout.addLiftOnScrollListener(((elevation, backgroundColor) -> getWindow().setStatusBarColor(backgroundColor)));

    }

    /////////////////////////////////////////////////////////////////////////
    // ON BACK NAVIGATION
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /////////////////////////////////////////////////////////////////////////
    // SETTINGS FRAGMENT | static
    /////////////////////////////////////////////////////////////////////////

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // Darkmode / Lightmode
            ListPreference themePreference = findPreference("theme");
            if (themePreference != null) {
                switchTheme(themePreference);
            }
        }

        /**
         * Change the theme to light / darkmode
         * @param themePref preference which has storage the theme
         */
        private void switchTheme(ListPreference themePref) {
            themePref.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue.equals("dark")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else if (newValue.equals("light")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                return true;
            });
        }
    }
}