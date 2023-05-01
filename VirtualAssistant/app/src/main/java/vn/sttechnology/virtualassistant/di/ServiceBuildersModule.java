package vn.sttechnology.virtualassistant.di;

import vn.sttechnology.virtualassistant.service.device.AppLockService;
import vn.sttechnology.virtualassistant.service.device.DeviceService;
import vn.sttechnology.virtualassistant.service.stoptracking.StopTrackingService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBuildersModule {
    @ContributesAndroidInjector
    abstract StopTrackingService contributeStopTrackingService();

    @ContributesAndroidInjector
    abstract AppLockService contributeAppLockService();

    @ContributesAndroidInjector
    abstract DeviceService contributeDeviceService();
}
