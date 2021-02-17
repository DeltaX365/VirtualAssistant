package com.vnsolution.virtualassistant.ui.contacts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiSetting;
import com.vnsolution.virtualassistant.model.PhoneCall;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.model.PhoneContactSummary;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.util.BitmapUtils;
import com.vnsolution.virtualassistant.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class ContactViewActivity extends BaseActivity implements HasAndroidInjector {
    private ImageView avatarImageView;
    private TextView displayNameTextView;
    private TextView phoneNumberTextView;
    private TextView lastUpdatedTimeTextView;
    private PhoneCall phoneCall;
    private PhoneContact phoneContact;

    private TextView totalCallDurationTextView;
    private TextView totalIncomingDurationTextView;
    private TextView totalOutgoingDurationTextView;
    private TextView totalCallNumberTextView;
    private TextView totalIncomingNumberTextView;
    private TextView totalOutgoingNumberTextView;
    private TextView totalMissedNumberTextView;
    private TextView totalBlockedNumberTextView;

    private RecyclerView phoneCallHistoryRecyclerView;
    private RecyclerViewAdapter<PhoneCall> phoneCallHistoryAdapter;
    private RecyclerView.LayoutManager phoneCallHistoryLayoutManager;
    private List<PhoneCall> phoneCallHistory = new ArrayList<>();
    private TextView noPhoneCallHistoryTextView;

    private FrameLayout progressOverlay;
    private ContactViewModel vm;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = ViewModelProviders.of(this, viewModelFactory).get(ContactViewModel.class);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.screen_contact_view);
        setupActionBar();

        progressOverlay =  findViewById(R.id.progress_overlay);

        Intent intent = getIntent();
        if (intent != null) {
            String phoneContactId = intent.getStringExtra("PhoneContactId");
            if (phoneContactId != null && phoneContactId != "") {
                phoneContact = vm.getPhoneContactById(phoneContactId);
            }
            String phoneCallId = intent.getStringExtra("PhoneCallId");
            if (phoneCallId != null && phoneCallId != "") {
                phoneCall = vm.getPhoneCallById(phoneCallId);
                if (phoneCall != null && phoneContact != null) {
                    phoneCall.contactRef = phoneContact;
                }
            }
        }

        setupBasicInfoView();
        setupContactStatView();
        setupContactHistoryView();
    }

    private void setupBasicInfoView() {
        avatarImageView = findViewById(R.id.contact_avatar);
        String contactName = (phoneContact != null) ? phoneContact.name : "";
        Bitmap avatar = BitmapUtils.generateBitmapByText(this, contactName, 12, 60, 60);
        Glide.with(this).load(avatar).apply(RequestOptions.circleCropTransform()).circleCrop().into(avatarImageView);

        displayNameTextView = findViewById(R.id.contact_name);
        displayNameTextView.setText(contactName);

        phoneNumberTextView = findViewById(R.id.phone_number);
        String phoneNumbers = (phoneContact != null) ? phoneContact.phoneNumbers : ((phoneCall != null) ? phoneCall.phoneNumber : "");
        phoneNumberTextView.setText(phoneNumbers);

        lastUpdatedTimeTextView = findViewById(R.id.contact_last_updated_time);
        long lastUpdatedTime = (phoneContact != null) ? phoneContact.lastUpdatedTime : 0;
        lastUpdatedTimeTextView.setText(DateUtils.formatDateTime(this, lastUpdatedTime, DateUtils.FORMAT_ABBREV_ALL));
    }

    private void setupContactStatView() {
        totalCallDurationTextView = findViewById(R.id.total_call_duration);
        totalIncomingDurationTextView = findViewById(R.id.total_incoming_duration);
        totalOutgoingDurationTextView = findViewById(R.id.total_outgoing_duration);
        totalCallNumberTextView = findViewById(R.id.total_call_number);
        totalIncomingNumberTextView = findViewById(R.id.total_incoming_number);
        totalOutgoingNumberTextView = findViewById(R.id.total_outgoing_number);
        totalMissedNumberTextView = findViewById(R.id.total_missed_number);
        totalBlockedNumberTextView = findViewById(R.id.total_blocked_number);

        int totalDuration = 0;
        int totalNumber = 0;
        String phoneNumbers = (phoneContact != null) ? phoneContact.phoneNumbers : ((phoneCall != null) ? phoneCall.phoneNumber : "");
        List<PhoneContactSummary> contactSummaryList = vm.getContactSummaryByNumbers(phoneNumbers);
        for (PhoneContactSummary contactSummary : contactSummaryList) {
            totalDuration += contactSummary.duration;
            totalNumber += contactSummary.total;
            switch (contactSummary.type) {
                case CallLog.Calls.INCOMING_TYPE:
                    totalIncomingDurationTextView.setText(DateTimeUtils.formatElapsedTime(contactSummary.duration));
                    totalIncomingNumberTextView.setText(String.valueOf(contactSummary.total));
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    totalOutgoingDurationTextView.setText(DateTimeUtils.formatElapsedTime(contactSummary.duration));
                    totalOutgoingNumberTextView.setText(String.valueOf(contactSummary.total));
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    totalMissedNumberTextView.setText(String.valueOf(contactSummary.total));
                    break;
                case CallLog.Calls.REJECTED_TYPE:
                    break;
                case CallLog.Calls.BLOCKED_TYPE:
                    totalBlockedNumberTextView.setText(String.valueOf(contactSummary.total));
                    break;
            }
        }
        totalCallDurationTextView.setText(DateTimeUtils.formatElapsedTime(totalDuration));
        totalCallNumberTextView.setText(String.valueOf(totalNumber));
    }

    private void setupContactHistoryView() {
        phoneCallHistoryRecyclerView = findViewById(R.id.list_contact_history);
        phoneCallHistoryRecyclerView.setHasFixedSize(true);

        phoneCallHistoryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        phoneCallHistoryRecyclerView.setLayoutManager(phoneCallHistoryLayoutManager);

        phoneCallHistoryAdapter = new RecyclerViewAdapter<PhoneCall>(this, UiSetting.CONTACT_LIST_ALL, R.layout.item_phone_call);
        phoneCallHistoryRecyclerView.setAdapter(phoneCallHistoryAdapter);

        String phoneNumbers = (phoneContact != null) ? phoneContact.phoneNumbers : ((phoneCall != null) ? phoneCall.phoneNumber : "");
        phoneCallHistory = vm.getCallHistoryByNumbers(phoneNumbers);
        assignContactToPhoneCalls(phoneCallHistory);
        phoneCallHistoryAdapter.setData(phoneCallHistory);

        noPhoneCallHistoryTextView = findViewById(R.id.no_contact_history);
        int visibility = (phoneCallHistory != null && phoneCallHistory.size() > 0) ? View.GONE : View.VISIBLE;
        noPhoneCallHistoryTextView.setVisibility(visibility);
    }

    private void assignContactToPhoneCalls(List<PhoneCall> phoneCalls) {
        if (phoneCalls != null && phoneContact != null) {
            for (PhoneCall phoneCall : phoneCalls) {
                phoneCall.contactRef = phoneContact;
            }
        }
    }
}
