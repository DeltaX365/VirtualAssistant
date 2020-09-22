package com.vnsolution.virtual_assistant.di;

import com.vnsolution.virtual_assistant.ui.phone_apps.v1.PhoneAppsFragment;
import com.vnsolution.virtual_assistant.ui.phone_calls.v1.CallLogsFragment;
import com.vnsolution.virtual_assistant.ui.dashboard.DashboardFragment;
import com.vnsolution.virtual_assistant.ui.notifications.NotificationsFragment;
import com.vnsolution.virtual_assistant.ui.settings.SettingsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class HomeFragmentBuildersModule {
    // Very helpful article for troubleshooting dagger injection
    // https://medium.com/@chauyan/dagger-tips-elegantly-handle-your-activities-and-fragments-by-androidinjector-37c66c7bce7

    @ContributesAndroidInjector
    abstract DashboardFragment contributeDashboardFragment();

//    @ContributesAndroidInjector
//    abstract VisitedPlacesFragment contributeVisitedPlacesFragment();

    @ContributesAndroidInjector
    abstract NotificationsFragment contributeNotificationsFragment();

    @ContributesAndroidInjector
    abstract PhoneAppsFragment contributeInstalledApplicationsFragment();

    @ContributesAndroidInjector
    abstract CallLogsFragment contributeCallLogsFragment();

    @ContributesAndroidInjector
    abstract SettingsFragment contributeSettingsFragment();
}
