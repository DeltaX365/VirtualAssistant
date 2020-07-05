package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.applications.InstalledApplicationsFragment;
import com.vnsolution.virtualassistant.ui.calllogs.CallLogsFragment;
import com.vnsolution.virtualassistant.ui.dashboard.DashboardFragment;
import com.vnsolution.virtualassistant.ui.notifications.NotificationsFragment;
import com.vnsolution.virtualassistant.ui.places.VisitedPlacesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    // Very helpful article for troubleshooting dagger injection
    // https://medium.com/@chauyan/dagger-tips-elegantly-handle-your-activities-and-fragments-by-androidinjector-37c66c7bce7

    @ContributesAndroidInjector
    abstract DashboardFragment contributeDashboardFragment();
    @ContributesAndroidInjector
    abstract VisitedPlacesFragment contributeVisitedPlacesFragment();
    @ContributesAndroidInjector
    abstract NotificationsFragment contributeNotificationsFragment();
    @ContributesAndroidInjector
    abstract InstalledApplicationsFragment contributeInstalledApplicationsFragment();
    @ContributesAndroidInjector
    abstract CallLogsFragment contributeCallLogsFragment();
}
