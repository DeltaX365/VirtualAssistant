package com.vnsolution.virtual_assistant.di;

import com.vnsolution.virtual_assistant.ui.phone_apps.v1.PhoneAppsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class PhoneAppsFragmentBuildersModule {
    // Very helpful article for troubleshooting dagger injection
    // https://medium.com/@chauyan/dagger-tips-elegantly-handle-your-activities-and-fragments-by-androidinjector-37c66c7bce7

    @ContributesAndroidInjector
    abstract PhoneAppsFragment contributeInstalledApplicationsFragment();
}
