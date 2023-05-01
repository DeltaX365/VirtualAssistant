package vn.sttechnology.virtualassistant.ui.phonesms.v1;

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

import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.model.PhoneSms;
import vn.sttechnology.virtualassistant.ui.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class PhoneSmsActivity extends BaseActivity implements HasAndroidInjector {
    private static final int REQUEST_CODE = 1001;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout progressOverlay;

    private SmsAdapter smsAdapter;
    private List<PhoneSms> phoneSmsHistory = new ArrayList<>();

    @Inject
    public SmsViewModel smsViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        refreshView();
    }

    private void setupView() {
        setContentView(R.layout.screen_phone_sms_list);
        setupActionBar();

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
    }

    private void refreshView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.READ_SMS }, REQUEST_CODE);
                return;
            }
        }
        smsViewModel.getSms(this,false).observe(this, new Observer<List<PhoneSms>>() {
            @Override
            public void onChanged(List<PhoneSms> phoneSmsList) {
                phoneSmsHistory = phoneSmsList;
                smsAdapter.setData(phoneSmsList);
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
