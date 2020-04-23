package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.service.geolocation.GeolocationService;
import com.vnsolution.virtualassistant.ui.dashboard.DashboardActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBuildersModule {
    @ContributesAndroidInjector
    abstract GeolocationService contributeGeolocationService();
}
