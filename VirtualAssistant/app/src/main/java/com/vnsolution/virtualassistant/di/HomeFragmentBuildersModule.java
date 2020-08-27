package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.applications.InstalledApplicationsFragment;
import com.vnsolution.virtualassistant.ui.calllogs.CallLogsFragment;
import com.vnsolution.virtualassistant.ui.dashboard.DashboardFragment;
import com.vnsolution.virtualassistant.ui.notifications.NotificationsFragment;
import com.vnsolution.virtualassistant.ui.places.v1.VisitedPlacesFragment;
import com.vnsolution.virtualassistant.ui.settings.SettingsFragment;

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
    abstract InstalledApplicationsFragment contributeInstalledApplicationsFragment();

    @ContributesAndroidInjector
    abstract CallLogsFragment contributeCallLogsFragment();

    @ContributesAndroidInjector
    abstract SettingsFragment contributeSettingsFragment();
}
