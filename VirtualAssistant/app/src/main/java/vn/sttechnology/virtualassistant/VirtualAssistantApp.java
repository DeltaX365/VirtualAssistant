package vn.sttechnology.virtualassistant;

import android.app.Application;

import com.google.android.libraries.places.api.Places;

import vn.sttechnology.virtualassistant.common.AppSetting;
import vn.sttechnology.virtualassistant.di.AppComponent;
import vn.sttechnology.virtualassistant.di.AppInjector;
import vn.sttechnology.virtualassistant.service.AppService;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class VirtualAssistantApp extends Application implements HasAndroidInjector {
    @Inject
    DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    public AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = AppInjector.init(this);
        new AppService(getApplicationContext()).run();
        Places.initialize(getApplicationContext(), AppSetting.GM_API_KEY);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}
