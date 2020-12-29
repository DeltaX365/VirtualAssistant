package com.vnsolution.virtualassistant.ui.contact.component;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vnsolution.virtualassistant.R;

public class ContactListSortBottomSheetDialogFragment extends BottomSheetDialogFragment {
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

        View contentView = View.inflate(getContext(), R.layout.bottomsheet_contact_list_sort, null);
        dialog.setContentView(contentView);

        RadioGroup contactListSortByRadioGroup = contentView.findViewById(R.id.contact_list_sort_by);
        contactListSortByRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SortBy sortBy = null;
                SortDirection sortDirection = null;
                switch (checkedId) {
                    case R.id.contact_list_sort_by_name_asc:
                        sortBy = SortBy.NAME;
                        sortDirection = SortDirection.ASC;
                        break;
                    case R.id.contact_list_sort_by_name_desc:
                        sortBy = SortBy.NAME;
                        sortDirection = SortDirection.DESC;
                        break;
                    case R.id.contact_list_sort_by_total_call:
                        sortBy = SortBy.TOTAL_CALL;
                        sortDirection = SortDirection.DESC;
                        break;
                }
                if (listener != null && sortBy != null && sortDirection != null) {
                    listener.onChanged(sortBy, sortDirection);
                }
            }
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).addBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    public void listener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onChanged(SortBy sortBy, SortDirection sortDirection);
    }

    public enum SortBy {
        NAME,
        TOTAL_CALL
    }

    public enum SortDirection {
        ASC,
        DESC
    }
}
