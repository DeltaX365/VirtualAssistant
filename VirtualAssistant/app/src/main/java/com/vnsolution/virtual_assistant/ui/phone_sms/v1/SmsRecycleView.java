package com.vnsolution.virtual_assistant.ui.phone_sms.v1;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtual_assistant.ui.common.OnItemActionListerner;

public class SmsRecycleView extends RecyclerView {
    public SmsRecycleView(@NonNull Context context) {
        super(context);
    }

    public SmsRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SmsRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof SmsAdapter) {
            SmsAdapter adapter = (SmsAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
