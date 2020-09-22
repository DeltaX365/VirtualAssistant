package com.vnsolution.virtual_assistant.ui.phone_sms.v1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.vnsolution.virtual_assistant.R;
import com.vnsolution.virtual_assistant.model.PhoneSms;
import com.vnsolution.virtual_assistant.ui.common.OnItemActionListerner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class PhoneSmsActivity extends AppCompatActivity implements HasAndroidInjector, OnItemActionListerner {
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
        recyclerView.setOnItemAction(this);

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
    public void onTap(View view, int position) {
    }

    @Override
    public void onLongTap(View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("Removed item will never be restored. Tap Yes to remove this item.");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    public void onDoubleTap(View view, int position) {
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}
