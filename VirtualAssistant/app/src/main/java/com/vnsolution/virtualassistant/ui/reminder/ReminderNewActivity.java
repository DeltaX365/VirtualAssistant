package com.vnsolution.virtualassistant.ui.reminder;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dagger.android.HasAndroidInjector;

public class ReminderNewActivity extends BaseActivity implements HasAndroidInjector {
    private MenuItem reminderCreationCompletedMenuItem;

    private TextInputEditText reminderTitleTextInput;
    private TextInputEditText reminderDateTextInput;
    private TextInputEditText reminderRepeatTextInput;

    private RadioGroup reminderRepeatOptionRadioGroup;
    private LinearLayout reminderRepeatOptionsBottomSheet;
    private BottomSheetBehavior reminderRepeatOptionsSheetBehavior;
    private ImageButton reminderRepeatOptionCloseButton;

    private String reminderTitle;
    private Date reminderDate;
    private String reminderRepeatOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.screen_reminder_new);
        setupActionBar();

        setupReminderTitleView();
        setupReminderDateView();
        setupReminderRepeatOptionsView();
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
                boolean isEnabled = (reminderTitle != null && reminderTitle != "" && reminderCreationCompletedMenuItem != null) ? true : false;
                reminderCreationCompletedMenuItem.setEnabled(isEnabled);
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
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                if (reminderDate != null) {
                    builder.setSelection(reminderDate.getTime());
                }

                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                long today = MaterialDatePicker.todayInUtcMilliseconds();
                constraintsBuilder.setStart(today);
                builder.setCalendarConstraints(constraintsBuilder.build());

                MaterialDatePicker<Long> picker = builder.build();
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selection);
                        reminderDate = calendar.getTime();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy 'at' HH:mm");
                        reminderDateTextInput.setText(simpleDateFormat.format(reminderDate));
                    }
                });
                picker.show(getSupportFragmentManager(), picker.toString());
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
                        reminderRepeatOption = "";
                        break;
                    case R.id.reminder_repeat_option_daily:
                        reminderRepeatOption = "Daily";
                        break;
                    case R.id.reminder_repeat_option_weekly:
                        reminderRepeatOption = "Weekly";
                        break;
                    case R.id.reminder_repeat_option_monthly:
                        reminderRepeatOption = "Monthly";
                        break;
                    case R.id.reminder_repeat_option_yearly:
                        reminderRepeatOption = "Yearly";
                        break;
                }
                reminderRepeatTextInput.setText(reminderRepeatOption);
            }
        });
//        reminderRepeatOptionRadioGroup.check(R.id.reminder_repeat_option_daily);

        reminderRepeatTextInput = findViewById(R.id.reminder_repeat);
        reminderRepeatTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet();
            }
        });
        reminderRepeatOption = "";
        reminderRepeatTextInput.setText(reminderRepeatOption);
    }

    private void toggleBottomSheet() {
        if (reminderRepeatOptionsSheetBehavior != null) {
            int state = (reminderRepeatOptionsSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) ?
                    BottomSheetBehavior.STATE_HIDDEN :
                    BottomSheetBehavior.STATE_EXPANDED;
            reminderRepeatOptionsSheetBehavior.setState(state);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reminder_options, menu);
        reminderCreationCompletedMenuItem = menu.findItem(R.id.menu_item_done);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                Toast.makeText(this, "Will create reminder: " + reminderTitle, Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
