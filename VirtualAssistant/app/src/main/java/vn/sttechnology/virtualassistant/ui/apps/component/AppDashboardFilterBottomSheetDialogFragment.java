package vn.sttechnology.virtualassistant.ui.apps.component;

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

public class AppDashboardFilterBottomSheetDialogFragment extends BottomSheetDialogFragment {
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

        View contentView = View.inflate(getContext(), R.layout.bottomsheet_app_dashboard_filter, null);
        dialog.setContentView(contentView);

        RadioGroup appDashboardFilterRadioGroup = contentView.findViewById(R.id.app_dashboard_filter);
        appDashboardFilterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.app_dashboard_filter_include_system_apps:
                        value = Filter.INCLUDE_SYSTEM_APPS;
                        break;
                    case R.id.app_dashboard_filter_exclude_system_apps:
                        value = Filter.EXCLUDE_SYSTEM_APPS;
                        break;
                }
            }
        });
        if (value != null) {
//            appDashboardFilterRadioGroup.clearCheck();
            if (value.equals(Filter.INCLUDE_SYSTEM_APPS)) {
                appDashboardFilterRadioGroup.check(R.id.app_dashboard_filter_include_system_apps);
            }
            if (value.equals(Filter.EXCLUDE_SYSTEM_APPS)) {
                appDashboardFilterRadioGroup.check(R.id.app_dashboard_filter_exclude_system_apps);
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
        INCLUDE_SYSTEM_APPS,
        EXCLUDE_SYSTEM_APPS
    }
}
