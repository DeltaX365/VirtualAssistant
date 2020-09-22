package com.vnsolution.virtual_assistant.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vnsolution.virtual_assistant.ui.phone_apps.v1.PhoneAppsViewModel;
import com.vnsolution.virtual_assistant.ui.phone_calls.v1.CallLogsViewModel;
import com.vnsolution.virtual_assistant.ui.phone_contacts.PhoneContactsViewModel;
import com.vnsolution.virtual_assistant.ui.dashboard.DashboardViewModel;
import com.vnsolution.virtual_assistant.ui.notifications.NotificationsViewModel;
import com.vnsolution.virtual_assistant.ui.travel.TravelViewModel;
import com.vnsolution.virtual_assistant.ui.phone_sms.v1.SmsViewModel;
import com.vnsolution.virtual_assistant.viewmodel.VirtualAssistantViewModelFactory;

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
    @ViewModelKey(PhoneAppsViewModel.class)
    abstract ViewModel bindInstalledApplicationsViewModel(PhoneAppsViewModel phoneAppsViewModel);

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
    @ViewModelKey(PhoneContactsViewModel.class)
    abstract ViewModel bindContactsViewModel(PhoneContactsViewModel phoneContactsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel.class)
    abstract ViewModel bindNotificationsViewModel(NotificationsViewModel notificationsViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(VirtualAssistantViewModelFactory factory);
}
