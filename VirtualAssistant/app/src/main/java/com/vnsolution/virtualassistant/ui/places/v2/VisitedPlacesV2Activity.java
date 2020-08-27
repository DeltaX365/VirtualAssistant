package com.vnsolution.virtualassistant.ui.places.v2;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.places.v1.VisitedPlacesFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class VisitedPlacesV2Activity extends AppCompatActivity implements HasAndroidInjector {
    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Inject
    public VisitedPlacesFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_visited_places_v2);

        VisitedPlacesV2ViewPagerAdapter adapter = new VisitedPlacesV2ViewPagerAdapter(getSupportFragmentManager());
        adapter.addItem("Static", VisitedPlacesFragment.newInstance(0));
        adapter.addItem("Top 10 recent places", VisitedPlacesFragment.newInstance(1));
        adapter.addItem("Top 10 most places", VisitedPlacesFragment.newInstance(2));
        adapter.addItem("Top 10 favourite places", VisitedPlacesFragment.newInstance(3));
        adapter.addItem("Top 10 unvisited places", VisitedPlacesFragment.newInstance(4));
        adapter.addItem("All places", VisitedPlacesFragment.newInstance(5));

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}