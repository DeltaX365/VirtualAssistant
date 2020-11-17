package com.vnsolution.virtualassistant.ui.phoneapps;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.phoneapps.v1.PhoneAppsViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PhoneAppsDashboardActivity extends BaseActivity {
    private RecyclerView recentlyUsageAppsRecyclerView;
    private RecyclerViewAdapter<PhoneApp> recentlyUsageAppsAdapter;
    private RecyclerView.LayoutManager recentlyUsageAppsLayoutManager;
    private List<PhoneApp> recentlyUsageApps = new ArrayList<>();

    private RecyclerView mostUsageAppsRecyclerView;
    private RecyclerViewAdapter<PhoneApp> mostUsageAppsAdapter;
    private RecyclerView.LayoutManager mostUsageAppsLayoutManager;
    private List<PhoneApp> mostUsageApps = new ArrayList<>();

    private RecyclerView categorizedAppsRecyclerView;
    private RecyclerViewAdapter<PhoneApp> categorizedAppsAdapter;
    private RecyclerView.LayoutManager categorizedAppsLayoutManager;
    private List<PhoneApp> categorizedApps;

    private FrameLayout progressOverlay;
    private PhoneAppsViewModel vm;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = ViewModelProviders.of(this, viewModelFactory).get(PhoneAppsViewModel.class);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.screen_phone_apps_dashboard);
        setupActionBar();

        progressOverlay = findViewById(R.id.progress_overlay);

        setupRecentlyUsageAppsView();
        setupMostUsageAppsView();
        setupCategorizedAppsView();
    }

    private void setupRecentlyUsageAppsView() {
        recentlyUsageAppsRecyclerView = findViewById(R.id.list_recently_usage_apps);
        recentlyUsageAppsRecyclerView.setHasFixedSize(true);

        recentlyUsageAppsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentlyUsageAppsRecyclerView.setLayoutManager(recentlyUsageAppsLayoutManager);

        recentlyUsageAppsAdapter = new RecyclerViewAdapter<PhoneApp>(this, UiOptions.APP_LIST_RECENTLY_USAGE, R.layout.card_app);
//        recentlyUsageAppsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<Place>() {
//            @Override
//            public void onTap(Place place) {
//                super.onTap(place);
//                Intent intent = new Intent(PhoneAppsDashboardActivity.this, TravelViewActivity.class);
//                intent.putExtra("PlaceId", place.id);
//                startActivity(intent);
//            }
//        });
        recentlyUsageAppsRecyclerView.setAdapter(recentlyUsageAppsAdapter);

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressOverlay.setAnimation(inAnimation);
        progressOverlay.setVisibility(View.VISIBLE);

        int filter = 1;
        vm.getPhoneApps(this, filter,false, false).observe(this, new Observer<List<PhoneApp>>() {
            @Override
            public void onChanged(@Nullable List<PhoneApp> apps) {
                PhoneAppsDashboardActivity.this.recentlyUsageApps = apps;
                recentlyUsageAppsAdapter.setData(apps);

                AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressOverlay.setAnimation(outAnimation);
                progressOverlay.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setupMostUsageAppsView() {
        mostUsageAppsRecyclerView = findViewById(R.id.list_most_usage);
        mostUsageAppsRecyclerView.setHasFixedSize(true);

        mostUsageAppsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mostUsageAppsRecyclerView.setLayoutManager(mostUsageAppsLayoutManager);

        mostUsageAppsAdapter = new RecyclerViewAdapter<PhoneApp>(this, UiOptions.APP_LIST_MOST_USAGE, R.layout.card_app);
//        mostUsageAppsTodayAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<Place>() {
//            @Override
//            public void onTap(Place place) {
//                super.onTap(place);
//                Intent intent = new Intent(PhoneAppsDashboardActivity.this, TravelViewActivity.class);
//                intent.putExtra("PlaceId", place.id);
//                startActivity(intent);
//            }
//        });
        mostUsageAppsRecyclerView.setAdapter(mostUsageAppsAdapter);

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressOverlay.setAnimation(inAnimation);
        progressOverlay.setVisibility(View.VISIBLE);

        int filter = 2;
        vm.getPhoneApps(this, filter,false, false).observe(this, new Observer<List<PhoneApp>>() {
            @Override
            public void onChanged(@Nullable List<PhoneApp> apps) {
                PhoneAppsDashboardActivity.this.mostUsageApps = apps;
                mostUsageAppsAdapter.setData(apps);

                AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressOverlay.setAnimation(outAnimation);
                progressOverlay.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setupCategorizedAppsView() {
        categorizedAppsRecyclerView = findViewById(R.id.list_categorized_apps);
        categorizedAppsRecyclerView.setHasFixedSize(true);

        categorizedAppsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        categorizedAppsRecyclerView.setLayoutManager(categorizedAppsLayoutManager);

        categorizedAppsAdapter = new RecyclerViewAdapter<PhoneApp>(this, UiOptions.APP_LIST_CATEGORY, R.layout.item_app);
//        categorizedAppsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneApp>() {
//            @Override
//            public void onTap(PhoneApp item) {
//                super.onTap(item);
//                Intent intent = new Intent(PhoneAppsDashboardActivity.this, TravelHistoryActivity.class);
//                intent.putExtra("UiOption", item.type);
//                startActivity(intent);
//            }
//        });
        categorizedAppsRecyclerView.setAdapter(categorizedAppsAdapter);

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressOverlay.setAnimation(inAnimation);
        progressOverlay.setVisibility(View.VISIBLE);

        int filter = 0;
        vm.getPhoneApps(this, filter,false, false).observe(this, new Observer<List<PhoneApp>>() {
            @Override
            public void onChanged(@Nullable List<PhoneApp> apps) {
                PhoneAppsDashboardActivity.this.categorizedApps = apps;
                categorizedAppsAdapter.setData(apps);

                AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressOverlay.setAnimation(outAnimation);
                progressOverlay.setVisibility(View.INVISIBLE);
            }
        });
    }
}
