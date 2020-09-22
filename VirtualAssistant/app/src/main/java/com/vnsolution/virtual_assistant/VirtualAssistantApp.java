package com.vnsolution.virtual_assistant;

import android.app.Application;

import com.vnsolution.virtual_assistant.di.AppInjector;
import com.vnsolution.virtual_assistant.service.AppService;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class VirtualAssistantApp extends Application implements HasAndroidInjector {
    @Inject
    DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        AppInjector.init(this);
        new AppService(getApplicationContext()).run();
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
