package com.vnsolution.virtual_assistant.ui.travel.travel_recently_visited_places;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtual_assistant.ui.common.OnItemActionListerner;

public class TravelRecentlyVisitedPlacesRecycleView extends RecyclerView {
    public TravelRecentlyVisitedPlacesRecycleView(@NonNull Context context) {
        super(context);
    }

    public TravelRecentlyVisitedPlacesRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TravelRecentlyVisitedPlacesRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof TravelRecentlyVisitedPlacesAdapter) {
            TravelRecentlyVisitedPlacesAdapter adapter = (TravelRecentlyVisitedPlacesAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
