package com.vnsolution.virtualassistant.di;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.vnsolution.virtualassistant.VirtualAssistantApp;
import com.vnsolution.virtualassistant.service.FloatingViewService;

import dagger.android.AndroidInjection;
import dagger.android.HasAndroidInjector;
import dagger.android.support.AndroidSupportInjection;

public class AppInjector {
    private static int activityReferences = 0;
    private static Intent floatingServiceIntent;

    public static AppComponent init(VirtualAssistantApp virtualAssistantApp) {
        AppComponent appComponent = DaggerAppComponent.builder().application(virtualAssistantApp)
                .build();
        appComponent.inject(virtualAssistantApp);
        virtualAssistantApp.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                handleActivity(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                if (activityReferences == 0 && floatingServiceIntent != null) {
                    activity.stopService(floatingServiceIntent);
                }
                activityReferences++;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                activityReferences--;
                if (activityReferences == 0) {
                    floatingServiceIntent = new Intent(activity, FloatingViewService.class);
                    activity.startService(floatingServiceIntent);
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
        return appComponent;
    }

    private static void handleActivity(Activity activity) {
        if (activity instanceof HasAndroidInjector) {
            AndroidInjection.inject(activity);
            if (activity instanceof FragmentActivity) {
                ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                    @Override
                    public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
//                        super.onFragmentAttached(fm, f, context);
                        if (f instanceof Injectable) {
                            AndroidSupportInjection.inject(f);
                        }
                    }
                }, true);
            }
//        if (activity instanceof FragmentActivity) {
//            ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
//                @Override
//                public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
//                    AndroidInjection.inject(f);
////                    super.onFragmentAttached(fm, f, context);
//                }
//            }, true);
//        }
        }
    }
}
