package com.example.jorgegil.closegamealert;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by jorgegil on 12/6/15.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int numOfTabs;
    Bundle bundle;

    public PagerAdapter(FragmentManager fragmentManager, int numOfTabs, Bundle bundle) {
        super(fragmentManager);
        this.numOfTabs = numOfTabs;
        this.bundle = bundle;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ThreadFragment tab1 = new ThreadFragment();
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                BoxScoreFragment tab2 = new BoxScoreFragment();
                return tab2;
            case 2:
                StatsFragment tab3 = new StatsFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
