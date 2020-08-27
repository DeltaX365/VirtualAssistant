package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.applications.InstalledApplicationsFragment;
import com.vnsolution.virtualassistant.ui.calllogs.CallLogsFragment;
import com.vnsolution.virtualassistant.ui.dashboard.DashboardFragment;
import com.vnsolution.virtualassistant.ui.notifications.NotificationsFragment;
import com.vnsolution.virtualassistant.ui.places.v1.VisitedPlacesFragment;
import com.vnsolution.virtualassistant.ui.places.v2.VisitedPlacesV2Activity;
import com.vnsolution.virtualassistant.ui.places.v2.VisitedPlacesV2ViewPagerAdapter;
import com.vnsolution.virtualassistant.ui.settings.SettingsFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class VisitedPlacesFragmentBuildersModule {
    // Very helpful article for troubleshooting dagger injection
    // https://medium.com/@chauyan/dagger-tips-elegantly-handle-your-activities-and-fragments-by-androidinjector-37c66c7bce7

    @ContributesAndroidInjector
    abstract VisitedPlacesFragment contributeVisitedPlacesFragment();
}
