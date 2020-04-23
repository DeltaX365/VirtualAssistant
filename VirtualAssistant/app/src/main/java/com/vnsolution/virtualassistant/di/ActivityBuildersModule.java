package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.dashboard.DashboardActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {
    @ContributesAndroidInjector
    abstract DashboardActivity contributeDashboardActivity();
}
