package com.vnsolution.virtualassistant.ui.apps.v2;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.apps.v1.PhoneAppsFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class PhoneAppV2Activity extends AppCompatActivity implements HasAndroidInjector {
    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Inject
    public PhoneAppsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_app_list_v2);

        PhoneAppsV2ViewPagerAdapter adapter = new PhoneAppsV2ViewPagerAdapter(getSupportFragmentManager());
        adapter.addItem("Static", PhoneAppsFragment.newInstance(0));
        adapter.addItem("Top 10 recent applications", PhoneAppsFragment.newInstance(1));
        adapter.addItem("Top 10 most applications", PhoneAppsFragment.newInstance(2));
        adapter.addItem("Top 10 favourite applications", PhoneAppsFragment.newInstance(3));
        adapter.addItem("Top 10 unvisited applications", PhoneAppsFragment.newInstance(4));
        adapter.addItem("All applications", PhoneAppsFragment.newInstance(5));

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