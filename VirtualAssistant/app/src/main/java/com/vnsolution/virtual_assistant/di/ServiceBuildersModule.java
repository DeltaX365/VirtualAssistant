package com.vnsolution.virtual_assistant.di;

import com.vnsolution.virtual_assistant.service.stop_tracking.StopTrackingService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBuildersModule {
    @ContributesAndroidInjector
    abstract StopTrackingService contributeStopTrackingService();
}
