package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.service.place.PlaceBroadcastReceiver;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BroadcastReceiverBuildersModule {
    @ContributesAndroidInjector
    abstract PlaceBroadcastReceiver contributePlaceBroadcastReceiver();
}
