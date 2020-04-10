package com.vnsolution.virtualassistant.di.activities;

import com.vnsolution.virtualassistant.ui.dashboard.DashboardActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class DashboardActivityModule {
    @ContributesAndroidInjector
    abstract DashboardActivity contributeHomeActivity();
}
