package com.vnsolution.virtualassistant.ui.places;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class VisitedPlaceRecycleView extends RecyclerView {
    public VisitedPlaceRecycleView(@NonNull Context context) {
        super(context);
    }

    public VisitedPlaceRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VisitedPlaceRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof VisitedPlaceAdapter) {
            VisitedPlaceAdapter adapter = (VisitedPlaceAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
