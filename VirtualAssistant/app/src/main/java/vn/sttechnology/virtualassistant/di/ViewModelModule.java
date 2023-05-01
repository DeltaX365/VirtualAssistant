package vn.sttechnology.virtualassistant.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import vn.sttechnology.virtualassistant.ui.notifications.NotificationViewModel;
import vn.sttechnology.virtualassistant.ui.apps.AppViewModel;
import vn.sttechnology.virtualassistant.ui.contacts.ContactViewModel;
import vn.sttechnology.virtualassistant.ui.phonecall.v1.CallLogsViewModel;
import vn.sttechnology.virtualassistant.ui.dashboard.DashboardViewModel;
import vn.sttechnology.virtualassistant.ui.reminders.ReminderViewModel;
import vn.sttechnology.virtualassistant.ui.travel.TravelViewModel;
import vn.sttechnology.virtualassistant.ui.phonesms.v1.SmsViewModel;
import vn.sttechnology.virtualassistant.viewmodel.VirtualAssistantViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel.class)
    abstract ViewModel bindDashboardViewModel(DashboardViewModel dashboardViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TravelViewModel.class)
    abstract ViewModel bindTravelViewModel(TravelViewModel travelViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AppViewModel.class)
    abstract ViewModel bindInstalledApplicationsViewModel(AppViewModel appViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CallLogsViewModel.class)
    abstract ViewModel bindCallLogsViewModel(CallLogsViewModel callLogsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SmsViewModel.class)
    abstract ViewModel bindSmsViewModel(SmsViewModel smsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ContactViewModel.class)
    abstract ViewModel bindContactsViewModel(ContactViewModel contactViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ReminderViewModel.class)
    abstract ViewModel bindReminderViewModel(ReminderViewModel reminderViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel.class)
    abstract ViewModel bindNotificationsViewModel(NotificationViewModel notificationViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(VirtualAssistantViewModelFactory factory);
}
