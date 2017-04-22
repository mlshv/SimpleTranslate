package me.mlshv.simpletranslate.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.ui.fragments.FavoritesFragment;
import me.mlshv.simpletranslate.ui.fragments.HistoryFragment;
import me.mlshv.simpletranslate.ui.fragments.SettingsFragment;
import me.mlshv.simpletranslate.ui.fragments.TranslateFragment;
import me.mlshv.simpletranslate.util.BottomNavigationViewHelper;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigation;

    private TranslateFragment translateFragment;
    private HistoryFragment historyFragment;
    private FavoritesFragment favoritesFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        translateFragment = new TranslateFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container, translateFragment)
                .commit();
        initBottomNavigation();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_translate:
                    if (translateFragment == null)
                        translateFragment = new TranslateFragment();
                    goToFragment(translateFragment);
                    return true;
                case R.id.navigation_history:
                    if (historyFragment == null)
                        historyFragment = new HistoryFragment();
                    goToFragment(historyFragment);
                    return true;
                case R.id.navigation_favorites:
                    if (favoritesFragment == null)
                        favoritesFragment = new FavoritesFragment();
                    goToFragment(favoritesFragment);
                    return true;
                case R.id.navigation_settings:
                    if (settingsFragment == null)
                        settingsFragment = new SettingsFragment();
                    goToFragment(settingsFragment);
                    return true;
            }
            return false;
        }

    };

    private void goToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    private void initBottomNavigation() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.removeShiftMode(navigation);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void goToTranslationFragment() {
        if (translateFragment == null)
            translateFragment = new TranslateFragment();
        goToFragment(translateFragment);
        navigation.getMenu().getItem(0).setChecked(true);
    }
}
