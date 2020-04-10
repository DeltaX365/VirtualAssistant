package com.vnsolution.virtualassistant.di;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vnsolution.virtualassistant.VirtualAssistantApp;

import dagger.android.AndroidInjection;

public class AppInjector {
    public static void init(VirtualAssistantApp virtualAssistantApp) {
        DaggerAppComponent.builder().application(virtualAssistantApp)
                .build().inject(virtualAssistantApp);
        virtualAssistantApp.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                handleActivity(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    private static void handleActivity(Activity activity) {
        AndroidInjection.inject(activity);
    }
}
