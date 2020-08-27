package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.HomeActivity;
import com.vnsolution.virtualassistant.ui.HomeV2Activity;
import com.vnsolution.virtualassistant.ui.applications.InstalledApplicationsActivity;
import com.vnsolution.virtualassistant.ui.calllogs.CallLogsActivity;
import com.vnsolution.virtualassistant.ui.developer.DeveloperActivity;
import com.vnsolution.virtualassistant.ui.places.v1.VisitedPlacesActivity;
import com.vnsolution.virtualassistant.ui.places.v2.VisitedPlacesV2Activity;
import com.vnsolution.virtualassistant.ui.places.v2.VisitedPlacesV2ViewPagerAdapter;
import com.vnsolution.virtualassistant.ui.settings.SettingsActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {
    //To use Dagger in Android, should add all activity modules right here. Wait for the next version of Dagger for this fixing
    //https://dagger.dev/android

    @ContributesAndroidInjector(modules = HomeFragmentBuildersModule.class)
    abstract HomeActivity contributeHomeActivity();

    @ContributesAndroidInjector()
    abstract HomeV2Activity contributeHomeV2Activity();

    @ContributesAndroidInjector()
    abstract DeveloperActivity contributeDeveloperActivity();

    @ContributesAndroidInjector()
    abstract VisitedPlacesActivity contributeVisitedPlacesActivity();

    @ContributesAndroidInjector(modules = VisitedPlacesFragmentBuildersModule.class)
    abstract VisitedPlacesV2Activity contributeVisitedPlacesV2Activity();

    @ContributesAndroidInjector()
    abstract InstalledApplicationsActivity contributeInstalledApplicationsActivity();

    @ContributesAndroidInjector()
    abstract CallLogsActivity contributeCallLogsActivity();

    @ContributesAndroidInjector()
    abstract SettingsActivity contributeSettingsActivity();
}
