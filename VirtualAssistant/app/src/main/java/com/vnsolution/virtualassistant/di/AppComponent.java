package com.vnsolution.virtualassistant.di;

import android.app.Application;

import com.vnsolution.virtualassistant.VirtualAssistantApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class
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
