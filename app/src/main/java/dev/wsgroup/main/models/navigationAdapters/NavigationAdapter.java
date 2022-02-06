package dev.wsgroup.main.models.navigationAdapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import dev.wsgroup.main.views.fragments.tabs.main.HistoryTab;
import dev.wsgroup.main.views.fragments.tabs.main.HomeTab;
import dev.wsgroup.main.views.fragments.tabs.main.ProfileTab;

public class NavigationAdapter extends FragmentStatePagerAdapter {

    private Fragment fragmentCommon;

    public NavigationAdapter(FragmentManager fragmentManager, int behavior) {
        super(fragmentManager, behavior);
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
