package me.mlshv.simpletranslate.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.mlshv.simpletranslate.R;

public class FavoritesHistoryContainerFragment extends Fragment {
    private HistoryFragment historyFragment = new HistoryFragment();
    private FavoritesFragment favoritesFragment = new FavoritesFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_history_container, container, false);
        // Инициализируем ViewPager и наш фрагмент
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.favorites_history_viewpager);
        FavoritesHistoryPagerAdapter pagerAdapter = new FavoritesHistoryPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        return view;
    }

    private class FavoritesHistoryPagerAdapter extends FragmentPagerAdapter {
        private Fragment fragments[] = new Fragment[] { historyFragment, favoritesFragment };
        private String tabTitles[] = new String[] { "История", "Избранное" };

        FavoritesHistoryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // уведомляем вложенные фрагменты о том, что батю показали (support library bug fix)
        final FragmentManager childFragmentManager = getChildFragmentManager();

        if (childFragmentManager != null) {
            final List<Fragment> nestedFragments = childFragmentManager.getFragments();
            if (nestedFragments == null || nestedFragments.size() == 0) return;
            for (Fragment childFragment : nestedFragments) {
                if (childFragment != null && !childFragment.isDetached() && !childFragment.isRemoving()) {
                    childFragment.onHiddenChanged(hidden);
                }
            }
        }
    }
}
