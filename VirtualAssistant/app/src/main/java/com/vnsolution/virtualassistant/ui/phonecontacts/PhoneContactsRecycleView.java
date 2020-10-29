package com.vnsolution.virtualassistant.ui.phonecontacts;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

public class PhoneContactsRecycleView extends RecyclerView {
    public PhoneContactsRecycleView(@NonNull Context context) {
        super(context);
    }

    public PhoneContactsRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhoneContactsRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnRecyclerViewRowTouchListener onRecyclerViewRowTouchListener) {
        if (this.getAdapter() != null && this.getAdapter() instanceof PhoneContactsAdapter) {
            PhoneContactsAdapter adapter = (PhoneContactsAdapter) this.getAdapter();
            adapter.setHandler(onRecyclerViewRowTouchListener);
        }
    }
}
