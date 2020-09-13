package com.vnsolution.virtualassistant.ui.phoneapps.v1;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class PhoneAppsRecycleView extends RecyclerView {
    public PhoneAppsRecycleView(@NonNull Context context) {
        super(context);
    }

    public PhoneAppsRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhoneAppsRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof PhoneAppsAdapter) {
            PhoneAppsAdapter adapter = (PhoneAppsAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
