package com.vnsolution.virtualassistant.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vnsolution.virtualassistant.db.dao.SmsHistoryDao;
import com.vnsolution.virtualassistant.ui.applications.v1.InstalledApplicationsViewModel;
import com.vnsolution.virtualassistant.ui.calllogs.v1.CallLogsViewModel;
import com.vnsolution.virtualassistant.ui.contacts.ContactsViewModel;
import com.vnsolution.virtualassistant.ui.dashboard.DashboardViewModel;
import com.vnsolution.virtualassistant.ui.notifications.NotificationsViewModel;
import com.vnsolution.virtualassistant.ui.places.v1.VisitedPlacesViewModel;
import com.vnsolution.virtualassistant.ui.sms.v1.SmsViewModel;
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
    @ViewModelKey(InstalledApplicationsViewModel.class)
    abstract ViewModel bindInstalledApplicationsViewModel(InstalledApplicationsViewModel installedApplicationsViewModel);

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
    @ViewModelKey(ContactsViewModel.class)
    abstract ViewModel bindContactsViewModel(ContactsViewModel contactsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel.class)
    abstract ViewModel bindNotificationsViewModel(NotificationsViewModel notificationsViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(VirtualAssistantViewModelFactory factory);
}
