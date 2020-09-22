package com.vnsolution.virtual_assistant.di;

import android.app.Application;

import com.vnsolution.virtual_assistant.VirtualAssistantApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        //To use Dagger in Android, should add all activity/service modules right here. Wait for the next version of Dagger for this fixing
        //https://dagger.dev/android
        //https://github.com/android/architecture-components-samples/issues/253
        ServiceBuildersModule.class,
        ActivityBuildersModule.class
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
