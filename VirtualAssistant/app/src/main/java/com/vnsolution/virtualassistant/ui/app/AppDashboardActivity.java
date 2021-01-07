package com.vnsolution.virtualassistant.ui.app;

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

    private boolean isEnteringFirstTime = true;
    private boolean isEnteringSecondTime = false;
    private String passwordNumbers;

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

    private void setupPhoneAppsView() {
        phoneAppsRecyclerView = findViewById(R.id.list_phone_apps);
        phoneAppsRecyclerView.setHasFixedSize(true);

        phoneAppsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        phoneAppsRecyclerView.setLayoutManager(phoneAppsLayoutManager);

        phoneAppsAdapter = new RecyclerViewAdapter<PhoneApp>(this, UiSetting.APP_LIST_CATEGORY, R.layout.card_app_full_width);
        phoneAppsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneApp>() {
            @Override
            public void onAction(PhoneApp phoneApp, String action) {
                switch (action) {
                    case UiSetting.APP_ACTION_LAUNCH:
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(phoneApp.packageName);
                        startActivity(launchIntent);
                        break;
                    case UiSetting.APP_ACTION_LOCK:
                        if (vm.hasPhoneLockPasscode(AppDashboardActivity.this)) {
                            vm.lockPhoneApp(AppDashboardActivity.this, phoneApp);
                            onPhoneAppLocked(phoneApp);
                            Toast.makeText(AppDashboardActivity.this, "App locked", Toast.LENGTH_SHORT).show();
                        } else {
                            showAppLockPasscodeDialog();
                        }
                        break;
                    case UiSetting.APP_ACTION_UNLOCK:
                        if (vm.hasPhoneLockPasscode(AppDashboardActivity.this)) {
                            vm.unlockPhoneApp(AppDashboardActivity.this, phoneApp);
                            onPhoneAppUnlocked(phoneApp);
                            Toast.makeText(AppDashboardActivity.this, "App unlocked", Toast.LENGTH_SHORT).show();
                        } else {
                            showAppLockPasscodeDialog();
                        }
                        break;
                    case UiSetting.APP_ACTION_SCHEDULE:
                        Intent intent = new Intent(AppDashboardActivity.this, ReminderNewActivity.class);
                        startActivity(intent);
                        break;
                    case UiSetting.APP_ACTION_EXPORT:
                        break;
                    case UiSetting.APP_ACTION_SHARE:
                        boolean result = vm.sharePhoneApp(AppDashboardActivity.this, phoneApp);
                        if (!result) {
                            Toast.makeText(AppDashboardActivity.this, "Can not perform the action.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case UiSetting.APP_ACTION_UNINSTALL:
                        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + phoneApp.packageName));
                        startActivity(uninstallIntent);
                        break;
                }
            }
        });
        phoneAppsRecyclerView.setAdapter(phoneAppsAdapter);
        noPhoneAppTextView = findViewById(R.id.no_phone_apps);
    }

    private void onPhoneAppLocked(PhoneApp phoneApp) {
        for(PhoneApp phoneApp1 : phoneApps) {
            if (phoneApp1.packageName.equals(phoneApp.packageName)) {
                phoneApp1.locked = 1;
                break;
            }
        }
        refreshPhoneAppsView();
    }

    private void onPhoneAppUnlocked(PhoneApp phoneApp) {
        for(PhoneApp phoneApp1 : phoneApps) {
            if (phoneApp1.packageName.equals(phoneApp.packageName)) {
                phoneApp1.locked = 0;
                break;
            }
        }
        refreshPhoneAppsView();
    }

    private void refreshPhoneAppsView() {
        phoneAppsAdapter.setData(phoneApps);
        phoneAppsRecyclerView.setVisibility(phoneApps.size() > 0? View.VISIBLE : View.GONE);
        noPhoneAppTextView.setVisibility(phoneApps.size() > 0? View.GONE : View.VISIBLE);
    }

    private void showAppLockPasscodeDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(Gravity.CENTER);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.screen_passcode, null);
        TextView textView = promptsView.findViewById(R.id.textView);
        Lock9View lock9View = (Lock9View) promptsView.findViewById(R.id.lock_9_view);
        lock9View.setGestureCallback(new Lock9View.GestureCallback() {
            @Override
            public void onNodeConnected(@NonNull int[] numbers) {
            }

            @Override
            public void onGestureFinished(@NonNull int[] numbers) {
                if (isEnteringFirstTime) {
                    passwordNumbers = StringUtils.join("", numbers);
                    isEnteringFirstTime = false;
                    isEnteringSecondTime = true;
                    textView.setText("Re-Draw Pattern");
                } else if (isEnteringSecondTime) {
                    String passConfirm = StringUtils.join("", numbers);
                    if (passwordNumbers.matches(passConfirm)) {
                        vm.savePhoneLockPasscode(AppDashboardActivity.this, passwordNumbers);
                        dialog.dismiss();
                        Toast.makeText(AppDashboardActivity.this, "You set passcode successfully. You can not start locking an app", Toast.LENGTH_SHORT).show();
                    } else {
                        isEnteringFirstTime = true;
                        isEnteringSecondTime = false;
                        textView.setText("Draw Pattern");
                        Toast.makeText(AppDashboardActivity.this, "Both Pattern did not match - Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dialog.setContentView(promptsView);
        dialog.show();
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
