package me.mlshv.simpletranslate.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import me.mlshv.simpletranslate.R;
import me.mlshv.simpletranslate.adapter.FragmentPagerAdapterImpl;
import me.mlshv.simpletranslate.data.model.Translation;
import me.mlshv.simpletranslate.ui.fragments.FavoritesFragment;
import me.mlshv.simpletranslate.ui.fragments.HistoryFragment;
import me.mlshv.simpletranslate.ui.fragments.PageableFragment;
import me.mlshv.simpletranslate.ui.fragments.SettingsFragment;
import me.mlshv.simpletranslate.ui.fragments.TranslateFragment;
import me.mlshv.simpletranslate.ui.views.NonSwipeableViewPager;
import me.mlshv.simpletranslate.util.BottomNavigationViewHelper;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigation;
    private NonSwipeableViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (NonSwipeableViewPager) findViewById(R.id.main_viewpager);
        FragmentPagerAdapterImpl fragmentPagerAdapter = new FragmentPagerAdapterImpl(getSupportFragmentManager());
        fragmentPagerAdapter.addFragment(new TranslateFragment());
        fragmentPagerAdapter.addFragment(new HistoryFragment());
        fragmentPagerAdapter.addFragment(new FavoritesFragment());
        fragmentPagerAdapter.addFragment(new SettingsFragment());
        viewPager.setAdapter(fragmentPagerAdapter);
        initBottomNavigation();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_translate:
                    PageableFragment f = (PageableFragment) viewPager.getAdapter().getItem(0);
                    f.notifyPaged();
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_history:
                    f = (PageableFragment) viewPager.getAdapter().getItem(1);
                    f.notifyPaged();
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_favorites:
                    f = (PageableFragment) viewPager.getAdapter().getItem(2);
                    f.notifyPaged();
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_settings:
                    viewPager.setCurrentItem(3);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void goToTranslationFragmentAndShowTranslation(Translation translation) {
        viewPager.setCurrentItem(0);
        ((TranslateFragment) viewPager.getAdapter().getItem(0)).setVisibleTranslation(translation);
        navigation.getMenu().getItem(0).setChecked(true);
    }
}
