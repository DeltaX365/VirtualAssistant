package vn.sttechnology.virtualassistant.di;

import vn.sttechnology.virtualassistant.ui.StartupActivity;
import vn.sttechnology.virtualassistant.ui.apps.AppListActivity;
import vn.sttechnology.virtualassistant.ui.contacts.ContactListActivity;
import vn.sttechnology.virtualassistant.ui.contacts.ContactViewActivity;
import vn.sttechnology.virtualassistant.ui.home.HomeActivity;
import vn.sttechnology.virtualassistant.ui.home.HomeV2Activity;
import vn.sttechnology.virtualassistant.ui.apps.AppDashboardActivity;
import vn.sttechnology.virtualassistant.ui.apps.v1.PhoneAppActivity;
import vn.sttechnology.virtualassistant.ui.apps.v2.PhoneAppV2Activity;
import vn.sttechnology.virtualassistant.ui.phonecall.v1.PhoneCallActivity;
import vn.sttechnology.virtualassistant.ui.contacts.ContactDashboardActivity;
import vn.sttechnology.virtualassistant.ui.reminders.ReminderListActivity;
import vn.sttechnology.virtualassistant.ui.reminders.ReminderNewActivity;
import vn.sttechnology.virtualassistant.ui.reminders.ReminderShowFullScreenActivity;
import vn.sttechnology.virtualassistant.ui.settings.PermissionActivity;
import vn.sttechnology.virtualassistant.ui.settings.SettingActivity;
import vn.sttechnology.virtualassistant.ui.travel.TravelDashboardActivity;
import vn.sttechnology.virtualassistant.ui.travel.TravelEditActivity;
import vn.sttechnology.virtualassistant.ui.travel.TravelHistoryActivity;
import vn.sttechnology.virtualassistant.ui.travel.TravelTrackingActivity;
import vn.sttechnology.virtualassistant.ui.travel.TravelV2Activity;
import vn.sttechnology.virtualassistant.ui.phonesms.v1.PhoneSmsActivity;
import vn.sttechnology.virtualassistant.ui.travel.TravelViewActivity;

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
