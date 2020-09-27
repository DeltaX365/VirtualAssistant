package com.vnsolution.virtualassistant.ui.travel.components.travelmostvisitedplaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

import java.util.List;

public class TravelMostVisitedPlacesAdapter extends RecyclerView.Adapter<TravelMostVisitedPlacesViewHolder> {
    private Context context;
    private List<Place> dataSource;
    private OnItemActionListerner onItemActionListerner;

    public TravelMostVisitedPlacesAdapter(Context context, List<Place> dataSource) {
        this.context = context;
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
    public TravelMostVisitedPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_place, parent, false);
        TravelMostVisitedPlacesViewHolder viewHolder = new TravelMostVisitedPlacesViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull TravelMostVisitedPlacesViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Place place = dataSource.get(position);
//        Glide.with(context).load(place.icon).into(holder.ivPlaceIcon);

        String placeName = (place.placeName == null || place.placeName == "") ? "Unknown" : place.placeName;
        holder.textPlaceName.setText(placeName);

        String placeAddress = (place.formattedAddress == null || place.formattedAddress == "") ? (place.latitude + "," + place.longitude) : place.formattedAddress;
        holder.textPlaceAddress.setText(placeAddress);

        holder.textPlaceTypes.setText(place.types);
        holder.onItemActionListerner = onItemActionListerner;
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}
