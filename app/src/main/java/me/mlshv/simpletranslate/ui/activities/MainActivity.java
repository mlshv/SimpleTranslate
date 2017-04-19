package me.mlshv.simpletranslate.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.data.model.Translation;
import me.mlshv.simpletranslate.ui.fragments.FavoritesFragment;
import me.mlshv.simpletranslate.ui.fragments.HistoryFragment;
import me.mlshv.simpletranslate.ui.fragments.SettingsFragment;
import me.mlshv.simpletranslate.ui.fragments.TranslateFragment;
import me.mlshv.simpletranslate.util.BottomNavigationViewHelper;

public class MainActivity extends AppCompatActivity {

    private TranslateFragment translateFragment;
    private HistoryFragment historyFragment;
    private FavoritesFragment favoritesFragment;
    private SettingsFragment settingsFragment;
    private Fragment currentFragment;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBottomNavigation();
        initFragments();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_translate:
                    goToFragment(translateFragment);
                    return true;
                case R.id.navigation_history:
                    goToFragment(historyFragment);
                    return true;
                case R.id.navigation_favorites:
                    goToFragment(favoritesFragment);
                    return true;
                case R.id.navigation_settings:
                    goToFragment(settingsFragment);
                    return true;
            }
            return false;
        }

    };

    private void initBottomNavigation() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.removeShiftMode(navigation);
    }

    private void initFragments() {
            translateFragment = new TranslateFragment();
            historyFragment = new HistoryFragment();
            favoritesFragment = new FavoritesFragment();
            settingsFragment = new SettingsFragment();
            getSupportFragmentManager().beginTransaction()
                    // добавляем и скрываем фрагменты при старте, иначе при первом переходе будет лаг анимации
                    .add(R.id.main_container, historyFragment)
                    .hide(historyFragment)
                    .add(R.id.main_container, favoritesFragment)
                    .hide(favoritesFragment)
                    .add(R.id.main_container, settingsFragment)
                    .hide(settingsFragment)
                    .add(R.id.main_container, translateFragment)
                    .addToBackStack(null)
                    .commit();
            currentFragment = translateFragment;
    }

    private void goToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .hide(currentFragment)
                .show(fragment)
                .commit();
        currentFragment = fragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void goToTranslationFragmentAndShowTranslation(Translation translation) {
        translateFragment.setVisibleTranslation(translation);
        goToFragment(translateFragment);
        navigation.getMenu().getItem(0).setChecked(true);
    }
}
