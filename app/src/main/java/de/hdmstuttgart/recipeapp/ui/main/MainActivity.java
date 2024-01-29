package de.hdmstuttgart.recipeapp.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import de.hdmstuttgart.recipeapp.R;
import de.hdmstuttgart.recipeapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private ActivityMainBinding mBinding;

    /////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    /////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dakmode / Lightmode (load from settings)
        handleTheme();

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setupToolbar();
        setupNavigationUI(setupBottomNavBarConfiguration());
    }

    /////////////////////////////////////////////////////////////////////////
    // SETUP
    /////////////////////////////////////////////////////////////////////////

    private void setupToolbar() {
        Toolbar toolbar = mBinding.topToolBar;
        setSupportActionBar(toolbar);
        mBinding.topAppBarLayout.addLiftOnScrollListener(((elevation, backgroundColor) -> getWindow().setStatusBarColor(backgroundColor)));
    }

    private void setupNavigationUI(AppBarConfiguration appBarConfiguration) {
        try {
            NavHostFragment navHostController = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            NavController navController = navHostController.getNavController();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(mBinding.bottomNavBar, navController);
        } catch (NullPointerException | IllegalStateException e) {
            // This should never be executed
            Log.wtf(TAG, "NavController is null!", e);
        }
    }

    private AppBarConfiguration setupBottomNavBarConfiguration() {
        return new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_library)
                .build();
    }


    /////////////////////////////////////////////////////////////////////////
    // HANDLE THEME
    /////////////////////////////////////////////////////////////////////////

    private void handleTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("theme", null);

        if (theme != null) {
            Log.d("MainActivity", "onCreatePreference: themePreference - " + theme);
            if (theme.equals("dark")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (theme.equals("light")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}