package com.vnsolution.virtualassistant.ui.travel.components.travelvisitedplaces;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class TravelVisitedPlaceRecycleView extends RecyclerView {
    public TravelVisitedPlaceRecycleView(@NonNull Context context) {
        super(context);
    }

    public TravelVisitedPlaceRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TravelVisitedPlaceRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof TravelVisitedPlaceAdapter) {
            TravelVisitedPlaceAdapter adapter = (TravelVisitedPlaceAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
