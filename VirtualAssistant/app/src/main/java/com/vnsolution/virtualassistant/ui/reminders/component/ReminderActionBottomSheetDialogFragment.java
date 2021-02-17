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

public class ReminderActionBottomSheetDialogFragment extends BottomSheetDialogFragment {
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

        View contentView = View.inflate(getContext(), R.layout.bottomsheet_reminder_action, null);
        dialog.setContentView(contentView);

        RadioGroup reminderActionRadioGroup = contentView.findViewById(R.id.reminder_action_group);
        reminderActionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String action = ReminderSetting.ACTION_NONE;
                switch (checkedId) {
                    case R.id.reminder_action_launch_app:
                        action = ReminderSetting.ACTION_LAUNCH_APP;
                        break;
                    case R.id.reminder_action_send_sms:
                        action = ReminderSetting.ACTION_SEND_SMS;
                        break;
                    case R.id.reminder_action_call_phone:
                        action = ReminderSetting.ACTION_CALL_PHONE;
                        break;
                }
                if (listener != null) {
                    listener.onChanged(action);
                }
            }
        });
        if (value != null) {
            reminderActionRadioGroup.clearCheck();
            switch (value) {
                case ReminderSetting.ACTION_NONE:
                    reminderActionRadioGroup.check(R.id.reminder_action_none);
                    break;
                case ReminderSetting.ACTION_LAUNCH_APP:
                    reminderActionRadioGroup.check(R.id.reminder_action_launch_app);
                    break;
                case ReminderSetting.ACTION_SEND_SMS:
                    reminderActionRadioGroup.check(R.id.reminder_action_send_sms);
                    break;
                case ReminderSetting.ACTION_CALL_PHONE:
                    reminderActionRadioGroup.check(R.id.reminder_action_call_phone);
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
        void onChanged(String action);
    }
}
