package com.vnsolution.virtualassistant.ui.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.model.Reminder;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.ui.contact.ContactViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class ReminderListActivity extends BaseActivity implements HasAndroidInjector {
    private RecyclerView reminderListRecyclerView;
    private RecyclerView.LayoutManager reminderListLayoutManager;
    private RecyclerViewAdapter<Reminder> reminderListAdapter;
    private FloatingActionButton addNewReminderButton;

    private List<Reminder> reminderList = new ArrayList<>();

    @Inject
    public ReminderViewModel vm;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    private void setupView() {
        setContentView(R.layout.screen_reminder_list);
        setupActionBar();
        turnScreenOnThroughKeyguard();

        setupReminderListView();
        setupReminderCreateView();
    }

    private void setupReminderListView() {
        reminderListRecyclerView = findViewById(R.id.list_reminders);
        reminderListRecyclerView.setHasFixedSize(true);

        reminderListLayoutManager = new LinearLayoutManager(this);
        reminderListRecyclerView.setLayoutManager(reminderListLayoutManager);

        reminderListAdapter = new RecyclerViewAdapter<Reminder>(this, UiOptions.REMINDER_LIST_ALL, R.layout.card_reminder_full_width);
        reminderListAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<Reminder>() {
            @Override
            public void onAction(Reminder reminder, String action) {
                switch (action) {
                    case UiOptions.REMINDER_ACTION_REMOVE:
                        vm.deleteReminder(ReminderListActivity.this, reminder.id);
                        refreshView();
                        break;
                }
            }
        });
        reminderListRecyclerView.setAdapter(reminderListAdapter);
    }

    private void setupReminderCreateView() {
        addNewReminderButton = findViewById(R.id.new_reminder);
        addNewReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderListActivity.this, ReminderNewActivity.class);
                startActivity(intent);
            }
        });
    }

    private void refreshView() {
        reminderList = vm.getReminders();
        reminderListAdapter.setData(reminderList);
    }

//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        getWindow().addFlags(
//                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//    }
//
//    @Override
//    public void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        getWindow().clearFlags(
//                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//    }
}
