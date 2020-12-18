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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.ReminderOptions;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;

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

    private RadioGroup reminderRepeatOptionRadioGroup;
    private LinearLayout reminderRepeatOptionsBottomSheet;
    private BottomSheetBehavior reminderRepeatOptionsSheetBehavior;
    private ImageButton reminderRepeatOptionCloseButton;

    private RadioGroup reminderActionRadioGroup;
    private LinearLayout reminderActionsBottomSheet;
    private BottomSheetBehavior reminderActionsSheetBehavior;
    private ImageButton reminderActionCloseButton;

    private List<PhoneApp> phoneApps;
    private List<PhoneContact> phoneContacts;

    private String reminderTitle;
    private Date reminderDate;
    private String reminderRepeat;
    private String reminderAction;
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
        reminderRepeatOptionsBottomSheet = findViewById(R.id.bottomsheet_reminder_repeat_options);
        reminderRepeatOptionsSheetBehavior = BottomSheetBehavior.from(reminderRepeatOptionsBottomSheet);
        reminderRepeatOptionsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Close bottomsheet
        reminderRepeatOptionCloseButton = findViewById(R.id.reminder_repeat_option_close);
        reminderRepeatOptionCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderRepeatOptionsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        // UI for bottomsheet
        reminderRepeatOptionRadioGroup = findViewById(R.id.reminder_repeat_option_group);
        reminderRepeatOptionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.reminder_repeat_option_no_repeat:
                        reminderRepeat = ReminderOptions.REPEAT_NONE;
                        break;
                    case R.id.reminder_repeat_option_daily:
                        reminderRepeat = ReminderOptions.REPEAT_DAILY;
                        break;
                    case R.id.reminder_repeat_option_weekly:
                        reminderRepeat = ReminderOptions.REPEAT_WEEKLY;
                        break;
                    case R.id.reminder_repeat_option_monthly:
                        reminderRepeat = ReminderOptions.REPEAT_MONTHLY;
                        break;
                    case R.id.reminder_repeat_option_yearly:
                        reminderRepeat = ReminderOptions.REPEAT_YEARLY;
                        break;
                }
                reminderRepeatTextInput.setText(reminderRepeat);
                validate();
            }
        });
//        reminderRepeatOptionRadioGroup.check(R.id.reminder_repeat_option_daily);

        reminderRepeatTextInput = findViewById(R.id.reminder_repeat);
        reminderRepeatTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet(reminderRepeatOptionsSheetBehavior);
            }
        });
        reminderRepeat = ReminderOptions.REPEAT_NONE;
        reminderRepeatTextInput.setText(reminderRepeat);
    }

    private void setupReminderActionsView() {
        reminderActionsBottomSheet = findViewById(R.id.bottomsheet_reminder_actions);
        reminderActionsSheetBehavior = BottomSheetBehavior.from(reminderActionsBottomSheet);
        reminderActionsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Close bottomsheet
        reminderActionCloseButton = findViewById(R.id.reminder_action_close);
        reminderActionCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderActionsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        // UI for bottomsheet
        reminderActionRadioGroup = findViewById(R.id.reminder_action_group);
        reminderActionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                reminderAction = "";
                switch (checkedId) {
                    case R.id.reminder_action_launch_app:
                        reminderAction = ReminderOptions.ACTION_LAUNCH_APP;
                        break;
                    case R.id.reminder_action_send_sms:
                        reminderAction = ReminderOptions.ACTION_SEND_SMS;
                        break;
                    case R.id.reminder_action_call_phone:
                        reminderAction = ReminderOptions.ACTION_CALL_PHONE;
                        break;
                }
                reminderActionTextInput.setText(reminderAction);
                onChangeReminderAction();
                validate();
            }
        });
//        reminderRepeatOptionRadioGroup.check(R.id.reminder_repeat_option_daily);

        reminderActionTextInput = findViewById(R.id.reminder_action);
        reminderActionTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet(reminderActionsSheetBehavior);
            }
        });
        reminderAction = "";
        reminderActionTextInput.setText(reminderAction);
    }

    private void setupReminderMetaDataView() {
        phoneContacts = vm.getPhoneContacts();
        phoneApps = vm.getPhoneApps(this,false,true);

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

    private void toggleBottomSheet(BottomSheetBehavior bottomSheetBehavior) {
        if (bottomSheetBehavior != null) {
            int state = (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) ?
                    BottomSheetBehavior.STATE_HIDDEN :
                    BottomSheetBehavior.STATE_EXPANDED;
            bottomSheetBehavior.setState(state);
        }
    }

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
        if (items == null || items.size() == 0) {
            reminderMetaDataAdapter.clear();
        } else {
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
                Toast.makeText(this, "Create reminder: " + reminderTitle, Toast.LENGTH_SHORT).show();
                vm.createReminder(this, reminderTitle, reminderDate, reminderRepeat, reminderAction, reminderMetaData);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
