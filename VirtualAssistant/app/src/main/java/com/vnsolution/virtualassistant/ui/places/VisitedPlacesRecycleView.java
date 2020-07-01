package com.vnsolution.virtualassistant.ui.places;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class VisitedPlacesRecycleView extends RecyclerView {
    public VisitedPlacesRecycleView(@NonNull Context context) {
        super(context);
    }

    public VisitedPlacesRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VisitedPlacesRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof VisitedPlacesAdapter) {
            VisitedPlacesAdapter adapter = (VisitedPlacesAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
