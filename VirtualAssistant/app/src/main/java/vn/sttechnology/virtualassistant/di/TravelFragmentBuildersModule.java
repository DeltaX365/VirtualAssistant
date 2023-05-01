package vn.sttechnology.virtualassistant.di;

import vn.sttechnology.virtualassistant.ui.travel.TravelFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class TravelFragmentBuildersModule {
    // Very helpful article for troubleshooting dagger injection
    // https://medium.com/@chauyan/dagger-tips-elegantly-handle-your-activities-and-fragments-by-androidinjector-37c66c7bce7

    @ContributesAndroidInjector
    abstract TravelFragment contributeTravelFragment();
}
