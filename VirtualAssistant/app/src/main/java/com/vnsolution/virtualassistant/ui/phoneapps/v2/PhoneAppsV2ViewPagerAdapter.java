package com.vnsolution.virtualassistant.ui.phoneapps.v2;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vnsolution.virtualassistant.ui.phoneapps.v1.PhoneAppsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class PhoneAppsV2ViewPagerAdapter extends FragmentPagerAdapter {
    private List<String> titles = new ArrayList<>();
    private List<PhoneAppsFragment> listFragment = new ArrayList<>();

    public PhoneAppsV2ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addItem(String title, PhoneAppsFragment fragment) {
        titles.add(title);
        listFragment.add(fragment);
    }

    @Override
    public PhoneAppsFragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return listFragment.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return listFragment.size();
    }
}