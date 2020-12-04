package com.vnsolution.virtualassistant.ui.phonecall.v1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.PhoneCall;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class PhoneCallsActivity extends BaseActivity implements HasAndroidInjector {
    private static final int REQUEST_CODE = 1001;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout progressOverlay;

    private CallLogsAdapter callLogsAdapter;
    private List<PhoneCall> callHistory = new ArrayList<>();

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Inject
    public CallLogsViewModel callLogsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        refreshView();
    }

    private void setupView() {
        setContentView(R.layout.screen_phone_call);
        setupActionBar();

        recyclerView = findViewById(R.id.list_call_logs);
        progressOverlay =  findViewById(R.id.progress_overlay);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        callLogsAdapter = new CallLogsAdapter(this, callHistory);
        recyclerView.setAdapter(callLogsAdapter);
    }

    private void refreshView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.READ_CALL_LOG }, REQUEST_CODE);
                return;
            }
        }
        callLogsViewModel.getCallLogs(this,false).observe(this, new Observer<List<PhoneCall>>() {
            @Override
            public void onChanged(List<PhoneCall> phoneCalls) {
                callHistory = phoneCalls;
                callLogsAdapter.setData(phoneCalls);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            refreshView();
        }
    }
}
