package com.vnsolution.virtualassistant.ui.calllogs;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.ui.applications.InstalledApplicationsAdapter;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class CallLogsRecycleView extends RecyclerView {
    public CallLogsRecycleView(@NonNull Context context) {
        super(context);
    }

    public CallLogsRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CallLogsRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof CallLogsAdapter) {
            CallLogsAdapter adapter = (CallLogsAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
