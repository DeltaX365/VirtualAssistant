package com.vnsolution.virtualassistant.ui.contact;

import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.model.PhoneContactSummary;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class ContactDashboardActivity extends BaseActivity implements HasAndroidInjector {
    private RecyclerView recentContactsRecyclerView;
    private RecyclerViewAdapter<PhoneContact> recentContactsAdapter;
    private RecyclerView.LayoutManager recentContactsLayoutManager;
    private List<PhoneContact> recentContacts = new ArrayList<>();
    private TextView noRecentContactTextView;

    private RecyclerView mostContactsRecyclerView;
    private RecyclerViewAdapter<PhoneContact> mostContactsAdapter;
    private RecyclerView.LayoutManager mostContactsLayoutManager;
    private List<PhoneContact> mostContacts = new ArrayList<>();
    private TextView noMostContactTextView;

    private TextView totalCallDurationTextView;
    private TextView totalIncomingDurationTextView;
    private TextView totalOutgoingDurationTextView;
    private TextView totalCallNumberTextView;
    private TextView totalIncomingNumberTextView;
    private TextView totalOutgoingNumberTextView;
    private TextView totalMissedNumberTextView;
    private TextView totalBlockedNumberTextView;

    private RecyclerView recentIncomingCallsRecyclerView;
    private RecyclerViewAdapter<PhoneContact> recentIncomingCallsAdapter;
    private RecyclerView.LayoutManager recentIncomingCallsLayoutManager;
    private List<PhoneContact> recentIncomingCalls = new ArrayList<>();
    private TextView noRecentIncomingCallTextView;

    private RecyclerView recentOutgoingCallsRecyclerView;
    private RecyclerViewAdapter<PhoneContact> recentOutgoingCallsAdapter;
    private RecyclerView.LayoutManager recentOutgoingCallsLayoutManager;
    private List<PhoneContact> recentOutgoingCalls = new ArrayList<>();
    private TextView noRecentOutgoingCallTextView;

    private RecyclerView recentMissedCallsRecyclerView;
    private RecyclerViewAdapter<PhoneContact> recentMissedCallsAdapter;
    private RecyclerView.LayoutManager recentMissedCallsLayoutManager;
    private List<PhoneContact> recentMissedCalls = new ArrayList<>();
    private TextView noRecentMissedCallTextView;

    private RecyclerView recentRejectedCallsRecyclerView;
    private RecyclerViewAdapter<PhoneContact> recentRejectedCallsAdapter;
    private RecyclerView.LayoutManager recentRejectedCallsLayoutManager;
    private List<PhoneContact> recentRejectedCalls = new ArrayList<>();
    private TextView noRecentRejectedCallTextView;

    private RecyclerView recentBlockedCallsRecyclerView;
    private RecyclerViewAdapter<PhoneContact> recentBlockedCallsAdapter;
    private RecyclerView.LayoutManager recentBlockedCallsLayoutManager;
    private List<PhoneContact> recentBlockedCalls = new ArrayList<>();
    private TextView noRecentBlockedCallTextView;

    private RecyclerView mostIncomingCallsRecyclerView;
    private RecyclerViewAdapter<PhoneContact> mostIncomingCallsAdapter;
    private RecyclerView.LayoutManager mostIncomingCallsLayoutManager;
    private List<PhoneContact> mostIncomingCalls = new ArrayList<>();
    private TextView noMostIncomingCallTextView;

    private RecyclerView mostOutgoingCallsRecyclerView;
    private RecyclerViewAdapter<PhoneContact> mostOutgoingCallsAdapter;
    private RecyclerView.LayoutManager mostOutgoingCallsLayoutManager;
    private List<PhoneContact> mostOutgoingCalls = new ArrayList<>();
    private TextView noMostOutgoingCallTextView;

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
        setContentView(R.layout.screen_contact_dashboard);
        setupActionBar();

        progressOverlay =  findViewById(R.id.progress_overlay);

        setupRecentContactsView();
        setupMostContactsView();

        setupContactStatView();

        setupRecentIncomingCallsView();
        setupRecentOutgoingCallsView();
        setupRecentMissedCallsView();
        setupRecentRejectedCallsView();
        setupRecentBlockedCallsView();

        setupMostIncomingCallsView();
        setupMostOutgoingCallsView();
    }

    private void setupRecentContactsView() {
        recentContactsRecyclerView = findViewById(R.id.list_recent_contact);
        recentContactsRecyclerView.setHasFixedSize(true);

        recentContactsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentContactsRecyclerView.setLayoutManager(recentContactsLayoutManager);

        recentContactsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact);
        recentContactsRecyclerView.setAdapter(recentContactsAdapter);

        recentContacts = vm.getRecentContactsByType(0); // no filter type
        recentContactsAdapter.setData(recentContacts);

        noRecentContactTextView = findViewById(R.id.no_recent_contact);
        int visibility = (recentContacts != null && recentContacts.size() > 0) ? View.GONE : View.VISIBLE;
        noRecentContactTextView.setVisibility(visibility);
    }

    private void setupMostContactsView() {
        mostContactsRecyclerView = findViewById(R.id.list_most_contact);
        mostContactsRecyclerView.setHasFixedSize(true);

        mostContactsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mostContactsRecyclerView.setLayoutManager(mostContactsLayoutManager);

        mostContactsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact);
        mostContactsRecyclerView.setAdapter(mostContactsAdapter);

        mostContacts = vm.getMostContactsByType(0); // no filter type
        mostContactsAdapter.setData(mostContacts);

        noMostContactTextView = findViewById(R.id.no_most_contact);
        int visibility = (mostContacts != null && mostContacts.size() > 0) ? View.GONE : View.VISIBLE;
        noMostContactTextView.setVisibility(visibility);
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
        List<PhoneContactSummary> contactSummaryList = vm.getContactSummary();
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

    private void setupRecentIncomingCallsView() {
        recentIncomingCallsRecyclerView = findViewById(R.id.list_recent_incoming_call);
        recentIncomingCallsRecyclerView.setHasFixedSize(true);

        recentIncomingCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentIncomingCallsRecyclerView.setLayoutManager(recentIncomingCallsLayoutManager);

        recentIncomingCallsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact);
        recentIncomingCallsRecyclerView.setAdapter(recentIncomingCallsAdapter);

        recentIncomingCalls = vm.getRecentContactsByType(CallLog.Calls.INCOMING_TYPE);
        recentIncomingCallsAdapter.setData(recentIncomingCalls);

        noRecentIncomingCallTextView = findViewById(R.id.no_recent_incoming_call);
        int visibility = (recentIncomingCalls != null && recentIncomingCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noRecentIncomingCallTextView.setVisibility(visibility);
    }

    private void setupRecentOutgoingCallsView() {
        recentOutgoingCallsRecyclerView = findViewById(R.id.list_recent_outgoing_call);
        recentOutgoingCallsRecyclerView.setHasFixedSize(true);

        recentOutgoingCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentOutgoingCallsRecyclerView.setLayoutManager(recentOutgoingCallsLayoutManager);

        recentOutgoingCallsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact);
        recentOutgoingCallsRecyclerView.setAdapter(recentOutgoingCallsAdapter);

        recentOutgoingCalls = vm.getRecentContactsByType(CallLog.Calls.OUTGOING_TYPE);
        recentOutgoingCallsAdapter.setData(recentOutgoingCalls);

        noRecentOutgoingCallTextView = findViewById(R.id.no_recent_outgoing_call);
        int visibility = (recentOutgoingCalls != null && recentOutgoingCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noRecentOutgoingCallTextView.setVisibility(visibility);
    }

    private void setupRecentMissedCallsView() {
        recentMissedCallsRecyclerView = findViewById(R.id.list_recent_missed_call);
        recentMissedCallsRecyclerView.setHasFixedSize(true);

        recentMissedCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentMissedCallsRecyclerView.setLayoutManager(recentMissedCallsLayoutManager);

        recentMissedCallsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact);
        recentMissedCallsRecyclerView.setAdapter(recentMissedCallsAdapter);

        recentMissedCalls = vm.getRecentContactsByType(CallLog.Calls.MISSED_TYPE);
        recentMissedCallsAdapter.setData(recentMissedCalls);

        noRecentMissedCallTextView = findViewById(R.id.no_missed_call);
        int visibility = (recentMissedCalls != null && recentMissedCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noRecentMissedCallTextView.setVisibility(visibility);
    }

    private void setupRecentRejectedCallsView() {
        recentRejectedCallsRecyclerView = findViewById(R.id.list_recent_rejected_call);
        recentRejectedCallsRecyclerView.setHasFixedSize(true);

        recentRejectedCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentRejectedCallsRecyclerView.setLayoutManager(recentRejectedCallsLayoutManager);

        recentRejectedCallsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact);
        recentRejectedCallsRecyclerView.setAdapter(recentRejectedCallsAdapter);

        recentRejectedCalls = vm.getRecentContactsByType(CallLog.Calls.REJECTED_TYPE);
        recentRejectedCallsAdapter.setData(recentRejectedCalls);

        noRecentRejectedCallTextView = findViewById(R.id.no_recent_rejected_call);
        int visibility = (recentRejectedCalls != null && recentRejectedCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noRecentRejectedCallTextView.setVisibility(visibility);
    }

    private void setupRecentBlockedCallsView() {
        recentBlockedCallsRecyclerView = findViewById(R.id.list_recent_blocked_call);
        recentBlockedCallsRecyclerView.setHasFixedSize(true);

        recentBlockedCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentBlockedCallsRecyclerView.setLayoutManager(recentBlockedCallsLayoutManager);

        recentBlockedCallsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact);
        recentBlockedCallsRecyclerView.setAdapter(recentBlockedCallsAdapter);

        recentBlockedCalls = vm.getRecentContactsByType(CallLog.Calls.BLOCKED_TYPE);
        recentBlockedCallsAdapter.setData(recentBlockedCalls);

        noRecentBlockedCallTextView = findViewById(R.id.no_recent_blocked_call);
        int visibility = (recentBlockedCalls != null && recentBlockedCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noRecentBlockedCallTextView.setVisibility(visibility);
    }

    private void setupMostIncomingCallsView() {
        mostIncomingCallsRecyclerView = findViewById(R.id.list_most_incoming_call);
        mostIncomingCallsRecyclerView.setHasFixedSize(true);

        mostIncomingCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mostIncomingCallsRecyclerView.setLayoutManager(mostIncomingCallsLayoutManager);

        mostIncomingCallsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact);
        mostIncomingCallsRecyclerView.setAdapter(mostIncomingCallsAdapter);

        mostIncomingCalls = vm.getMostContactsByType(CallLog.Calls.INCOMING_TYPE);
        mostIncomingCallsAdapter.setData(mostIncomingCalls);

        noMostIncomingCallTextView = findViewById(R.id.no_most_incoming_call);
        int visibility = (mostIncomingCalls != null && mostIncomingCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noMostIncomingCallTextView.setVisibility(visibility);
    }

    private void setupMostOutgoingCallsView() {
        mostOutgoingCallsRecyclerView = findViewById(R.id.list_most_outgoing_call);
        mostOutgoingCallsRecyclerView.setHasFixedSize(true);

        mostOutgoingCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mostOutgoingCallsRecyclerView.setLayoutManager(mostOutgoingCallsLayoutManager);

        mostOutgoingCallsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiOptions.CONTACT_LIST_ALL, R.layout.card_contact);
        mostOutgoingCallsRecyclerView.setAdapter(mostOutgoingCallsAdapter);

        mostOutgoingCalls = vm.getMostContactsByType(CallLog.Calls.OUTGOING_TYPE);
        mostOutgoingCallsAdapter.setData(mostOutgoingCalls);

        noMostOutgoingCallTextView = findViewById(R.id.no_most_outgoing_call);
        int visibility = (mostOutgoingCalls != null && mostOutgoingCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noMostOutgoingCallTextView.setVisibility(visibility);
    }
}
