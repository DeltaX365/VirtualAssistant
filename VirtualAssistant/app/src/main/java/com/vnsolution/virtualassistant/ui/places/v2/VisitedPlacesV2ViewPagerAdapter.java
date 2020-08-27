package com.vnsolution.virtualassistant.ui.places.v2;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vnsolution.virtualassistant.ui.places.v1.VisitedPlacesFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class VisitedPlacesV2ViewPagerAdapter extends FragmentPagerAdapter {
    private List<String> titles = new ArrayList<>();
    private List<VisitedPlacesFragment> listFragment = new ArrayList<>();

    public VisitedPlacesV2ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addItem(String title, VisitedPlacesFragment fragment) {
        titles.add(title);
        listFragment.add(fragment);
    }

    @Override
    public VisitedPlacesFragment getItem(int position) {
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