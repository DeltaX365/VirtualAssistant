package com.vnsolution.virtualassistant.ui.travel.components.travelsimplevisitedplaces;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class TravelRecycleView extends RecyclerView {
    public TravelRecycleView(@NonNull Context context) {
        super(context);
    }

    public TravelRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TravelRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof TravelAdapter) {
            TravelAdapter adapter = (TravelAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
