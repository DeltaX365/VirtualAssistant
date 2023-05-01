package vn.sttechnology.virtualassistant.ui.reminders;

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

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.textfield.TextInputEditText;
import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.common.ReminderSetting;
import vn.sttechnology.virtualassistant.model.PhoneApp;
import vn.sttechnology.virtualassistant.model.PhoneContact;
import vn.sttechnology.virtualassistant.model.Reminder;
import vn.sttechnology.virtualassistant.ui.common.BaseActivity;
import vn.sttechnology.virtualassistant.ui.reminders.component.ReminderActionBottomSheetDialogFragment;
import vn.sttechnology.virtualassistant.ui.reminders.component.ReminderRepeatOptionBottomSheetDialogFragment;

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

    private String reminderId;
    private Reminder reminder;
    private boolean isNewReminder = true;

    @Inject
    ReminderViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("ReminderId")) {
            reminderId = getIntent().getStringExtra("ReminderId");
            reminder = vm.getReminderById(reminderId);
            isNewReminder = false;
        }
        if (reminder == null) {
            reminder = new Reminder();
            reminder.date = new Date();
            reminder.action = ReminderSetting.ACTION_NONE;
            reminder.repeat = ReminderSetting.REPEAT_NONE;
        }
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
                reminder.name = s.toString().trim();
                validate();
            }
        });
        if (reminder != null) {
            reminderTitleTextInput.setText(reminder.name);
        }
    }

    private void setupReminderDateView() {
        reminderDateTextInput = findViewById(R.id.reminder_date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, MMM dd, yyyy 'at' HH:mm");
        reminderDateTextInput.setText(simpleDateFormat.format(reminder.date));

        if (isNewReminder) {
            SingleDateAndTimePickerDialog.Builder reminderDatePicker = new SingleDateAndTimePickerDialog.Builder(ReminderNewActivity.this)
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
                            if (!reminder.date.equals(date)) {
                                reminder.date = date;
                                reminderDateTextInput.setText(simpleDateFormat.format(reminder.date));
                                validate();
                            }
                        }
                    });
            reminderDateTextInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reminderDatePicker.display();
                }
            });
        } else {
            reminderDateTextInput.setEnabled(false);
        }
    }

    private void setupReminderRepeatOptionsView() {
        reminderRepeatTextInput = findViewById(R.id.reminder_repeat);
        reminderRepeatTextInput.setText(reminder.repeat);
        if (isNewReminder) {
            reminderRepeatTextInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReminderRepeatOptionBottomSheetDialogFragment bottomSheetDialog = new ReminderRepeatOptionBottomSheetDialogFragment();
                    bottomSheetDialog.listener(new ReminderRepeatOptionBottomSheetDialogFragment.Listener() {
                        @Override
                        public void onChanged(String repeatOption) {
                            if (!reminder.repeat.equals(repeatOption)) {
                                reminder.repeat = repeatOption;
                                reminderRepeatTextInput.setText(String.valueOf(repeatOption));
                                validate();
                            }
                        }
                    });
                    bottomSheetDialog.setValue(reminder.repeat);
                    bottomSheetDialog.show(getSupportFragmentManager(), "ReminderRepeatOptionBottomSheetDialogFragment");
                }
            });
        } else {
            reminderRepeatTextInput.setEnabled(false);
        }
    }

    private void setupReminderActionsView() {
        reminderActionTextInput = findViewById(R.id.reminder_action);
        reminderActionTextInput.setText(reminder.action);

        ReminderActionBottomSheetDialogFragment bottomSheetDialog = new ReminderActionBottomSheetDialogFragment();
        bottomSheetDialog.listener(new ReminderActionBottomSheetDialogFragment.Listener() {
            @Override
            public void onChanged(String action) {
                if (!reminder.action.equals(action)) {
                    reminder.action = action;
                    reminderActionTextInput.setText(action);
                    onChangeReminderAction();
                    validate();
                }
            }
        });
        bottomSheetDialog.setValue(reminder.action);
        reminderActionTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                String extendData = reminderMetaDataTextView.getText().toString().trim();
                if (!reminder.extendedData.equals(extendData)) {
                    reminder.extendedData = extendData;
                    validate();
                }
            }
        });
        reminderMetaDataTextView.setText(reminder.extendedData);
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
        switch (reminder.action) {
            case ReminderSetting.ACTION_LAUNCH_APP:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    items = phoneApps.stream().map(phoneApp -> phoneApp.appName).collect(Collectors.toList());
                }
                break;
            case ReminderSetting.ACTION_SEND_SMS:
            case ReminderSetting.ACTION_CALL_PHONE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    items = phoneContacts.stream().map(phoneContact -> phoneContact.name).collect(Collectors.toList());
                }
                break;
        }

        reminderMetaDataAdapter.clear();
        if (items != null && items.size() > 0) {
            reminderMetaDataAdapter.addAll(items);
        }
        reminder.extendedData = "";
        reminderMetaDataTextView.setText(reminder.extendedData);
    }

    private void validate() {
        if (reminderCreationCompletedMenuItem != null) {
            boolean isValid = (reminder.name != null && !reminder.name.isEmpty() && reminderCreationCompletedMenuItem != null) ? true : false;
            if (reminder.action != null && !reminder.action.isEmpty() && !reminder.action.equals(ReminderSetting.ACTION_NONE)) {
                isValid = isValid && (reminder.extendedData != null && !reminder.extendedData.isEmpty());
            }
            reminderCreationCompletedMenuItem.setEnabled(isValid);
        }
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
                boolean result = vm.saveReminder(this, reminder);
                String message = "";
                if (result) {
                    message = String.format("A reminder %s was added successfully", reminder.name);
                    finish();
                } else {
                    message = String.format("Failed to add %s. Please correct reminder and submit again.", reminder.name);
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
