package com.vnsolution.virtualassistant.ui.reminders;

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
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.phonecontacts.PhoneContactsViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class RemindersActivity extends BaseActivity implements HasAndroidInjector {
    private static final int REQUEST_CODE = 1001;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout progressOverlay;

    private RecyclerViewAdapter<String> remindersAdapter;
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
        setContentView(R.layout.screen_reminders);
        setupActionBar();

        progressOverlay =  findViewById(R.id.progress_overlay);
        recyclerView = findViewById(R.id.list_reminders);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        remindersAdapter = new RecyclerViewAdapter<String>(this, UiOptions.REMINDER_LIST_ALL, R.layout.card_reminder_full_width);
        recyclerView.setAdapter(remindersAdapter);
    }

    private void refreshView() {
        List<String> reminders = new ArrayList<>();
        reminders.add("Reminder 1");
        reminders.add("Reminder 2");
        reminders.add("Reminder 3");
        reminders.add("Reminder 4");
        reminders.add("Reminder 5");
        reminders.add("Reminder 6");
        reminders.add("Reminder 7");
        reminders.add("Reminder 8");
        reminders.add("Reminder 9");
        remindersAdapter.setData(reminders);
    }
}
