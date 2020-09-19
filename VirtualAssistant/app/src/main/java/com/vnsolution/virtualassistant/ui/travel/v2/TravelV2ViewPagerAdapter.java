package com.vnsolution.virtualassistant.ui.travel.v2;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vnsolution.virtualassistant.ui.travel.v1.TravelFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TravelV2ViewPagerAdapter extends FragmentPagerAdapter {
    private List<String> titles = new ArrayList<>();
    private List<TravelFragment> listFragment = new ArrayList<>();

    public TravelV2ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addItem(String title, TravelFragment fragment) {
        titles.add(title);
        listFragment.add(fragment);
    }

    @Override
    public TravelFragment getItem(int position) {
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