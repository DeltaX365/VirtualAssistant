package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.HomeActivity;
import com.vnsolution.virtualassistant.ui.HomeV2Activity;
import com.vnsolution.virtualassistant.ui.phoneapps.v1.PhoneAppsActivity;
import com.vnsolution.virtualassistant.ui.phoneapps.v2.PhoneAppsV2Activity;
import com.vnsolution.virtualassistant.ui.phonecalls.v1.PhoneCallsActivity;
import com.vnsolution.virtualassistant.ui.phonecontacts.PhoneContactsActivity;
import com.vnsolution.virtualassistant.ui.developer.DeveloperActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelSummaryActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelV2Activity;
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
    abstract TravelActivity contributeTravelActivity();

    @ContributesAndroidInjector()
    abstract TravelSummaryActivity contributeTravelSummaryActivity();

    @ContributesAndroidInjector(modules = TravelFragmentBuildersModule.class)
    abstract TravelV2Activity contributeTravelV2Activity();

    @ContributesAndroidInjector()
    abstract PhoneAppsActivity contributePhoneAppsActivity();

    @ContributesAndroidInjector(modules = PhoneAppsFragmentBuildersModule.class)
    abstract PhoneAppsV2Activity contributePhoneAppsV2Activity();

    @ContributesAndroidInjector()
    abstract PhoneCallsActivity contributePhoneCallsActivity();

    @ContributesAndroidInjector()
    abstract PhoneSmsActivity contributePhoneSmsActivity();

    @ContributesAndroidInjector()
    abstract PhoneContactsActivity contributePhoneContactsActivity();

    @ContributesAndroidInjector()
    abstract SettingsActivity contributeSettingsActivity();
}
