package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.HomeActivity;
import com.vnsolution.virtualassistant.ui.HomeV2Activity;
import com.vnsolution.virtualassistant.ui.applications.InstalledApplicationsActivity;
import com.vnsolution.virtualassistant.ui.calllogs.CallLogsActivity;
import com.vnsolution.virtualassistant.ui.places.VisitedPlacesActivity;
import com.vnsolution.virtualassistant.ui.settings.SettingsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {
    //To use Dagger in Android, should add all activity modules right here. Wait for the next version of Dagger for this fixing
    //https://dagger.dev/android

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract HomeActivity contributeHomeActivity();

    @ContributesAndroidInjector()
    abstract HomeV2Activity contributeHomeV2Activity();

    @ContributesAndroidInjector()
    abstract VisitedPlacesActivity contributeVisitedPlacesActivity();

    @ContributesAndroidInjector()
    abstract InstalledApplicationsActivity contributeInstalledApplicationsActivity();

    @ContributesAndroidInjector()
    abstract CallLogsActivity contributeCallLogsActivity();

    @ContributesAndroidInjector()
    abstract SettingsActivity contributeSettingsActivity();
}
