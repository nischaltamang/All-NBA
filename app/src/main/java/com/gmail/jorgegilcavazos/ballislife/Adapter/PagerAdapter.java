package com.gmail.jorgegilcavazos.ballislife.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gmail.jorgegilcavazos.ballislife.View.Fragments.BoxScoreFragment;
import com.gmail.jorgegilcavazos.ballislife.View.Fragments.CommentThreadFragment;

/**
 * Pager for the TabLayout in CommentsActivity.
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
            case 0: // GAME THREAD
                bundle.putInt(CommentThreadFragment.THREAD_TYPE_KEY,
                        CommentThreadFragment.LIVE_THREAD);
                CommentThreadFragment tab1 = new CommentThreadFragment();
                tab1.setArguments(bundle);
                return tab1;
            case 1: // BOX SCORE
                BoxScoreFragment tab2 = new BoxScoreFragment();
                tab2.setArguments(bundle);
                return tab2;
            case 2: // POST GAME THREAD
                bundle.putInt(CommentThreadFragment.THREAD_TYPE_KEY,
                        CommentThreadFragment.POST_THREAD);
                CommentThreadFragment tab3 = new CommentThreadFragment();
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
