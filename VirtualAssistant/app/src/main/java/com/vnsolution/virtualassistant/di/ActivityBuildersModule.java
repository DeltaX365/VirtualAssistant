package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.StartupActivity;
import com.vnsolution.virtualassistant.ui.apps.AppListActivity;
import com.vnsolution.virtualassistant.ui.contacts.ContactListActivity;
import com.vnsolution.virtualassistant.ui.contacts.ContactViewActivity;
import com.vnsolution.virtualassistant.ui.home.HomeActivity;
import com.vnsolution.virtualassistant.ui.home.HomeV2Activity;
import com.vnsolution.virtualassistant.ui.apps.AppDashboardActivity;
import com.vnsolution.virtualassistant.ui.apps.v1.PhoneAppActivity;
import com.vnsolution.virtualassistant.ui.apps.v2.PhoneAppV2Activity;
import com.vnsolution.virtualassistant.ui.phonecall.v1.PhoneCallActivity;
import com.vnsolution.virtualassistant.ui.contacts.ContactDashboardActivity;
import com.vnsolution.virtualassistant.ui.reminders.ReminderListActivity;
import com.vnsolution.virtualassistant.ui.reminders.ReminderNewActivity;
import com.vnsolution.virtualassistant.ui.reminders.ReminderShowFullScreenActivity;
import com.vnsolution.virtualassistant.ui.settings.PermissionActivity;
import com.vnsolution.virtualassistant.ui.settings.SettingActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelDashboardActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelEditActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelHistoryActivity;
import com.vnsolution.virtualassistant.ui.travel.TravelTrackingActivity;
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
    abstract TravelDashboardActivity contributeTravelDashboardActivity();

    @ContributesAndroidInjector()
    abstract TravelEditActivity contributeTravelEditActivity();

    @ContributesAndroidInjector()
    abstract TravelViewActivity contributeTravelViewActivity();

    @ContributesAndroidInjector()
    abstract TravelHistoryActivity contributeTravelHistoryActivity();

    @ContributesAndroidInjector()
    abstract TravelTrackingActivity contributeTravelTrackingActivity();

    @ContributesAndroidInjector(modules = TravelFragmentBuildersModule.class)
    abstract TravelV2Activity contributeTravelV2Activity();

    @ContributesAndroidInjector()
    abstract ContactDashboardActivity contributeContactDashboardActivity();

    @ContributesAndroidInjector()
    abstract ContactViewActivity contributeContactViewActivity();

    @ContributesAndroidInjector()
    abstract ContactListActivity contributeContactListActivity();

    @ContributesAndroidInjector()
    abstract AppDashboardActivity contributeAppDashboardActivity();

    @ContributesAndroidInjector()
    abstract AppListActivity contributeAppListActivity();

    @ContributesAndroidInjector()
    abstract PhoneAppActivity contributePhoneAppActivity();

    @ContributesAndroidInjector(modules = PhoneAppsFragmentBuildersModule.class)
    abstract PhoneAppV2Activity contributePhoneAppV2Activity();

    @ContributesAndroidInjector()
    abstract PhoneCallActivity contributePhoneCallActivity();

    @ContributesAndroidInjector()
    abstract PhoneSmsActivity contributePhoneSmsActivity();

    @ContributesAndroidInjector()
    abstract ReminderListActivity contributeReminderListActivity();

    @ContributesAndroidInjector()
    abstract ReminderNewActivity contributeReminderNewActivity();

    @ContributesAndroidInjector()
    abstract ReminderShowFullScreenActivity contributeReminderShowFullScreenActivity();

    @ContributesAndroidInjector()
    abstract SettingActivity contributeSettingActivity();

    @ContributesAndroidInjector()
    abstract PermissionActivity contributePermissionActivity();
}
