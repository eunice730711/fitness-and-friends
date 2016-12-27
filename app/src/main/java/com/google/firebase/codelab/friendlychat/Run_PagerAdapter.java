package com.google.firebase.codelab.friendlychat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by CatLin on 2016/12/24.
 */

public class Run_PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private double dist;
    private Running_fragment tab1;
    private Map_fragment tab2;

    public Run_PagerAdapter(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
        this.tab1 = new Running_fragment();
        this.tab2 = new Map_fragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return tab1;
            case 1:
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
