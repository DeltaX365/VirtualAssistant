package com.vnsolution.virtualassistant.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vnsolution.virtualassistant.ui.dashboard.DashboardViewModel;
import com.vnsolution.virtualassistant.ui.notifications.NotificationsViewModel;
import com.vnsolution.virtualassistant.ui.places.v1.VisitedPlacesViewModel;
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
    @ViewModelKey(NotificationsViewModel.class)
    abstract ViewModel bindNotificationsViewModel(NotificationsViewModel notificationsViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(VirtualAssistantViewModelFactory factory);
}
