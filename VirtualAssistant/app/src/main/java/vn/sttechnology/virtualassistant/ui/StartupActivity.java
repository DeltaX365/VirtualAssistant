package vn.sttechnology.virtualassistant.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.ui.common.BaseActivity;
import vn.sttechnology.virtualassistant.ui.home.HomeV2Activity;

public class StartupActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.screen_startup);
        scheduleSplashScreen();
    }

    private void scheduleSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartupActivity.this, HomeV2Activity.class);
                startActivity(intent);
            }
        }, 2000L);
    }
}