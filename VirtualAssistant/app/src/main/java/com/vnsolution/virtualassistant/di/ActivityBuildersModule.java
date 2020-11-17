package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.StartupActivity;
import com.vnsolution.virtualassistant.ui.home.HomeActivity;
import com.vnsolution.virtualassistant.ui.home.HomeV2Activity;
import com.vnsolution.virtualassistant.ui.phoneapps.PhoneAppsDashboardActivity;
import com.vnsolution.virtualassistant.ui.phoneapps.v1.PhoneAppsActivity;
import com.vnsolution.virtualassistant.ui.phoneapps.v2.PhoneAppsV2Activity;
import com.vnsolution.virtualassistant.ui.phonecalls.v1.PhoneCallsActivity;
import com.vnsolution.virtualassistant.ui.phonecontacts.PhoneContactsActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelDashboardActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelEditActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelHistoryActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelV2Activity;
import com.vnsolution.virtualassistant.ui.settings.SettingsActivity;
import com.vnsolution.virtualassistant.ui.phonesms.v1.PhoneSmsActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelViewActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {
    //To use Dagger in Android, should add all activity modules right here. Wait for the next version of Dagger for this fixing
    //https://dagger.dev/android

    @ContributesAndroidInjector()
    abstract StartupActivity contributeStartupActivity();

    @ContributesAndroidInjector(modules = HomeFragmentBuildersModule.class)
    abstract HomeActivity contributeHomeActivity();

    @ContributesAndroidInjector()
    abstract HomeV2Activity contributeHomeV2Activity();

    @ContributesAndroidInjector()
    abstract TravelDashboardActivity contributeTravelActivity();

    @ContributesAndroidInjector()
    abstract TravelEditActivity contributeTravelEditActivity();

    @ContributesAndroidInjector()
    abstract TravelViewActivity contributeTravelViewActivity();

    @ContributesAndroidInjector()
    abstract TravelHistoryActivity contributeTravelSummaryActivity();

    @ContributesAndroidInjector(modules = TravelFragmentBuildersModule.class)
    abstract TravelV2Activity contributeTravelV2Activity();

    @ContributesAndroidInjector()
    abstract PhoneAppsDashboardActivity contributePhoneAppsDashboardActivity();

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
