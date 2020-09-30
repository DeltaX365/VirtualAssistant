package com.vnsolution.virtualassistant.ui.travel.components.travelrecentlyvisitedplaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

import java.util.List;

public class TravelRecentlyVisitedPlacesAdapter extends RecyclerView.Adapter<TravelRecentlyVisitedPlacesViewHolder> {
    private Context context;
    private List<Place> dataSource;
    private OnItemActionListerner onItemActionListerner;

    public TravelRecentlyVisitedPlacesAdapter(Context context, List<Place> dataSource) {
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
    public TravelRecentlyVisitedPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_place_detail, parent, false);
        TravelRecentlyVisitedPlacesViewHolder viewHolder = new TravelRecentlyVisitedPlacesViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull TravelRecentlyVisitedPlacesViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Place place = dataSource.get(position);
        if (place.photoReference != null && !place.photoReference.isEmpty()) {
            String photoUri = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&photoreference=" + place.photoReference + "&key=" + Constants.GM_API_KEY;
            Glide.with(context).load(photoUri).into(holder.ivPlaceIcon);
        }

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
