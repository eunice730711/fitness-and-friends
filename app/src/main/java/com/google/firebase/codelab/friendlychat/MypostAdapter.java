package com.google.firebase.codelab.friendlychat;

/**
 * Created by pei on 2016/11/15.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MypostAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public MypostAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                mypost_fragment tab1 = new mypost_fragment();
                return tab1;
            case 1:
                myjoin_fragment tab2 = new myjoin_fragment();
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
