package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.service.stop_tracking.StopTrackingService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBuildersModule {
    @ContributesAndroidInjector
    abstract StopTrackingService contributeStopTrackingService();
}
