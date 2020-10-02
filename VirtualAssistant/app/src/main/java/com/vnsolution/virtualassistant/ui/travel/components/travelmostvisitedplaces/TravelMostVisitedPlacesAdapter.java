package com.vnsolution.virtualassistant.ui.travel.components.travelmostvisitedplaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.db.convertor.DateConverter;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

import java.util.List;

public class TravelMostVisitedPlacesAdapter extends RecyclerView.Adapter<TravelMostVisitedPlacesViewHolder> {
    private Context context;
    private List<Place> dataSource;
    private List<PlaceType> placeTypes;
    private OnItemActionListerner onItemActionListerner;

    public TravelMostVisitedPlacesAdapter(Context context, List<Place> dataSource, List<PlaceType> placeTypes) {
        this.context = context;
        this.dataSource = dataSource;
        this.placeTypes = placeTypes;
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
        View view = inflater.inflate(R.layout.card_place_detail, parent, false);
        TravelMostVisitedPlacesViewHolder viewHolder = new TravelMostVisitedPlacesViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull TravelMostVisitedPlacesViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Place place = dataSource.get(position);
        if (place.photoReference != null && !place.photoReference.isEmpty()) {
            String photoUri = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&photoreference=" + place.photoReference + "&key=" + Constants.GM_API_KEY;
            Glide.with(context).load(photoUri).into(holder.ivPlacePhoto);
        }

        String placeName = (place.placeName == null || place.placeName == "") ? "Unknown" : place.placeName;
        holder.textPlaceName.setText(placeName);

        String placeAddress = (place.formattedAddress == null || place.formattedAddress == "") ? (place.latitude + "," + place.longitude) : place.formattedAddress;
        holder.textPlaceAddress.setText(placeAddress);

        PlaceType placeType = searchPlaceType(placeTypes, place.types);
        if (placeType != null) {
            holder.textPlaceType.setText(placeType.name);
            holder.textPlaceType.setVisibility(View.VISIBLE);
        } else {
            holder.textPlaceType.setVisibility(View.GONE);
        }

        if (place.placeHistories != null && place.placeHistories.size() > 0) {
            String totalVisit = place.placeHistories.size() + " " + ((place.placeHistories.size() == 1)? "visit" : "visits");
            holder.textPlaceTotalVisit.setText(totalVisit);
            holder.textPlaceTotalVisit.setVisibility(View.VISIBLE);

            int latestIndex = place.placeHistories.size() - 1;
            String latestEnterTime = DateConverter.toString(place.placeHistories.get(latestIndex).enterTime);
            holder.textPlaceEnterDate.setText(latestEnterTime);
            holder.textPlaceEnterDate.setVisibility(View.VISIBLE);
        } else {
            holder.textPlaceTotalVisit.setVisibility(View.GONE);
            holder.textPlaceEnterDate.setVisibility(View.GONE);
        }

        holder.onItemActionListerner = onItemActionListerner;
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    private PlaceType searchPlaceType(List<PlaceType> placeTypes, String tags) {
        PlaceType placeType = null;
        if (tags != null) {
            for (int i = 0; i < placeTypes.size(); i++) {
                if (tags.contains(placeTypes.get(i).tag)) {
                    placeType = placeTypes.get(i);
                }
            }
        }
        return placeType;
    }
}
