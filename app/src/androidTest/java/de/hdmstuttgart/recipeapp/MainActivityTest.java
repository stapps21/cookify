package de.hdmstuttgart.recipeapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.Menu;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.hdmstuttgart.recipeapp.ui.main.MainActivity;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void bottomNavBarSetupTest() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_nav_bar);
            assertNotNull(bottomNavigationView);

            Menu menu = bottomNavigationView.getMenu();
            assertNotNull(menu);

            assertEquals(3, menu.size());
            assertNotNull(menu.findItem(R.id.navigation_home));
            assertNotNull(menu.findItem(R.id.navigation_search));
            assertNotNull(menu.findItem(R.id.navigation_library));
        });
    }
}
