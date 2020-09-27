package com.vnsolution.virtualassistant.ui.travel.components.travelcategorizedplaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.db.entity.PlaceSummary;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class TravelCategorizedVisitedPlacesAdapter extends RecyclerView.Adapter<TravelCategorizedVisitedPlacesViewHolder> {
    private Context context;
    private PlaceSummary dataSource;
    private OnItemActionListerner onItemActionListerner;

    public TravelCategorizedVisitedPlacesAdapter(Context context, PlaceSummary dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    public void setData(PlaceSummary data) {
        this.dataSource = data;
        notifyDataSetChanged();
    }

    public void setHandler(OnItemActionListerner onItemActionListerner) {
        this.onItemActionListerner = onItemActionListerner;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public TravelCategorizedVisitedPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_place_categorized, parent, false);
        TravelCategorizedVisitedPlacesViewHolder viewHolder = new TravelCategorizedVisitedPlacesViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull TravelCategorizedVisitedPlacesViewHolder holder, int position) {
        int total = 0;
        String extension = "";
        switch (position) {
            case 0:
                holder.textCategoryName.setText("Yesterday");
                total = (dataSource != null)? dataSource.totalPlaceYesterday : 0;
                extension = (total == 1)? "place" : "places";
                holder.textCategoryDetail.setText(total + " " + extension);
                break;
            case 1:
                holder.textCategoryName.setText("Places");
                total = (dataSource != null) ? dataSource.totalPlace : 0;
                extension = (total == 1)? "place" : "places";
                holder.textCategoryDetail.setText(total + " " + extension);
                break;
            case 2:
                holder.textCategoryName.setText("Cities");
                total = (dataSource != null) ? dataSource.totalCity : 0;
                extension = (total == 1)? "city" : "cities";
                holder.textCategoryDetail.setText(total + " " + extension);
                break;
            case 3:
                holder.textCategoryName.setText("Countries");
                total = (dataSource != null) ? dataSource.totalCountry : 0;
                extension = (total == 1)? "country" : "countries";
                holder.textCategoryDetail.setText(total + " " + extension);
                break;
        }
        holder.onItemActionListerner = onItemActionListerner;
    }

    @Override
    public int getItemCount() {
        return (dataSource != null)? 4 : 0;
    }
}
