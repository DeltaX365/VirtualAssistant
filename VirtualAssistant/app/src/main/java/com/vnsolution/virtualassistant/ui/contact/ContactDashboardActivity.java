package com.vnsolution.virtualassistant.ui.contact;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiSetting;
import com.vnsolution.virtualassistant.model.PhoneCall;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.model.PhoneContactSummary;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.ui.contact.component.ContactDashboardFilterBottomSheetDialogFragment;
import com.vnsolution.virtualassistant.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class ContactDashboardActivity extends BaseActivity implements HasAndroidInjector {
    private TextView totalCallDurationTextView;
    private TextView totalIncomingDurationTextView;
    private TextView totalOutgoingDurationTextView;
    private TextView totalCallNumberTextView;
    private TextView totalIncomingNumberTextView;
    private TextView totalOutgoingNumberTextView;
    private TextView totalMissedNumberTextView;
    private TextView totalBlockedNumberTextView;

    private RecyclerView recentCallsRecyclerView;
    private RecyclerViewAdapter<PhoneCall> recentCallsAdapter;
    private RecyclerView.LayoutManager recentCallsLayoutManager;
    private List<PhoneCall> recentCalls = new ArrayList<>();
    private TextView noRecentCallTextView;

    private RecyclerView mostCallsRecyclerView;
    private RecyclerViewAdapter<PhoneCall> mostCallsAdapter;
    private RecyclerView.LayoutManager mostContactsLayoutManager;
    private List<PhoneCall> mostCalls = new ArrayList<>();
    private TextView noMostCallTextView;

    private RecyclerView recentIncomingCallsRecyclerView;
    private RecyclerViewAdapter<PhoneCall> recentIncomingCallsAdapter;
    private RecyclerView.LayoutManager recentIncomingCallsLayoutManager;
    private List<PhoneCall> recentIncomingCalls = new ArrayList<>();
    private TextView noRecentIncomingCallTextView;

    private RecyclerView recentOutgoingCallsRecyclerView;
    private RecyclerViewAdapter<PhoneCall> recentOutgoingCallsAdapter;
    private RecyclerView.LayoutManager recentOutgoingCallsLayoutManager;
    private List<PhoneCall> recentOutgoingCalls = new ArrayList<>();
    private TextView noRecentOutgoingCallTextView;

    private RecyclerView recentMissedCallsRecyclerView;
    private RecyclerViewAdapter<PhoneCall> recentMissedCallsAdapter;
    private RecyclerView.LayoutManager recentMissedCallsLayoutManager;
    private List<PhoneCall> recentMissedCalls = new ArrayList<>();
    private TextView noRecentMissedCallTextView;

    private RecyclerView recentRejectedCallsRecyclerView;
    private RecyclerViewAdapter<PhoneCall> recentRejectedCallsAdapter;
    private RecyclerView.LayoutManager recentRejectedCallsLayoutManager;
    private List<PhoneCall> recentRejectedCalls = new ArrayList<>();
    private TextView noRecentRejectedCallTextView;

    private RecyclerView recentBlockedCallsRecyclerView;
    private RecyclerViewAdapter<PhoneCall> recentBlockedCallsAdapter;
    private RecyclerView.LayoutManager recentBlockedCallsLayoutManager;
    private List<PhoneCall> recentBlockedCalls = new ArrayList<>();
    private TextView noRecentBlockedCallTextView;

    private RecyclerView mostIncomingCallsRecyclerView;
    private RecyclerViewAdapter<PhoneCall> mostIncomingCallsAdapter;
    private RecyclerView.LayoutManager mostIncomingCallsLayoutManager;
    private List<PhoneCall> mostIncomingCalls = new ArrayList<>();
    private TextView noMostIncomingCallTextView;

    private RecyclerView mostOutgoingCallsRecyclerView;
    private RecyclerViewAdapter<PhoneCall> mostOutgoingCallsAdapter;
    private RecyclerView.LayoutManager mostOutgoingCallsLayoutManager;
    private List<PhoneCall> mostOutgoingCalls = new ArrayList<>();
    private TextView noMostOutgoingCallTextView;

    private List<PhoneContact> phoneContacts;
    private boolean includeStranger = false;

    @Inject
    public ContactViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        refreshView();
    }

    private void setupView() {
        setContentView(R.layout.screen_contact_dashboard);
        setupActionBar();

        setupContactStatView();
        setupRecentCallsView();
        setupMostCallsView();
        setupRecentIncomingCallsView();
        setupRecentOutgoingCallsView();
        setupRecentMissedCallsView();
        setupRecentRejectedCallsView();
        setupRecentBlockedCallsView();
        setupMostIncomingCallsView();
        setupMostOutgoingCallsView();
    }

    private void refreshView() {
        refreshContactStatView();
        refreshRecentCallsView();
        refreshMostCallsView();
        refreshRecentIncomingCallsView();
        refreshRecentOutgoingCallsView();
        refreshRecentMissedCallsView();
        refreshRecentRejectedCallsView();
        refreshRecentBlockedCallsView();
        refreshMostIncomingCallsView();
        refreshMostOutgoingCallsView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact_dashboard_options, menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_filter:
                ContactDashboardFilterBottomSheetDialogFragment bottomSheetDialog = new ContactDashboardFilterBottomSheetDialogFragment();
                bottomSheetDialog.listener(new ContactDashboardFilterBottomSheetDialogFragment.Listener() {
                    @Override
                    public void onChanged(ContactDashboardFilterBottomSheetDialogFragment.Filter filterOption) {
                        boolean includeStrangerNew = (filterOption == ContactDashboardFilterBottomSheetDialogFragment.Filter.INCLUDE_STRANGERS)? true : false;
                        if (includeStranger != includeStrangerNew) {
                            includeStranger = includeStrangerNew;
                            refreshView();
                        }
                    }
                });
                bottomSheetDialog.setValue(includeStranger? ContactDashboardFilterBottomSheetDialogFragment.Filter.INCLUDE_STRANGERS : ContactDashboardFilterBottomSheetDialogFragment.Filter.EXCLUDE_STRANGERS);
                bottomSheetDialog.show(getSupportFragmentManager(), "ContactDashboardFilterBottomSheetDialogFragment");
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
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
    }

    private void refreshContactStatView() {
        int totalDuration = 0;
        int totalNumber = 0;
        List<PhoneContactSummary> contactSummaryList = vm.getContactSummaryByNumbers(null);
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

    private void setupRecentCallsView() {
        recentCallsRecyclerView = findViewById(R.id.list_recent_call);
        recentCallsRecyclerView.setHasFixedSize(true);

        recentCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentCallsRecyclerView.setLayoutManager(recentCallsLayoutManager);

        recentCallsAdapter = new RecyclerViewAdapter<PhoneCall>(this, UiSetting.CONTACT_LIST_ALL, R.layout.card_phone_call);
        recentCallsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneCall>() {
            @Override
            public void onTap(PhoneCall phoneCall) {
                Intent intent = new Intent(ContactDashboardActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneCallId", phoneCall.id);
                intent.putExtra("PhoneContactId", (phoneCall.contactRef != null)? phoneCall.contactRef.id : "");
                startActivity(intent);
            }
        });
        recentCallsRecyclerView.setAdapter(recentCallsAdapter);
        noRecentCallTextView = findViewById(R.id.no_recent_contact);
    }

    private void refreshRecentCallsView() {
        recentCalls = vm.getRecentCalls(0, includeStranger); // no filter type
        assignContactToPhoneCalls(recentCalls);
        recentCallsAdapter.setData(recentCalls);

        int visibility = (recentCalls != null && recentCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noRecentCallTextView.setVisibility(visibility);
    }

    private void setupMostCallsView() {
        mostCallsRecyclerView = findViewById(R.id.list_most_call);
        mostCallsRecyclerView.setHasFixedSize(true);

        mostContactsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mostCallsRecyclerView.setLayoutManager(mostContactsLayoutManager);

        mostCallsAdapter = new RecyclerViewAdapter<PhoneCall>(this, UiSetting.CONTACT_LIST_ALL, R.layout.card_phone_call);
        mostCallsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneCall>() {
            @Override
            public void onTap(PhoneCall phoneCall) {
                Intent intent = new Intent(ContactDashboardActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneCallId", phoneCall.id);
                intent.putExtra("PhoneContactId", (phoneCall.contactRef != null)? phoneCall.contactRef.id : "");
                startActivity(intent);
            }
        });
        mostCallsRecyclerView.setAdapter(mostCallsAdapter);
        noMostCallTextView = findViewById(R.id.no_most_contact);
    }

    private void refreshMostCallsView() {
        mostCalls = vm.getMostCalls(0, includeStranger); // no filter type
        assignContactToPhoneCalls(mostCalls);
        mostCallsAdapter.setData(mostCalls);

        int visibility = (mostCalls != null && mostCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noMostCallTextView.setVisibility(visibility);
    }

    private void setupRecentIncomingCallsView() {
        recentIncomingCallsRecyclerView = findViewById(R.id.list_recent_incoming_call);
        recentIncomingCallsRecyclerView.setHasFixedSize(true);

        recentIncomingCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentIncomingCallsRecyclerView.setLayoutManager(recentIncomingCallsLayoutManager);

        recentIncomingCallsAdapter = new RecyclerViewAdapter<PhoneCall>(this, UiSetting.CONTACT_LIST_ALL, R.layout.card_phone_call);
        recentIncomingCallsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneCall>() {
            @Override
            public void onTap(PhoneCall phoneCall) {
                Intent intent = new Intent(ContactDashboardActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneCallId", phoneCall.id);
                intent.putExtra("PhoneContactId", (phoneCall.contactRef != null)? phoneCall.contactRef.id : "");
                startActivity(intent);
            }
        });
        recentIncomingCallsRecyclerView.setAdapter(recentIncomingCallsAdapter);
        noRecentIncomingCallTextView = findViewById(R.id.no_recent_incoming_call);
    }

    private void refreshRecentIncomingCallsView() {
        recentIncomingCalls = vm.getRecentCalls(CallLog.Calls.INCOMING_TYPE, includeStranger);
        assignContactToPhoneCalls(recentIncomingCalls);
        recentIncomingCallsAdapter.setData(recentIncomingCalls);

        int visibility = (recentIncomingCalls != null && recentIncomingCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noRecentIncomingCallTextView.setVisibility(visibility);
    }

    private void setupRecentOutgoingCallsView() {
        recentOutgoingCallsRecyclerView = findViewById(R.id.list_recent_outgoing_call);
        recentOutgoingCallsRecyclerView.setHasFixedSize(true);

        recentOutgoingCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentOutgoingCallsRecyclerView.setLayoutManager(recentOutgoingCallsLayoutManager);

        recentOutgoingCallsAdapter = new RecyclerViewAdapter<PhoneCall>(this, UiSetting.CONTACT_LIST_ALL, R.layout.card_phone_call);
        recentOutgoingCallsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneCall>() {
            @Override
            public void onTap(PhoneCall phoneCall) {
                Intent intent = new Intent(ContactDashboardActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneCallId", phoneCall.id);
                intent.putExtra("PhoneContactId", (phoneCall.contactRef != null)? phoneCall.contactRef.id : "");
                startActivity(intent);
            }
        });
        recentOutgoingCallsRecyclerView.setAdapter(recentOutgoingCallsAdapter);
        noRecentOutgoingCallTextView = findViewById(R.id.no_recent_outgoing_call);
    }

    private void refreshRecentOutgoingCallsView() {
        recentOutgoingCalls = vm.getRecentCalls(CallLog.Calls.OUTGOING_TYPE, includeStranger);
        assignContactToPhoneCalls(recentOutgoingCalls);
        recentOutgoingCallsAdapter.setData(recentOutgoingCalls);

        int visibility = (recentOutgoingCalls != null && recentOutgoingCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noRecentOutgoingCallTextView.setVisibility(visibility);
    }

    private void setupRecentMissedCallsView() {
        recentMissedCallsRecyclerView = findViewById(R.id.list_recent_missed_call);
        recentMissedCallsRecyclerView.setHasFixedSize(true);

        recentMissedCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentMissedCallsRecyclerView.setLayoutManager(recentMissedCallsLayoutManager);

        recentMissedCallsAdapter = new RecyclerViewAdapter<PhoneCall>(this, UiSetting.CONTACT_LIST_ALL, R.layout.card_phone_call);
        recentMissedCallsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneCall>() {
            @Override
            public void onTap(PhoneCall phoneCall) {
                Intent intent = new Intent(ContactDashboardActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneCallId", phoneCall.id);
                intent.putExtra("PhoneContactId", (phoneCall.contactRef != null)? phoneCall.contactRef.id : "");
                startActivity(intent);
            }
        });
        recentMissedCallsRecyclerView.setAdapter(recentMissedCallsAdapter);
        noRecentMissedCallTextView = findViewById(R.id.no_missed_call);
    }

    private void refreshRecentMissedCallsView() {
        recentMissedCalls = vm.getRecentCalls(CallLog.Calls.MISSED_TYPE, includeStranger);
        assignContactToPhoneCalls(recentMissedCalls);
        recentMissedCallsAdapter.setData(recentMissedCalls);

        int visibility = (recentMissedCalls != null && recentMissedCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noRecentMissedCallTextView.setVisibility(visibility);
    }

    private void setupRecentRejectedCallsView() {
        recentRejectedCallsRecyclerView = findViewById(R.id.list_recent_rejected_call);
        recentRejectedCallsRecyclerView.setHasFixedSize(true);

        recentRejectedCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentRejectedCallsRecyclerView.setLayoutManager(recentRejectedCallsLayoutManager);

        recentRejectedCallsAdapter = new RecyclerViewAdapter<PhoneCall>(this, UiSetting.CONTACT_LIST_ALL, R.layout.card_phone_call);
        recentRejectedCallsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneCall>() {
            @Override
            public void onTap(PhoneCall phoneCall) {
                Intent intent = new Intent(ContactDashboardActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneCallId", phoneCall.id);
                intent.putExtra("PhoneContactId", (phoneCall.contactRef != null)? phoneCall.contactRef.id : "");
                startActivity(intent);
            }
        });
        recentRejectedCallsRecyclerView.setAdapter(recentRejectedCallsAdapter);
        noRecentRejectedCallTextView = findViewById(R.id.no_recent_rejected_call);
    }

    private void refreshRecentRejectedCallsView() {
        recentRejectedCalls = vm.getRecentCalls(CallLog.Calls.REJECTED_TYPE, includeStranger);
        assignContactToPhoneCalls(recentRejectedCalls);
        recentRejectedCallsAdapter.setData(recentRejectedCalls);

        int visibility = (recentRejectedCalls != null && recentRejectedCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noRecentRejectedCallTextView.setVisibility(visibility);
    }

    private void setupRecentBlockedCallsView() {
        recentBlockedCallsRecyclerView = findViewById(R.id.list_recent_blocked_call);
        recentBlockedCallsRecyclerView.setHasFixedSize(true);

        recentBlockedCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentBlockedCallsRecyclerView.setLayoutManager(recentBlockedCallsLayoutManager);

        recentBlockedCallsAdapter = new RecyclerViewAdapter<PhoneCall>(this, UiSetting.CONTACT_LIST_ALL, R.layout.card_phone_call);
        recentBlockedCallsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneCall>() {
            @Override
            public void onTap(PhoneCall phoneCall) {
                Intent intent = new Intent(ContactDashboardActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneCallId", phoneCall.id);
                intent.putExtra("PhoneContactId", (phoneCall.contactRef != null)? phoneCall.contactRef.id : "");
                startActivity(intent);
            }
        });
        recentBlockedCallsRecyclerView.setAdapter(recentBlockedCallsAdapter);
        noRecentBlockedCallTextView = findViewById(R.id.no_recent_blocked_call);
    }

    private void refreshRecentBlockedCallsView() {
        recentBlockedCalls = vm.getRecentCalls(CallLog.Calls.BLOCKED_TYPE, includeStranger);
        assignContactToPhoneCalls(recentBlockedCalls);
        recentBlockedCallsAdapter.setData(recentBlockedCalls);

        int visibility = (recentBlockedCalls != null && recentBlockedCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noRecentBlockedCallTextView.setVisibility(visibility);
    }

    private void setupMostIncomingCallsView() {
        mostIncomingCallsRecyclerView = findViewById(R.id.list_most_incoming_call);
        mostIncomingCallsRecyclerView.setHasFixedSize(true);

        mostIncomingCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mostIncomingCallsRecyclerView.setLayoutManager(mostIncomingCallsLayoutManager);

        mostIncomingCallsAdapter = new RecyclerViewAdapter<PhoneCall>(this, UiSetting.CONTACT_LIST_ALL, R.layout.card_phone_call);
        mostIncomingCallsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneCall>() {
            @Override
            public void onTap(PhoneCall phoneCall) {
                Intent intent = new Intent(ContactDashboardActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneCallId", phoneCall.id);
                intent.putExtra("PhoneContactId", (phoneCall.contactRef != null)? phoneCall.contactRef.id : "");
                startActivity(intent);
            }
        });
        mostIncomingCallsRecyclerView.setAdapter(mostIncomingCallsAdapter);
        noMostIncomingCallTextView = findViewById(R.id.no_most_incoming_call);
    }

    private void refreshMostIncomingCallsView() {
        mostIncomingCalls = vm.getMostCalls(CallLog.Calls.INCOMING_TYPE, includeStranger);
        assignContactToPhoneCalls(mostIncomingCalls);
        mostIncomingCallsAdapter.setData(mostIncomingCalls);

        int visibility = (mostIncomingCalls != null && mostIncomingCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noMostIncomingCallTextView.setVisibility(visibility);
    }

    private void setupMostOutgoingCallsView() {
        mostOutgoingCallsRecyclerView = findViewById(R.id.list_most_outgoing_call);
        mostOutgoingCallsRecyclerView.setHasFixedSize(true);

        mostOutgoingCallsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mostOutgoingCallsRecyclerView.setLayoutManager(mostOutgoingCallsLayoutManager);

        mostOutgoingCallsAdapter = new RecyclerViewAdapter<PhoneCall>(this, UiSetting.CONTACT_LIST_ALL, R.layout.card_phone_call);
        mostOutgoingCallsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneCall>() {
            @Override
            public void onTap(PhoneCall phoneCall) {
                Intent intent = new Intent(ContactDashboardActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneCallId", phoneCall.id);
                intent.putExtra("PhoneContactId", (phoneCall.contactRef != null)? phoneCall.contactRef.id : "");
                startActivity(intent);
            }
        });
        mostOutgoingCallsRecyclerView.setAdapter(mostOutgoingCallsAdapter);
        noMostOutgoingCallTextView = findViewById(R.id.no_most_outgoing_call);
    }

    private void refreshMostOutgoingCallsView() {
        mostOutgoingCalls = vm.getMostCalls(CallLog.Calls.OUTGOING_TYPE, includeStranger);
        assignContactToPhoneCalls(mostOutgoingCalls);
        mostOutgoingCallsAdapter.setData(mostOutgoingCalls);

        int visibility = (mostOutgoingCalls != null && mostOutgoingCalls.size() > 0) ? View.GONE : View.VISIBLE;
        noMostOutgoingCallTextView.setVisibility(visibility);
    }

    private void assignContactToPhoneCalls(List<PhoneCall> phoneCalls) {
        if (phoneContacts == null) {
            phoneContacts = vm.getPhoneContacts(this,false,false);
        }
        vm.assignContactToPhoneCalls(phoneCalls, phoneContacts);
    }
}
