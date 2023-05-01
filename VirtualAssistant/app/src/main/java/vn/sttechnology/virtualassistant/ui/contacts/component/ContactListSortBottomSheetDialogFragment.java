package vn.sttechnology.virtualassistant.ui.contacts.component;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import vn.sttechnology.virtualassistant.R;

public class ContactListSortBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private SortBy sortBy;
    private SortDirection sortDirection;
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
                switch (checkedId) {
                    case R.id.contact_list_sort_by_name_asc:
                        sortBy = SortBy.NAME;
                        sortDirection = SortDirection.ASC;
                        break;
                    case R.id.contact_list_sort_by_name_desc:
                        sortBy = SortBy.NAME;
                        sortDirection = SortDirection.DESC;
                        break;
                    case R.id.contact_list_sort_by_total_call_asc:
                        sortBy = SortBy.TOTAL_CALL;
                        sortDirection = SortDirection.ASC;
                        break;
                    case R.id.contact_list_sort_by_total_call_desc:
                        sortBy = SortBy.TOTAL_CALL;
                        sortDirection = SortDirection.DESC;
                        break;
                }
            }
        });
        if (sortBy != null && sortDirection != null) {
//            contactListSortByRadioGroup.clearCheck();
            if (sortBy.equals(SortBy.NAME) && sortDirection.equals(SortDirection.ASC)) {
                contactListSortByRadioGroup.check(R.id.contact_list_sort_by_name_asc);
            }
            if (sortBy.equals(SortBy.NAME) && sortDirection.equals(SortDirection.DESC)) {
                contactListSortByRadioGroup.check(R.id.contact_list_sort_by_name_desc);
            }
            if (sortBy.equals(SortBy.TOTAL_CALL) && sortDirection.equals(SortDirection.ASC)) {
                contactListSortByRadioGroup.check(R.id.contact_list_sort_by_total_call_asc);
            }
            if (sortBy.equals(SortBy.TOTAL_CALL) && sortDirection.equals(SortDirection.DESC)) {
                contactListSortByRadioGroup.check(R.id.contact_list_sort_by_total_call_desc);
            }
        }

        TextView acceptFilterTextView = contentView.findViewById(R.id.accept_filter);
        acceptFilterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && sortBy != null && sortDirection != null) {
                    listener.onChanged(sortBy, sortDirection);
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

    public void setValue(SortBy sortBy, SortDirection sortDirection) {
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
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
