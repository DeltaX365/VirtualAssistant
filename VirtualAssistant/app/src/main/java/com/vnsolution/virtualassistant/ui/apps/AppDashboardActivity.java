package com.vnsolution.virtualassistant.ui.apps;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.takwolf.android.lock9.Lock9View;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiSetting;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.ui.apps.component.AppDashboardFilterBottomSheetDialogFragment;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.ui.reminders.ReminderNewActivity;
import com.vnsolution.virtualassistant.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class AppDashboardActivity extends BaseActivity implements HasAndroidInjector {
    private RecyclerView recentUsageAppsRecyclerView;
    private RecyclerViewAdapter<PhoneApp> recentUsageAppsAdapter;
    private RecyclerView.LayoutManager recentUsageAppsLayoutManager;
    private TextView noRecentlyUsageAppTextView;
    private List<PhoneApp> recentUsageApps = new ArrayList<>();

    private RecyclerView mostUsageAppsRecyclerView;
    private RecyclerViewAdapter<PhoneApp> mostUsageAppsAdapter;
    private RecyclerView.LayoutManager mostUsageAppsLayoutManager;
    private TextView noMostUsageAppTextView;
    private List<PhoneApp> mostUsageApps = new ArrayList<>();

    private List<PhoneApp> phoneApps;
    private boolean shouldGetSystemPackages = false;

    @Inject
    public AppViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    private void setupView() {
        setContentView(R.layout.screen_app_dashboard);
        setupActionBar();
        setupRecentUsageAppsView();
        setupMostUsageAppsView();
    }

    private void refreshView() {
        startProcessing();
        phoneApps = vm.getPhoneApps(this, shouldGetSystemPackages,false,true);
        refreshRecentUsageAppsView();
        refreshMostUsageAppsView();
        stopProcessing();
    }

    private void setupRecentUsageAppsView() {
        recentUsageAppsRecyclerView = findViewById(R.id.list_recent_usage_apps);
        recentUsageAppsRecyclerView.setHasFixedSize(true);

        recentUsageAppsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentUsageAppsRecyclerView.setLayoutManager(recentUsageAppsLayoutManager);

        recentUsageAppsAdapter = new RecyclerViewAdapter<PhoneApp>(this, UiSetting.APP_LIST_RECENT_USAGE, R.layout.card_app);
//        recentlyUsageAppsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<Place>() {
//            @Override
//            public void onTap(Place place) {
//                super.onTap(place);
//                Intent intent = new Intent(PhoneAppsDashboardActivity.this, TravelViewActivity.class);
//                intent.putExtra("PlaceId", place.id);
//                startActivity(intent);
//            }
//        });
        recentUsageAppsRecyclerView.setAdapter(recentUsageAppsAdapter);
        noRecentlyUsageAppTextView = findViewById(R.id.no_recent_usage_app);
    }

    private void refreshRecentUsageAppsView() {
        List<PhoneApp> sortablePhoneApps = new ArrayList<>(phoneApps);
        Collections.sort(sortablePhoneApps, new Comparator<PhoneApp>() {
            @Override
            public int compare(PhoneApp o1, PhoneApp o2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int lastTimeUsed1 = (o1 != null && o1.usageStats != null)? (int)(o1.usageStats.getLastTimeUsed() / 1000) : 0;
                    int lastTimeUsed2 = (o2 != null && o2.usageStats != null)? (int)(o2.usageStats.getLastTimeUsed() / 1000) : 0;
                    return (lastTimeUsed2 - lastTimeUsed1);
                }
                return 0;
            }
        });
        recentUsageApps = sortablePhoneApps.subList(0, Math.min(sortablePhoneApps.size(), 10));
        recentUsageAppsAdapter.setData(recentUsageApps);
        recentUsageAppsRecyclerView.setVisibility(recentUsageApps.size() > 0? View.VISIBLE : View.GONE);
        noRecentlyUsageAppTextView.setVisibility(recentUsageApps.size() > 0? View.GONE : View.VISIBLE);
    }

    private void setupMostUsageAppsView() {
        mostUsageAppsRecyclerView = findViewById(R.id.list_most_usage);
        mostUsageAppsRecyclerView.setHasFixedSize(true);

        mostUsageAppsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mostUsageAppsRecyclerView.setLayoutManager(mostUsageAppsLayoutManager);

        mostUsageAppsAdapter = new RecyclerViewAdapter<PhoneApp>(this, UiSetting.APP_LIST_MOST_USAGE, R.layout.card_app);
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
        noMostUsageAppTextView = findViewById(R.id.no_most_usage_app);
    }

    private void refreshMostUsageAppsView() {
        List<PhoneApp> sortablePhoneApps = new ArrayList<>(phoneApps);
        Collections.sort(sortablePhoneApps, new Comparator<PhoneApp>() {
            @Override
            public int compare(PhoneApp o1, PhoneApp o2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int totalTimeInForeground1 = (o1 != null && o1.usageStats != null)? (int)(o1.usageStats.getTotalTimeInForeground() / 1000) : 0;
                    int totalTimeInForeground2 = (o2 != null && o2.usageStats != null)? (int)(o2.usageStats.getTotalTimeInForeground() / 1000) : 0;
                    return (totalTimeInForeground2 - totalTimeInForeground1);
                }
                return 0;
            }
        });
        mostUsageApps = sortablePhoneApps.subList(0, Math.min(sortablePhoneApps.size(), 10));
        mostUsageAppsAdapter.setData(mostUsageApps);
        mostUsageAppsRecyclerView.setVisibility(mostUsageApps.size() > 0? View.VISIBLE : View.GONE);
        noMostUsageAppTextView.setVisibility(mostUsageApps.size() > 0? View.GONE : View.VISIBLE);
    }
}
