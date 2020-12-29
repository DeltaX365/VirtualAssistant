package com.vnsolution.virtualassistant.ui.phoneapp;

import android.content.Intent;
import android.net.Uri;
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
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PhoneAppDashboardActivity extends BaseActivity {
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
    private PhoneAppViewModel vm;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = ViewModelProviders.of(this, viewModelFactory).get(PhoneAppViewModel.class);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.screen_app_dashboard);
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
        vm.getPhoneAppsAsync(this, filter,false, false).observe(this, new Observer<List<PhoneApp>>() {
            @Override
            public void onChanged(@Nullable List<PhoneApp> apps) {
                PhoneAppDashboardActivity.this.recentlyUsageApps = apps;
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
        vm.getPhoneAppsAsync(this, filter,false, false).observe(this, new Observer<List<PhoneApp>>() {
            @Override
            public void onChanged(@Nullable List<PhoneApp> apps) {
                PhoneAppDashboardActivity.this.mostUsageApps = apps;
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

        categorizedAppsAdapter = new RecyclerViewAdapter<PhoneApp>(this, UiOptions.APP_LIST_CATEGORY, R.layout.card_app_full_width);
        categorizedAppsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneApp>() {
            @Override
            public void onAction(PhoneApp item, String action) {
                super.onAction(item, action);
                switch (action) {
                    case UiOptions.APP_ACTION_LAUNCH:
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(item.packageName);
                        startActivity(launchIntent);
                        break;
                    case UiOptions.APP_ACTION_UNINSTALL:
                        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + item.packageName));
                        startActivity(uninstallIntent);
                        break;
                }
            }
        });
        categorizedAppsRecyclerView.setAdapter(categorizedAppsAdapter);

        categorizedApps = vm.getPhoneApps(this,false,false);
        categorizedAppsAdapter.setData(categorizedApps);
    }
}
