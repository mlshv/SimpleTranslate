package me.mlshv.simpletranslate.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.fragments.FavoritesFragment;
import me.mlshv.simpletranslate.fragments.SettingsFragment;
import me.mlshv.simpletranslate.fragments.TranslateFragment;

public class MainActivity extends FragmentActivity {

    private TranslateFragment translateFragment;
    private FavoritesFragment favoritesFragment;
    private SettingsFragment settingsFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_translate:
                    goToFragment(translateFragment);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        translateFragment = new TranslateFragment();
        favoritesFragment = new FavoritesFragment();
        settingsFragment = new SettingsFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.main_container, translateFragment).commit();
    }

    private void goToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
    }
}
