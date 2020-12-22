package com.vnsolution.virtualassistant.ui.reminder;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.ReminderOptions;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.reminder.component.ReminderActionBottomSheetDialogFragment;
import com.vnsolution.virtualassistant.ui.reminder.component.ReminderRepeatOptionBottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class ReminderNewActivity extends BaseActivity implements HasAndroidInjector {
    private MenuItem reminderCreationCompletedMenuItem;

    private TextInputEditText reminderTitleTextInput;
    private TextInputEditText reminderDateTextInput;
    private TextInputEditText reminderRepeatTextInput;
    private TextInputEditText reminderActionTextInput;

    private AutoCompleteTextView reminderMetaDataTextView;
    private ArrayAdapter reminderMetaDataAdapter;

    private List<PhoneApp> phoneApps;
    private List<PhoneContact> phoneContacts;

    private String reminderTitle;
    private Date reminderDate;
    private String reminderRepeat = ReminderOptions.REPEAT_NONE;
    private String reminderAction = ReminderOptions.ACTION_NONE;
    private String reminderMetaData;

    @Inject
    ViewModelProvider.Factory vmFactory;

    private ReminderViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = ViewModelProviders.of(this, vmFactory).get(ReminderViewModel.class);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.screen_reminder_new);
        setupActionBar();

        setupReminderTitleView();
        setupReminderDateView();
        setupReminderRepeatOptionsView();
        setupReminderActionsView();
        setupReminderMetaDataView();
    }

    private void setupReminderTitleView() {
        reminderTitleTextInput = findViewById(R.id.reminder_title);
        reminderTitleTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                reminderTitle = s.toString().trim();
                validate();
            }
        });
    }

    private void setupReminderDateView() {
        reminderDateTextInput = findViewById(R.id.reminder_date);
        reminderDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy 'at' HH:mm");
        reminderDateTextInput.setText(simpleDateFormat.format(reminderDate));

        reminderDateTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingleDateAndTimePickerDialog.Builder(ReminderNewActivity.this)
                        .bottomSheet()
                        .curved()
                        .minutesStep(1)
                        .backgroundColor(Color.rgb(206, 206, 206))
                        .mainColor(Color.BLACK)
                        .titleTextColor(Color.BLACK)
                        //.displayHours(false)
                        //.displayMinutes(false)
                        .title("Choose a date in future")
                        .todayText("Today")
                        .mustBeOnFuture()
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                reminderDate = date;
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy 'at' HH:mm");
                                reminderDateTextInput.setText(simpleDateFormat.format(reminderDate));
                                validate();
                            }
                        }).display();
            }
        });
    }

    private void setupReminderRepeatOptionsView() {
        reminderRepeatTextInput = findViewById(R.id.reminder_repeat);
        reminderRepeatTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderRepeatOptionBottomSheetDialogFragment bottomSheetDialog = new ReminderRepeatOptionBottomSheetDialogFragment();
                bottomSheetDialog.listener(new ReminderRepeatOptionBottomSheetDialogFragment.Listener() {
                    @Override
                    public void onChanged(String repeatOption) {
                        reminderRepeat = repeatOption;
                        reminderRepeatTextInput.setText(String.valueOf(repeatOption));
                        validate();
                    }
                });
                bottomSheetDialog.setValue(reminderRepeat);
                bottomSheetDialog.show(getSupportFragmentManager(), "ReminderRepeatOptionBottomSheetDialogFragment");
            }
        });
    }

    private void setupReminderActionsView() {
        reminderActionTextInput = findViewById(R.id.reminder_action);
        reminderActionTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderActionBottomSheetDialogFragment bottomSheetDialog = new ReminderActionBottomSheetDialogFragment();
                bottomSheetDialog.listener(new ReminderActionBottomSheetDialogFragment.Listener() {
                    @Override
                    public void onChanged(String action) {
                        reminderAction = action;
                        reminderActionTextInput.setText(action);
                        onChangeReminderAction();
                        validate();
                    }
                });
                bottomSheetDialog.setValue(reminderAction);
                bottomSheetDialog.show(getSupportFragmentManager(), "ReminderActionBottomSheetDialogFragment");
            }
        });
    }

    private void setupReminderMetaDataView() {
        phoneContacts = vm.getPhoneContacts(this,false);
        phoneApps = vm.getPhoneApps(this,false,false);

        reminderMetaDataTextView = findViewById(R.id.reminder_metadata);
        reminderMetaDataAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        reminderMetaDataTextView.setAdapter(reminderMetaDataAdapter);
        reminderMetaDataTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reminderMetaData = reminderMetaDataTextView.getText().toString();
                validate();
            }
        });
    }

//    private void toggleBottomSheet(BottomSheetBehavior bottomSheetBehavior) {
//        if (bottomSheetBehavior != null) {
//            int state = (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) ?
//                    BottomSheetBehavior.STATE_HIDDEN :
//                    BottomSheetBehavior.STATE_EXPANDED;
//            bottomSheetBehavior.setState(state);
//        }
//    }

    private void onChangeReminderAction() {
        List<String> items = new ArrayList<>();
        switch (reminderAction) {
            case ReminderOptions.ACTION_LAUNCH_APP:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    items = phoneApps.stream().map(phoneApp -> phoneApp.appName).collect(Collectors.toList());
                }
                break;
            case ReminderOptions.ACTION_SEND_SMS:
            case ReminderOptions.ACTION_CALL_PHONE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    items = phoneContacts.stream().map(phoneContact -> phoneContact.name).collect(Collectors.toList());
                }
                break;
        }

        reminderMetaDataAdapter.clear();
        if (items != null && items.size() > 0) {
            reminderMetaDataAdapter.addAll(items);
        }
        reminderMetaData = "";
        reminderMetaDataTextView.setText(reminderMetaData);
    }

    private void validate() {
        boolean isValid = (reminderTitle != null && !reminderTitle.isEmpty() && reminderCreationCompletedMenuItem != null) ? true : false;
        if (reminderAction != null && !reminderAction.isEmpty()) {
            String reminderMetaData = reminderMetaDataTextView.getText().toString().trim();
            isValid = isValid && (reminderMetaData != null && !reminderMetaData.isEmpty());
        }
        reminderCreationCompletedMenuItem.setEnabled(isValid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reminder_list_options, menu);
        reminderCreationCompletedMenuItem = menu.findItem(R.id.menu_item_done);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                boolean result = vm.createReminder(this, reminderTitle, reminderDate, reminderRepeat, reminderAction, reminderMetaData);
                String message = "";
                if (result) {
                    message = String.format("A reminder %s was added successfully", reminderTitle);
                    finish();
                } else {
                    message = String.format("Failed to add %s. Please correct reminder and submit again.", reminderTitle);
                }
                Toast.makeText(ReminderNewActivity.this, message, Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
