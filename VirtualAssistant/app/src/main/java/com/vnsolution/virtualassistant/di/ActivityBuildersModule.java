package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.StartupActivity;
import com.vnsolution.virtualassistant.ui.contact.ContactActivity;
import com.vnsolution.virtualassistant.ui.home.HomeActivity;
import com.vnsolution.virtualassistant.ui.home.HomeV2Activity;
import com.vnsolution.virtualassistant.ui.phoneapp.PhoneAppDashboardActivity;
import com.vnsolution.virtualassistant.ui.phoneapp.v1.PhoneAppsActivity;
import com.vnsolution.virtualassistant.ui.phoneapp.v2.PhoneAppsV2Activity;
import com.vnsolution.virtualassistant.ui.phonecall.v1.PhoneCallsActivity;
import com.vnsolution.virtualassistant.ui.contact.ContactDashboardActivity;
import com.vnsolution.virtualassistant.ui.reminder.ReminderActivity;
import com.vnsolution.virtualassistant.ui.setting.SettingActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelDashboardActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelEditActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelHistoryActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelV2Activity;
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
    abstract ContactDashboardActivity contributePhoneContactDashboardActivity();

    @ContributesAndroidInjector()
    abstract ContactActivity contributePhoneContactsActivity();

    @ContributesAndroidInjector()
    abstract PhoneAppDashboardActivity contributePhoneAppsDashboardActivity();

    @ContributesAndroidInjector()
    abstract PhoneAppsActivity contributePhoneAppsActivity();

    @ContributesAndroidInjector(modules = PhoneAppsFragmentBuildersModule.class)
    abstract PhoneAppsV2Activity contributePhoneAppsV2Activity();

    @ContributesAndroidInjector()
    abstract PhoneCallsActivity contributePhoneCallsActivity();

    @ContributesAndroidInjector()
    abstract PhoneSmsActivity contributePhoneSmsActivity();

    @ContributesAndroidInjector()
    abstract ReminderActivity remindersActivity();

    @ContributesAndroidInjector()
    abstract SettingActivity contributeSettingsActivity();
}
