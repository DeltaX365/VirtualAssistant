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

public class VisitedPlacesAdapter extends RecyclerView.Adapter<VisitedPlacesViewHolder> {
    private List<Place> dataSource;
    private OnItemActionListerner onItemActionListerner;

    public VisitedPlacesAdapter(List<Place> dataSource) {
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
        Place place = dataSource.get(position);

        String placeName = (place.placeName == null || place.placeName.isEmpty()) ? "Unknown" : place.placeName;
        holder.textPlaceName.setText(placeName);

        String placeAddress = (place.formattedAddress == null || place.formattedAddress.isEmpty()) ? (place.latitude + "," + place.longitude) : place.formattedAddress;
        holder.textPlaceAddress.setText(placeAddress);

        holder.onItemActionListerner = onItemActionListerner;
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}
