package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.service.geolocation.GeolocationTrackingService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBuildersModule {
    @ContributesAndroidInjector
    abstract GeolocationTrackingService contributeGeolocationService();
}
