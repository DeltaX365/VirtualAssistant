package com.vnsolution.virtualassistant.ui.contacts;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class ContactsRecycleView extends RecyclerView {
    public ContactsRecycleView(@NonNull Context context) {
        super(context);
    }

    public ContactsRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactsRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof ContactsAdapter) {
            ContactsAdapter adapter = (ContactsAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
