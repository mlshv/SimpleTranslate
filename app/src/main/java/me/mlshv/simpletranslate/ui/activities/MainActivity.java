package me.mlshv.simpletranslate.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.data.model.Translation;
import me.mlshv.simpletranslate.ui.fragments.FavoritesHistoryContainerFragment;
import me.mlshv.simpletranslate.ui.fragments.SettingsFragment;
import me.mlshv.simpletranslate.ui.fragments.TranslateFragment;

public class MainActivity extends FragmentActivity {

    private TranslateFragment translateFragment;
    private FavoritesHistoryContainerFragment favoritesHistoryContainerFragment;
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
                case R.id.navigation_favorites:
                    goToFragment(favoritesHistoryContainerFragment);
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

    }

    private void initFragments() {
            translateFragment = new TranslateFragment();
            favoritesHistoryContainerFragment = new FavoritesHistoryContainerFragment();
            settingsFragment = new SettingsFragment();
            getSupportFragmentManager().beginTransaction()
                    // добавляем и скрываем фрагменты при старте, иначе при первом переходе будет лаг анимации
                    .add(R.id.main_container, favoritesHistoryContainerFragment)
                    .hide(favoritesHistoryContainerFragment)
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
