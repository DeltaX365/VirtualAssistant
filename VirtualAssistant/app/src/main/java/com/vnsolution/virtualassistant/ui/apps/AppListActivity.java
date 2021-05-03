package com.vnsolution.virtualassistant.ui.apps;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class AppListActivity extends BaseActivity implements HasAndroidInjector, SearchView.OnQueryTextListener {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app_list_options, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();
        searchView.setQueryHint("Search your app");
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
        setContentView(R.layout.screen_app_list);
        setupActionBar();
        setupPhoneAppsView();
    }

    private void refreshView() {
        startProcessing();
        phoneApps = vm.getPhoneApps(this, shouldGetSystemPackages,false,false);
        refreshPhoneAppsView();
        stopProcessing();
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
                        if (vm.hasPhoneLockPasscode(AppListActivity.this)) {
                            vm.lockPhoneApp(AppListActivity.this, phoneApp);
                            onPhoneAppLocked(phoneApp);
                            Toast.makeText(AppListActivity.this, "App locked", Toast.LENGTH_SHORT).show();
                        } else {
                            showAppLockPasscodeDialog();
                        }
                        break;
                    case UiSetting.APP_ACTION_UNLOCK:
                        if (vm.hasPhoneLockPasscode(AppListActivity.this)) {
                            vm.unlockPhoneApp(AppListActivity.this, phoneApp);
                            onPhoneAppUnlocked(phoneApp);
                            Toast.makeText(AppListActivity.this, "App unlocked", Toast.LENGTH_SHORT).show();
                        } else {
                            showAppLockPasscodeDialog();
                        }
                        break;
                    case UiSetting.APP_ACTION_SCHEDULE:
                        Intent intent = new Intent(AppListActivity.this, ReminderNewActivity.class);
                        startActivity(intent);
                        break;
                    case UiSetting.APP_ACTION_EXPORT:
                        break;
                    case UiSetting.APP_ACTION_SHARE:
                        boolean result = vm.sharePhoneApp(AppListActivity.this, phoneApp);
                        if (!result) {
                            Toast.makeText(AppListActivity.this, "Can not perform the action.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case UiSetting.APP_ACTION_UNINSTALL:
                        if (vm.deletePhoneApp(AppListActivity.this, phoneApp)) {
                            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + phoneApp.packageName));
                            startActivity(uninstallIntent);
                            onPhoneAppDeleted(phoneApp);
                            Toast.makeText(AppListActivity.this, "App deleted", Toast.LENGTH_SHORT).show();
                        }
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

    private void onPhoneAppDeleted(PhoneApp phoneApp) {
        PhoneApp deletedPhoneApp = null;
        for(PhoneApp phoneApp1 : phoneApps) {
            if (phoneApp1.packageName.equals(phoneApp.packageName)) {
                deletedPhoneApp = phoneApp1;
                break;
            }
        }
        if (deletedPhoneApp != null) {
            phoneApps.remove(deletedPhoneApp);
            refreshPhoneAppsView();
        }
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
                        vm.savePhoneLockPasscode(AppListActivity.this, passwordNumbers);
                        dialog.dismiss();
                        Toast.makeText(AppListActivity.this, "You set passcode successfully. You can not start locking an app", Toast.LENGTH_SHORT).show();
                    } else {
                        isEnteringFirstTime = true;
                        isEnteringSecondTime = false;
                        textView.setText("Draw Pattern");
                        Toast.makeText(AppListActivity.this, "Both Pattern did not match - Try again", Toast.LENGTH_SHORT).show();
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
