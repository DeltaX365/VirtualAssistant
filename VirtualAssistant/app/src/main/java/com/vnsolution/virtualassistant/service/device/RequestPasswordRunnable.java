package com.vnsolution.virtualassistant.service.device;

import android.content.Context;
import android.content.Intent;

import com.vnsolution.virtualassistant.ui.home.HomeV2Activity;

public class RequestPasswordRunnable implements Runnable {
    private Context context;
    private String packageName;

    public RequestPasswordRunnable(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
    }

    @Override
    public void run() {
        Intent passwordActivity = new Intent(context, HomeV2Activity.class);
        passwordActivity.putExtra("PACKAGE_NAME", packageName);
        passwordActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(passwordActivity);
    }
}
