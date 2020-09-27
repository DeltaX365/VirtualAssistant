package com.vnsolution.virtualassistant.ui.travel.components.travelmostvisitedplaces;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class TravelMostVisitedPlacesRecycleView extends RecyclerView {
    public TravelMostVisitedPlacesRecycleView(@NonNull Context context) {
        super(context);
    }

    public TravelMostVisitedPlacesRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TravelMostVisitedPlacesRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof TravelMostVisitedPlacesAdapter) {
            TravelMostVisitedPlacesAdapter adapter = (TravelMostVisitedPlacesAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
