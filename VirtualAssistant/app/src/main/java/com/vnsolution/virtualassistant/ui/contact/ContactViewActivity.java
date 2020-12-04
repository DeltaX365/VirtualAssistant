package com.vnsolution.virtualassistant.ui.contact;

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
import com.vnsolution.virtualassistant.common.UiOptions;
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
    private PhoneContact phoneContact;

    private TextView totalCallDurationTextView;
    private TextView totalIncomingDurationTextView;
    private TextView totalOutgoingDurationTextView;
    private TextView totalCallNumberTextView;
    private TextView totalIncomingNumberTextView;
    private TextView totalOutgoingNumberTextView;
    private TextView totalMissedNumberTextView;
    private TextView totalBlockedNumberTextView;

    private RecyclerView contactHistoryRecyclerView;
    private RecyclerViewAdapter<PhoneContact> contactHistoryAdapter;
    private RecyclerView.LayoutManager contactHistoryLayoutManager;
    private List<PhoneContact> contactHistory = new ArrayList<>();
    private TextView noContactHistoryTextView;

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

        Intent intent = getIntent();
        if (intent != null) {
            String phoneContactId = intent.getStringExtra("Id");
            if (phoneContactId != null && phoneContactId != "") {
                phoneContact = vm.getContactById(phoneContactId);
            }
        }
        progressOverlay =  findViewById(R.id.progress_overlay);

        setupBasicInfoView();
        setupContactStatView();
        setupContactHistoryView();
    }

    private void setupBasicInfoView() {
        avatarImageView = findViewById(R.id.contact_avatar);
        displayNameTextView = findViewById(R.id.contact_name);
        phoneNumberTextView = findViewById(R.id.contact_phone_number);
        lastUpdatedTimeTextView = findViewById(R.id.contact_last_updated_time);

        if (phoneContact != null) {
            Bitmap avatar = BitmapUtils.generateBitmapByText(this, phoneContact.name, 12, 60, 60);
            Glide.with(this).load(avatar).apply(RequestOptions.circleCropTransform()).circleCrop().into(avatarImageView);

            displayNameTextView.setText(phoneContact.name);
            phoneNumberTextView.setText(phoneContact.phoneNumbers);
            lastUpdatedTimeTextView.setText(DateUtils.formatDateTime(this, phoneContact.lastUpdatedTime, DateUtils.FORMAT_ABBREV_ALL));
        }
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

        if (phoneContact != null) {
            int totalDuration = 0;
            int totalNumber = 0;
            List<PhoneContactSummary> contactSummaryList = vm.getContactSummaryByNumbers(phoneContact.phoneNumbers);
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
    }

    private void setupContactHistoryView() {
        contactHistoryRecyclerView = findViewById(R.id.list_contact_history);
        noContactHistoryTextView = findViewById(R.id.no_contact_history);

        if (phoneContact != null) {
            contactHistoryRecyclerView.setHasFixedSize(true);

            contactHistoryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            contactHistoryRecyclerView.setLayoutManager(contactHistoryLayoutManager);

            contactHistoryAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact_full_width);
            contactHistoryRecyclerView.setAdapter(contactHistoryAdapter);

            contactHistory = vm.getCallHistoryByNumbers(phoneContact.phoneNumbers);
            contactHistoryAdapter.setData(contactHistory);

            int visibility = (contactHistory != null && contactHistory.size() > 0) ? View.GONE : View.VISIBLE;
            noContactHistoryTextView.setVisibility(visibility);
        } else {
            noContactHistoryTextView.setVisibility(View.VISIBLE);
        }
    }
}
