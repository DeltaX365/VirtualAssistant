package com.vnsolution.virtualassistant.ui.places;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceHistory;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

import java.util.List;

public class VisitedPlaceAdapter extends RecyclerView.Adapter<VisitedPlacesViewHolder> {
    private List<Place> dataSource;
    private OnItemActionListerner onItemActionListerner;

    public VisitedPlaceAdapter(List<Place> dataSource) {
        this.dataSource = dataSource;
    }

    public void setData(List<Place> places) {
        this.dataSource = places;
        notifyDataSetChanged();
    }

    public void setHandler(OnItemActionListerner onItemActionListerner) {
        this.onItemActionListerner = onItemActionListerner;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public VisitedPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_visited_place, parent, false);
        VisitedPlacesViewHolder viewHolder = new VisitedPlacesViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull VisitedPlacesViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textPlaceName.setText(dataSource.get(position).placeName);
        holder.textPlaceAddress.setText(dataSource.get(position).formattedAddress);
        holder.onItemActionListerner = onItemActionListerner;
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}
