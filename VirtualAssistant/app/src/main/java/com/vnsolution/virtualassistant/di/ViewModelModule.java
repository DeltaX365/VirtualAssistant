package com.vnsolution.virtualassistant.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vnsolution.virtualassistant.ui.notification.NotificationViewModel;
import com.vnsolution.virtualassistant.ui.phoneapp.PhoneAppViewModel;
import com.vnsolution.virtualassistant.ui.contact.ContactViewModel;
import com.vnsolution.virtualassistant.ui.phonecall.v1.CallLogsViewModel;
import com.vnsolution.virtualassistant.ui.dashboard.DashboardViewModel;
import com.vnsolution.virtualassistant.ui.travel.TravelViewModel;
import com.vnsolution.virtualassistant.ui.phonesms.v1.SmsViewModel;
import com.vnsolution.virtualassistant.viewmodel.VirtualAssistantViewModelFactory;

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
    @ViewModelKey(PhoneAppViewModel.class)
    abstract ViewModel bindInstalledApplicationsViewModel(PhoneAppViewModel phoneAppViewModel);

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
    @ViewModelKey(NotificationViewModel.class)
    abstract ViewModel bindNotificationsViewModel(NotificationViewModel notificationViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(VirtualAssistantViewModelFactory factory);
}
