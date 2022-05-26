package dev.wsgroup.main.views.fragments.tabs.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.navigationAdapters.NavigationAdapter;
import dev.wsgroup.main.views.fragments.OrderFragment;

public class HistoryTab extends Fragment {

    private ViewPager historyViewPager;
    private TabLayout historyTabLayout;
    private TabLayout.Tab tabCommon;

    private int tabPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_history_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        historyViewPager = view.findViewById(R.id.historyViewPager);
        historyTabLayout = view.findViewById(R.id.historyTabLayout);

        tabPosition = getActivity().getIntent()
                                   .getIntExtra("HISTORY_TAB_POSITION", 0);

        historyTabLayout.removeAllTabs();
        historyViewPager.setAdapter(null);

        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Unpaid");
        historyTabLayout.addTab(tabCommon);

        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Waiting");
        historyTabLayout.addTab(tabCommon);

        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Ordered");
        historyTabLayout.addTab(tabCommon);

        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Processing");
        historyTabLayout.addTab(tabCommon);

        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Delivering");
        historyTabLayout.addTab(tabCommon);

        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Delivered");
        historyTabLayout.addTab(tabCommon);

        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Completed");
        historyTabLayout.addTab(tabCommon);

        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Returning");
        historyTabLayout.addTab(tabCommon);

        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Returned");
        historyTabLayout.addTab(tabCommon);

        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Cancelled");
        historyTabLayout.addTab(tabCommon);

        historyTabLayout.setTabTextColors(getResources().getColor(R.color.black),
                                            getResources().getColor(R.color.black));
        NavigationAdapter adapter = new NavigationAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return  new OrderFragment(historyTabLayout.getTabAt(position).getText().toString());
            }

            @Override
            public int getCount() {
                return historyTabLayout.getTabCount();
            }
        };
        historyViewPager.setAdapter(adapter);
        historyTabLayout.clearOnTabSelectedListeners();
        historyViewPager.clearOnPageChangeListeners();
        historyViewPager.setEnabled(false);
        historyViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(historyTabLayout));
        historyTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                historyViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        historyTabLayout.selectTab(historyTabLayout.getTabAt(tabPosition));
        historyViewPager.setCurrentItem(historyTabLayout.getSelectedTabPosition());
    }
}