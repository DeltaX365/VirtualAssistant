package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.VirtualAssistantApp;

public class AppInjector {
    public static void init(VirtualAssistantApp virtualAssistantApp) {
        DaggerAppComponent.builder().application(virtualAssistantApp)
                .build().inject(virtualAssistantApp);
    }
}
