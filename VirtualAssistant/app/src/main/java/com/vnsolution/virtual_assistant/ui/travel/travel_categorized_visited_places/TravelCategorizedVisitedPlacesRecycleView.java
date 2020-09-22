package com.vnsolution.virtual_assistant.ui.travel.travel_categorized_visited_places;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtual_assistant.ui.common.OnItemActionListerner;

public class TravelCategorizedVisitedPlacesRecycleView extends RecyclerView {
    public TravelCategorizedVisitedPlacesRecycleView(@NonNull Context context) {
        super(context);
    }

    public TravelCategorizedVisitedPlacesRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TravelCategorizedVisitedPlacesRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemAction(OnItemActionListerner onItemActionListerner) {
        if (this.getAdapter() != null && this.getAdapter() instanceof TravelCategorizedVisitedPlacesAdapter) {
            TravelCategorizedVisitedPlacesAdapter adapter = (TravelCategorizedVisitedPlacesAdapter) this.getAdapter();
            adapter.setHandler(onItemActionListerner);
        }
    }
}
