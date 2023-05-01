package vn.sttechnology.virtualassistant.di;

import vn.sttechnology.virtualassistant.service.place.PlaceBroadcastReceiver;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BroadcastReceiverBuildersModule {
    @ContributesAndroidInjector
    abstract PlaceBroadcastReceiver contributePlaceBroadcastReceiver();
}
