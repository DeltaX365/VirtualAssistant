package com.vnsolution.virtualassistant.ui.phonecontacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class PhoneContactsActivity extends BaseActivity implements HasAndroidInjector {
    private static final int REQUEST_CODE = 1001;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout progressOverlay;

    private RecyclerViewAdapter<PhoneContact> phoneContactsAdapter;
    private List<PhoneContact> phoneContactList = new ArrayList<>();

    @Inject
    public PhoneContactsViewModel phoneContactsViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        refreshView();
    }

    private void setupView() {
        setContentView(R.layout.screen_phone_contacts);
        setupActionBar();

        progressOverlay =  findViewById(R.id.progress_overlay);
        recyclerView = findViewById(R.id.list_contacts);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        phoneContactsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact_full_width);
        recyclerView.setAdapter(phoneContactsAdapter);
    }

    private void refreshView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.READ_CONTACTS }, REQUEST_CODE);
                return;
            }
        }

        phoneContactsViewModel.getContacts(this,false).observe(this, new Observer<List<PhoneContact>>() {
            @Override
            public void onChanged(List<PhoneContact> phoneContacts) {
                phoneContactList = phoneContacts;
                phoneContactsAdapter.setData(phoneContacts);
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
