package com.vnsolution.virtualassistant;

import android.app.Application;

import com.vnsolution.virtualassistant.di.AppComponent;
import com.vnsolution.virtualassistant.di.AppInjector;
import com.vnsolution.virtualassistant.service.AppService;
import com.vnsolution.virtualassistant.service.AppWorkManager;

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
        new AppWorkManager(getApplicationContext()).run();
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
