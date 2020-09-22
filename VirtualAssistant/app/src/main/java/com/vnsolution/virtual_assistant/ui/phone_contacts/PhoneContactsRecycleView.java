package com.vnsolution.virtual_assistant.ui.phone_contacts;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtual_assistant.ui.common.OnItemActionListerner;

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

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof PhoneContactsAdapter) {
            PhoneContactsAdapter adapter = (PhoneContactsAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
