package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.HomeActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {
    @ContributesAndroidInjector
    abstract HomeActivity contributeHomeActivity();
}
