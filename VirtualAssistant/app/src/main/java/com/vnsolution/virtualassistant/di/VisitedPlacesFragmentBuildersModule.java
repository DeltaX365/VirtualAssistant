package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.ui.places.v1.VisitedPlacesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class VisitedPlacesFragmentBuildersModule {
    // Very helpful article for troubleshooting dagger injection
    // https://medium.com/@chauyan/dagger-tips-elegantly-handle-your-activities-and-fragments-by-androidinjector-37c66c7bce7

    @ContributesAndroidInjector
    abstract VisitedPlacesFragment contributeVisitedPlacesFragment();
}
