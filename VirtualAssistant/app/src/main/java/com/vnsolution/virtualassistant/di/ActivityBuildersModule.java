package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.HomeActivity;
import com.vnsolution.virtualassistant.ui.HomeV2Activity;
import com.vnsolution.virtualassistant.ui.applications.v1.InstalledApplicationsActivity;
import com.vnsolution.virtualassistant.ui.applications.v2.InstalledApplicationsV2Activity;
import com.vnsolution.virtualassistant.ui.phonecalls.v1.PhoneCallsActivity;
import com.vnsolution.virtualassistant.ui.phonecontacts.PhoneContactsActivity;
import com.vnsolution.virtualassistant.ui.developer.DeveloperActivity;
import com.vnsolution.virtualassistant.ui.places.v1.VisitedPlacesActivity;
import com.vnsolution.virtualassistant.ui.places.v2.VisitedPlacesV2Activity;
import com.vnsolution.virtualassistant.ui.settings.SettingsActivity;
import com.vnsolution.virtualassistant.ui.phonesms.v1.PhoneSmsActivity;

import dagger.Module;
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

    @ContributesAndroidInjector(modules = InstalledApplicationsFragmentBuildersModule.class)
    abstract InstalledApplicationsV2Activity contributeInstalledApplicationsV2Activity();

    @ContributesAndroidInjector()
    abstract PhoneCallsActivity contributeCallLogsActivity();

    @ContributesAndroidInjector()
    abstract PhoneSmsActivity contributeSmsActivity();

    @ContributesAndroidInjector()
    abstract PhoneContactsActivity contributeContactsActivity();

    @ContributesAndroidInjector()
    abstract SettingsActivity contributeSettingsActivity();
}
