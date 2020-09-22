package com.vnsolution.virtual_assistant.ui.travel;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.vnsolution.virtual_assistant.R;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class TravelV2Activity extends AppCompatActivity implements HasAndroidInjector {
    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Inject
    public TravelFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_travel_v2);

        TravelV2ViewPagerAdapter adapter = new TravelV2ViewPagerAdapter(getSupportFragmentManager());
        adapter.addItem("Static", TravelFragment.newInstance(0));
        adapter.addItem("Top 10 recent places", TravelFragment.newInstance(1));
        adapter.addItem("Top 10 most places", TravelFragment.newInstance(2));
        adapter.addItem("Top 10 favourite places", TravelFragment.newInstance(3));
        adapter.addItem("Top 10 unvisited places", TravelFragment.newInstance(4));
        adapter.addItem("All places", TravelFragment.newInstance(5));

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