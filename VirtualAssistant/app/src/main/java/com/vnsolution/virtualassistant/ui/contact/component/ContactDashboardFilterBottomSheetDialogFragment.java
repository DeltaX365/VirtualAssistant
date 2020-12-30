package com.vnsolution.virtualassistant.ui.contact.component;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vnsolution.virtualassistant.R;

public class ContactDashboardFilterBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private Filter value;
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

        View contentView = View.inflate(getContext(), R.layout.bottomsheet_contact_dashboard_filter, null);
        dialog.setContentView(contentView);

        RadioGroup contactDashboardFilterRadioGroup = contentView.findViewById(R.id.contact_dashboard_filter);
        contactDashboardFilterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.contact_dashboard_filter_include_strangers:
                        value = Filter.INCLUDE_STRANGERS;
                        break;
                    case R.id.contact_dashboard_filter_exclude_strangers:
                        value = Filter.EXCLUDE_STRANGERS;
                        break;
                }
            }
        });
        if (value != null) {
            contactDashboardFilterRadioGroup.clearCheck();
            if (value == Filter.INCLUDE_STRANGERS) {
                contactDashboardFilterRadioGroup.check(R.id.contact_dashboard_filter_include_strangers);
            } else {
                contactDashboardFilterRadioGroup.check(R.id.contact_dashboard_filter_exclude_strangers);
            }
        }

        TextView acceptFilterTextView = contentView.findViewById(R.id.accept_filter);
        acceptFilterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && value != null) {
                    listener.onChanged(value);
                    dismiss();
                }
            }
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).addBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    public void setValue(Filter value) {
        this.value = value;
    }

    public void listener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onChanged(Filter filter);
    }

    public enum Filter {
        INCLUDE_STRANGERS,
        EXCLUDE_STRANGERS
    }
}
