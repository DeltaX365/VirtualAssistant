package com.vnsolution.virtualassistant.ui.contact;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.DeviceOptions;
import com.vnsolution.virtualassistant.common.StopTrackingOptions;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.util.PermissionUtils;
import com.vnsolution.virtualassistant.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class ContactActivity extends BaseActivity implements HasAndroidInjector {
    private RecyclerView favouriteContactsRecyclerView;
    private RecyclerView.LayoutManager favouriteContactsLayoutManager;
    private RecyclerViewAdapter<PhoneContact> favouriteContactsAdapter;

    private RecyclerView phoneContactsRecyclerView;
    private RecyclerView.LayoutManager phoneContactsLayoutManager;
    private RecyclerViewAdapter<PhoneContact> phoneContactsAdapter;

    private List<PhoneContact> phoneContacts = new ArrayList<>();
    private FrameLayout progressOverlay;

    @Inject
    public ContactViewModel contactViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.screen_contact);
        setupActionBar();

        progressOverlay =  findViewById(R.id.progress_overlay);
        phoneContacts = contactViewModel.getPhoneContacts();

        setupFavouriteContactsView();
        setupPhoneContactsView();
    }

    private void setupFavouriteContactsView() {
        favouriteContactsRecyclerView = findViewById(R.id.list_favourite_contact);
        favouriteContactsRecyclerView.setHasFixedSize(true);

        favouriteContactsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        favouriteContactsRecyclerView.setLayoutManager(favouriteContactsLayoutManager);

        favouriteContactsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact);
        favouriteContactsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneContact>() {
            @Override
            public void onTap(PhoneContact phoneContact) {
                Intent intent = new Intent(ContactActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneContactId", phoneContact.id);
                startActivity(intent);
            }
            @Override
            public void onLongTap(PhoneContact phoneContact) {
                makePhoneCall(phoneContact.phoneNumbers);
            }
        });
        List<PhoneContact> favouriteContacts = filterContacts(phoneContacts, 1);
        favouriteContactsAdapter.setData(favouriteContacts);
        favouriteContactsRecyclerView.setAdapter(favouriteContactsAdapter);
    }

    private void setupPhoneContactsView() {
        phoneContactsRecyclerView = findViewById(R.id.list_normal_contact);
        phoneContactsRecyclerView.setHasFixedSize(true);

        phoneContactsLayoutManager = new LinearLayoutManager(this);
        phoneContactsRecyclerView.setLayoutManager(phoneContactsLayoutManager);

        phoneContactsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact_full_width);
        phoneContactsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneContact>() {
            @Override
            public void onTap(PhoneContact phoneContact) {
                Intent intent = new Intent(ContactActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneContactId", phoneContact.id);
                startActivity(intent);
            }

            @Override
            public void onAction(PhoneContact phoneContact, String action) {
                makePhoneCall(phoneContact.phoneNumbers);
            }
        });
        phoneContactsAdapter.setData(phoneContacts);
        phoneContactsRecyclerView.setAdapter(phoneContactsAdapter);
    }

    private List<PhoneContact> filterContacts(List<PhoneContact> phoneContacts, int starred) {
        List<PhoneContact> result = new ArrayList<>();
        if (phoneContacts != null) {
            for(PhoneContact phoneContact : phoneContacts) {
                if (phoneContact.starred == starred) {
                    result.add(phoneContact);
                }
            }
        }
        return result;
    }

    @SuppressLint("MissingPermission")
    private void makePhoneCall(String phoneNumber) {
        if (requestPermissions() && phoneNumber != null) {
            String[] splitedNumbers = phoneNumber.split(",");
            if (splitedNumbers != null && splitedNumbers.length > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose phone number")
                        .setItems(splitedNumbers, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + splitedNumbers[which]));
                                startActivity(intent);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        }
    }

    private boolean requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtils.hasPermissions(this, DeviceOptions.PERMISSIONS_MAKE_CALL)) {
                requestPermissions(DeviceOptions.PERMISSIONS_MAKE_CALL, DeviceOptions.PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == StopTrackingOptions.PERMISSIONS_REQUEST_CODE
                || requestCode == DeviceOptions.PERMISSIONS_REQUEST_CODE) {
            //TODO
        }
    }
}
