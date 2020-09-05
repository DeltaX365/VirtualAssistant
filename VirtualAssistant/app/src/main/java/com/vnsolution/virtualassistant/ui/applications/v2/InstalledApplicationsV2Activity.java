package com.vnsolution.virtualassistant.ui.applications.v2;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.applications.v1.InstalledApplicationsFragment;
import com.vnsolution.virtualassistant.ui.places.v1.VisitedPlacesFragment;
import com.vnsolution.virtualassistant.ui.places.v2.VisitedPlacesV2ViewPagerAdapter;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class InstalledApplicationsV2Activity extends AppCompatActivity implements HasAndroidInjector {
    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Inject
    public InstalledApplicationsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_installed_applications_v2);

        InstalledApplicationsV2ViewPagerAdapter adapter = new InstalledApplicationsV2ViewPagerAdapter(getSupportFragmentManager());
        adapter.addItem("Static", InstalledApplicationsFragment.newInstance(0));
        adapter.addItem("Top 10 recent applications", InstalledApplicationsFragment.newInstance(1));
        adapter.addItem("Top 10 most applications", InstalledApplicationsFragment.newInstance(2));
        adapter.addItem("Top 10 favourite applications", InstalledApplicationsFragment.newInstance(3));
        adapter.addItem("Top 10 unvisited applications", InstalledApplicationsFragment.newInstance(4));
        adapter.addItem("All applications", InstalledApplicationsFragment.newInstance(5));

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