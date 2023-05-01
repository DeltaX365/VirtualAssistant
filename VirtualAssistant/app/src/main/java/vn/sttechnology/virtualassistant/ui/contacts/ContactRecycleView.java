package vn.sttechnology.virtualassistant.ui.contacts;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import vn.sttechnology.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

public class ContactRecycleView extends RecyclerView {
    public ContactRecycleView(@NonNull Context context) {
        super(context);
    }

    public ContactRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnRecyclerViewRowTouchListener onRecyclerViewRowTouchListener) {
        if (this.getAdapter() != null && this.getAdapter() instanceof ContactAdapter) {
            ContactAdapter adapter = (ContactAdapter) this.getAdapter();
            adapter.setHandler(onRecyclerViewRowTouchListener);
        }
    }
}
