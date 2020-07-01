package com.vnsolution.virtualassistant.ui.applications;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class InstalledApplicationsRecycleView extends RecyclerView {
    public InstalledApplicationsRecycleView(@NonNull Context context) {
        super(context);
    }

    public InstalledApplicationsRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InstalledApplicationsRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof InstalledApplicationsAdapter) {
            InstalledApplicationsAdapter adapter = (InstalledApplicationsAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
