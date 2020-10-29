package com.vnsolution.virtualassistant.ui.phonesms.v1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.PhoneSms;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class PhoneSmsActivity extends AppCompatActivity implements HasAndroidInjector {
    private static final int REQUEST_CODE = 1001;
    private SmsRecycleView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout progressOverlay;

    private SmsAdapter smsAdapter;
    private List<PhoneSms> phoneSmsHistory = new ArrayList<>();

    @Inject
    public SmsViewModel smsViewModel;

    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_phone_sms);

        recyclerView = findViewById(R.id.list_sms);
        progressOverlay =  findViewById(R.id.progress_overlay);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        smsAdapter = new SmsAdapter(this, phoneSmsHistory);
        recyclerView.setAdapter(smsAdapter);

        // retrieve data
        getSms();
    }

    private void getSms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.READ_SMS }, REQUEST_CODE);
                return;
            }
        }

        // retrieve data
        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressOverlay.setAnimation(inAnimation);
        progressOverlay.setVisibility(View.VISIBLE);

        smsViewModel.getSms(this,false).observe(this, new Observer<List<PhoneSms>>() {
            @Override
            public void onChanged(List<PhoneSms> phoneSmsList) {
                phoneSmsHistory = phoneSmsList;
                smsAdapter.setData(phoneSmsList);

                AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressOverlay.setAnimation(outAnimation);
                progressOverlay.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            getSms();
        }
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}
