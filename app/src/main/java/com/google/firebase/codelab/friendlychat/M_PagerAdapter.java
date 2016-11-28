package com.google.firebase.codelab.friendlychat;

/**
 * Created by pei on 2016/11/15.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class M_PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public M_PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Post_fragment tab1 = new Post_fragment();
                return tab1;
            case 1:
                Join_fragment tab2 = new Join_fragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
