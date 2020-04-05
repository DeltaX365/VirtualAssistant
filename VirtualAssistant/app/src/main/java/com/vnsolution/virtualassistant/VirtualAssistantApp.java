package com.vnsolution.virtualassistant;

import android.app.Application;

import com.vnsolution.virtualassistant.di.AppInjector;

public class VirtualAssistantApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppInjector.init(this);
    }
}
