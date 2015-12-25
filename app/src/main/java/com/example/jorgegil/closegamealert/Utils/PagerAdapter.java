package com.example.jorgegil.closegamealert.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.jorgegil.closegamealert.View.Fragments.BoxScoreFragment;
import com.example.jorgegil.closegamealert.View.Fragments.ThreadFragment;

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
                //bundle.putString("threadUrl", "https://www.reddit.com/r/nba/search.json?sort=new&restrict_sr=on&q=flair%3AGame%2BThread");
                bundle.putString("threadUrl", "https://www.reddit.com/r/nba/.json?limit=100");
                bundle.putString("threadType", "LIVE");
                ThreadFragment tab1 = new ThreadFragment();
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                BoxScoreFragment tab2 = new BoxScoreFragment();
                tab2.setArguments(bundle);
                return tab2;
            case 2:
                bundle.putString("threadUrl", "https://www.reddit.com/r/nba/.json?limit=100");
                bundle.putString("threadType", "POST");
                ThreadFragment tab3 = new ThreadFragment();
                tab3.setArguments(bundle);
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
