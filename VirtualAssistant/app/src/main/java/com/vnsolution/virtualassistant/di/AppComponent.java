package com.vnsolution.virtualassistant.di;

import android.app.Application;

import com.vnsolution.virtualassistant.VirtualAssistantApp;
import com.vnsolution.virtualassistant.di.activities.DashboardActivityModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        //To use Dagger in Android, should add all activity modules right here. Wait for the next version of Dagger for this fixing
        //https://dagger.dev/android
        DashboardActivityModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }
    void inject(VirtualAssistantApp virtualAssistantApp);
}
