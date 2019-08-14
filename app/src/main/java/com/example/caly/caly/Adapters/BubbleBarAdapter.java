package com.example.caly.caly.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.caly.caly.Fragments.FakeFragment;
import com.example.caly.caly.Fragments.HomeUserFragment;
import com.example.caly.caly.Fragments.SearchBarFragment;
import com.example.caly.caly.Fragments.UserProfileFragment;
import com.example.caly.caly.Fragments.UserReservationsFragment;

public class BubbleBarAdapter extends FragmentStatePagerAdapter {

    public BubbleBarAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return HomeUserFragment.newInstance();
            case 1:
                return UserProfileFragment.newInstance();
            case 2:

                return UserReservationsFragment.newInstance();
            case 3:
                return SearchBarFragment.newInstance();
            default:
                return HomeUserFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }


}