package com.vnsolution.virtualassistant.ui.phonecontacts;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

public class PhoneContactRecycleView extends RecyclerView {
    public PhoneContactRecycleView(@NonNull Context context) {
        super(context);
    }

    public PhoneContactRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhoneContactRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnRecyclerViewRowTouchListener onRecyclerViewRowTouchListener) {
        if (this.getAdapter() != null && this.getAdapter() instanceof PhoneContactAdapter) {
            PhoneContactAdapter adapter = (PhoneContactAdapter) this.getAdapter();
            adapter.setHandler(onRecyclerViewRowTouchListener);
        }
    }
}
