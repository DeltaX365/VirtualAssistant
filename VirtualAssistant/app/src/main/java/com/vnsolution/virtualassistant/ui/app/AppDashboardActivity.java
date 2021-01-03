package com.vnsolution.virtualassistant.ui.app;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.app.component.AppDashboardFilterBottomSheetDialogFragment;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.ui.reminder.ReminderNewActivity;
import com.vnsolution.virtualassistant.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class AppDashboardActivity extends BaseActivity implements HasAndroidInjector, SearchView.OnQueryTextListener {
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

    private RecyclerView phoneAppsRecyclerView;
    private RecyclerViewAdapter<PhoneApp> phoneAppsAdapter;
    private RecyclerView.LayoutManager phoneAppsLayoutManager;
    private TextView noPhoneAppTextView;
    private List<PhoneApp> phoneApps;

    private boolean shouldGetSystemPackages = false;

    @Inject
    public AppViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        refreshView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app_dashboard_options, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();
        searchView.setQueryHint("Search your contact");
        searchView.setOnQueryTextListener(this);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setQueryRefinementEnabled(true);
//        searchView.setSubmitButtonEnabled(true);
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_filter:
                AppDashboardFilterBottomSheetDialogFragment bottomSheetDialog = new AppDashboardFilterBottomSheetDialogFragment();
                bottomSheetDialog.listener(new AppDashboardFilterBottomSheetDialogFragment.Listener() {
                    @Override
                    public void onChanged(AppDashboardFilterBottomSheetDialogFragment.Filter filterOption) {
                        boolean includeSystemApps = (filterOption == AppDashboardFilterBottomSheetDialogFragment.Filter.INCLUDE_SYSTEM_APPS)? true : false;
                        if (shouldGetSystemPackages != includeSystemApps) {
                            shouldGetSystemPackages = includeSystemApps;
                            refreshView();
                        }
                    }
                });
                bottomSheetDialog.setValue(shouldGetSystemPackages ? AppDashboardFilterBottomSheetDialogFragment.Filter.INCLUDE_SYSTEM_APPS : AppDashboardFilterBottomSheetDialogFragment.Filter.EXCLUDE_SYSTEM_APPS);
                bottomSheetDialog.show(getSupportFragmentManager(), "AppDashboardFilterBottomSheetDialogFragment");
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void setupView() {
        setContentView(R.layout.screen_app_dashboard);
        setupActionBar();

        setupRecentUsageAppsView();
        setupMostUsageAppsView();
        setupPhoneAppsView();
    }

    private void refreshView() {
        startProcessing();
        vm.getPhoneAppsAsync(this, shouldGetSystemPackages,false).observe(this, new Observer<List<PhoneApp>>() {
            @Override
            public void onChanged(List<PhoneApp> phoneAppList) {
                phoneApps = phoneAppList;
                refreshRecentUsageAppsView();
                refreshMostUsageAppsView();
                refreshPhoneAppsView();
                stopProcessing();
            }
        });
    }

    private void setupRecentUsageAppsView() {
        recentUsageAppsRecyclerView = findViewById(R.id.list_recent_usage_apps);
        recentUsageAppsRecyclerView.setHasFixedSize(true);

        recentUsageAppsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentUsageAppsRecyclerView.setLayoutManager(recentUsageAppsLayoutManager);

        recentUsageAppsAdapter = new RecyclerViewAdapter<PhoneApp>(this, UiOptions.APP_LIST_RECENT_USAGE, R.layout.card_app);
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

    private void setupPhoneAppsView() {
        phoneAppsRecyclerView = findViewById(R.id.list_phone_apps);
        phoneAppsRecyclerView.setHasFixedSize(true);

        phoneAppsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        phoneAppsRecyclerView.setLayoutManager(phoneAppsLayoutManager);

        phoneAppsAdapter = new RecyclerViewAdapter<PhoneApp>(this, UiOptions.APP_LIST_CATEGORY, R.layout.card_app_full_width);
        phoneAppsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneApp>() {
            @Override
            public void onAction(PhoneApp phoneApp, String action) {
                switch (action) {
                    case UiOptions.APP_ACTION_LAUNCH:
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(phoneApp.packageName);
                        startActivity(launchIntent);
                        break;
                    case UiOptions.APP_ACTION_LOCK:
                        break;
                    case UiOptions.APP_ACTION_SCHEDULE:
                        Intent intent = new Intent(AppDashboardActivity.this, ReminderNewActivity.class);
                        startActivity(intent);
                        break;
                    case UiOptions.APP_ACTION_EXPORT:
                        break;
                    case UiOptions.APP_ACTION_SHARE:
                        boolean result = vm.sharePhoneApp(AppDashboardActivity.this, phoneApp);
                        if (!result) {
                            Toast.makeText(AppDashboardActivity.this, "Can not perform the action.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case UiOptions.APP_ACTION_UNINSTALL:
                        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + phoneApp.packageName));
                        startActivity(uninstallIntent);
                        break;
                }
            }
        });
        phoneAppsRecyclerView.setAdapter(phoneAppsAdapter);
        noPhoneAppTextView = findViewById(R.id.no_phone_apps);
    }

    private void refreshPhoneAppsView() {
        phoneAppsAdapter.setData(phoneApps);
        phoneAppsRecyclerView.setVisibility(phoneApps.size() > 0? View.VISIBLE : View.GONE);
        noPhoneAppTextView.setVisibility(phoneApps.size() > 0? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, "Query Inserted", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter(newText);
        return true;
    }

    private void filter(String queryText) {
        List<PhoneApp> filteredApps = new ArrayList<>();
        if (queryText.isEmpty()) {
            filteredApps.addAll(phoneApps);
        } else {
            for (PhoneApp app : phoneApps) {
                String normalizedName = StringUtils.nonAccentVietnamese(app.appName);
                if (normalizedName.toLowerCase().contains(queryText.toLowerCase())) {
                    filteredApps.add(app);
                }
            }
        }
        phoneAppsAdapter.setData(filteredApps);
    }
}
