package com.vnsolution.virtual_assistant.di;

import com.vnsolution.virtual_assistant.ui.HomeActivity;
import com.vnsolution.virtual_assistant.ui.HomeV2Activity;
import com.vnsolution.virtual_assistant.ui.phone_apps.v1.PhoneAppsActivity;
import com.vnsolution.virtual_assistant.ui.phone_apps.v2.PhoneAppsV2Activity;
import com.vnsolution.virtual_assistant.ui.phone_calls.v1.PhoneCallsActivity;
import com.vnsolution.virtual_assistant.ui.phone_contacts.PhoneContactsActivity;
import com.vnsolution.virtual_assistant.ui.developer.DeveloperActivity;
import com.vnsolution.virtual_assistant.ui.travel.TravelActivity;
import com.vnsolution.virtual_assistant.ui.travel.TravelV2Activity;
import com.vnsolution.virtual_assistant.ui.settings.SettingsActivity;
import com.vnsolution.virtual_assistant.ui.phone_sms.v1.PhoneSmsActivity;

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
    abstract TravelActivity contributeVisitedPlacesActivity();

    @ContributesAndroidInjector(modules = TravelFragmentBuildersModule.class)
    abstract TravelV2Activity contributeTravelV2Activity();

    @ContributesAndroidInjector()
    abstract PhoneAppsActivity contributeInstalledApplicationsActivity();

    @ContributesAndroidInjector(modules = PhoneAppsFragmentBuildersModule.class)
    abstract PhoneAppsV2Activity contributeInstalledApplicationsV2Activity();

    @ContributesAndroidInjector()
    abstract PhoneCallsActivity contributeCallLogsActivity();

    @ContributesAndroidInjector()
    abstract PhoneSmsActivity contributeSmsActivity();

    @ContributesAndroidInjector()
    abstract PhoneContactsActivity contributeContactsActivity();

    @ContributesAndroidInjector()
    abstract SettingsActivity contributeSettingsActivity();
}
