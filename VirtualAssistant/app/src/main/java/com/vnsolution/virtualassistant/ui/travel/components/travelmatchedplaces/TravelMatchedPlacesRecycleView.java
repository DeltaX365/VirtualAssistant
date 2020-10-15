package com.vnsolution.virtualassistant.ui.travel.components.travelmatchedplaces;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;
import com.vnsolution.virtualassistant.ui.travel.components.travelvisitedplaces.TravelVisitedPlaceAdapter;

public class TravelMatchedPlacesRecycleView extends RecyclerView {
    public TravelMatchedPlacesRecycleView(@NonNull Context context) {
        super(context);
    }

    public TravelMatchedPlacesRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TravelMatchedPlacesRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof TravelVisitedPlaceAdapter) {
            TravelVisitedPlaceAdapter adapter = (TravelVisitedPlaceAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
