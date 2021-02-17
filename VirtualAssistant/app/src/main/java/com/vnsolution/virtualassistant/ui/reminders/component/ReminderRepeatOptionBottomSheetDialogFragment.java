package com.vnsolution.virtualassistant.ui.reminders.component;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.ReminderSetting;

public class ReminderRepeatOptionBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private String value;
    private Listener listener;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.bottomsheet_reminder_repeat_option, null);
        dialog.setContentView(contentView);

        RadioGroup reminderRepeatOptionRadioGroup = contentView.findViewById(R.id.reminder_repeat_option_group);
        reminderRepeatOptionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String repeatOption = ReminderSetting.REPEAT_NONE;
                switch (checkedId) {
                    case R.id.reminder_repeat_option_no_repeat:
                        repeatOption = ReminderSetting.REPEAT_NONE;
                        break;
                    case R.id.reminder_repeat_option_daily:
                        repeatOption = ReminderSetting.REPEAT_DAILY;
                        break;
                    case R.id.reminder_repeat_option_weekly:
                        repeatOption = ReminderSetting.REPEAT_WEEKLY;
                        break;
                    case R.id.reminder_repeat_option_monthly:
                        repeatOption = ReminderSetting.REPEAT_MONTHLY;
                        break;
                    case R.id.reminder_repeat_option_yearly:
                        repeatOption = ReminderSetting.REPEAT_YEARLY;
                        break;
                }
                if (listener != null) {
                    listener.onChanged(repeatOption);
                }
            }
        });
        if (value != null) {
            reminderRepeatOptionRadioGroup.clearCheck();
            switch (value) {
                case ReminderSetting.REPEAT_NONE:
                    reminderRepeatOptionRadioGroup.check(R.id.reminder_repeat_option_no_repeat);
                    break;
                case ReminderSetting.REPEAT_DAILY:
                    reminderRepeatOptionRadioGroup.check(R.id.reminder_repeat_option_daily);
                    break;
                case ReminderSetting.REPEAT_WEEKLY:
                    reminderRepeatOptionRadioGroup.check(R.id.reminder_repeat_option_weekly);
                    break;
                case ReminderSetting.REPEAT_MONTHLY:
                    reminderRepeatOptionRadioGroup.check(R.id.reminder_repeat_option_monthly);
                    break;
                case ReminderSetting.REPEAT_YEARLY:
                    reminderRepeatOptionRadioGroup.check(R.id.reminder_repeat_option_yearly);
                    break;
            }
        }

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).addBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void listener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onChanged(String repeatOption);
    }
}
