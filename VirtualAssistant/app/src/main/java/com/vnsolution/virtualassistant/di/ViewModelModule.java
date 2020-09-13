package com.vnsolution.virtualassistant.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vnsolution.virtualassistant.ui.phoneapps.v1.PhoneAppsViewModel;
import com.vnsolution.virtualassistant.ui.phonecalls.v1.CallLogsViewModel;
import com.vnsolution.virtualassistant.ui.phonecontacts.PhoneContactsViewModel;
import com.vnsolution.virtualassistant.ui.dashboard.DashboardViewModel;
import com.vnsolution.virtualassistant.ui.notifications.NotificationsViewModel;
import com.vnsolution.virtualassistant.ui.places.v1.VisitedPlacesViewModel;
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
    @ViewModelKey(VisitedPlacesViewModel.class)
    abstract ViewModel bindVisitedPlaceViewModel(VisitedPlacesViewModel visitedPlaceViewModel);

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
