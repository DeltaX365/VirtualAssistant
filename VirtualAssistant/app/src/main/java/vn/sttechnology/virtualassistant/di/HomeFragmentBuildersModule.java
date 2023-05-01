package vn.sttechnology.virtualassistant.di;

import vn.sttechnology.virtualassistant.ui.notifications.NotificationFragment;
import vn.sttechnology.virtualassistant.ui.apps.v1.PhoneAppsFragment;
import vn.sttechnology.virtualassistant.ui.phonecall.v1.CallLogsFragment;
import vn.sttechnology.virtualassistant.ui.dashboard.DashboardFragment;
import vn.sttechnology.virtualassistant.ui.settings.SettingFragment;

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
    abstract NotificationFragment contributeNotificationsFragment();

    @ContributesAndroidInjector
    abstract PhoneAppsFragment contributeInstalledApplicationsFragment();

    @ContributesAndroidInjector
    abstract CallLogsFragment contributeCallLogsFragment();

    @ContributesAndroidInjector
    abstract SettingFragment contributeSettingsFragment();
}
