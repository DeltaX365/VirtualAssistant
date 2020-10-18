package com.vnsolution.virtualassistant.ui.travel.components.travelvisitedplaces;

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
import com.vnsolution.virtualassistant.ui.travel.events.OnTravelActionListerner;

import java.util.List;

public class TravelVisitedPlaceAdapter extends RecyclerView.Adapter<TravelVisitedPlaceViewHolder> {
    private Context context;
    private List<Place> dataSource;
    private List<PlaceType> placeTypes;
    private OnTravelActionListerner onTravelActionListerner;

    public TravelVisitedPlaceAdapter(Context context, List<Place> dataSource, List<PlaceType> placeTypes) {
        this.context = context;
        this.dataSource = dataSource;
        this.placeTypes = placeTypes;
    }

    public void setData(List<Place> places) {
        this.dataSource = places;
        notifyDataSetChanged();
    }

    public void setHandler(OnTravelActionListerner onTravelActionListerner) {
        this.onTravelActionListerner = onTravelActionListerner;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public TravelVisitedPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_place_detail, parent, false);
        TravelVisitedPlaceViewHolder viewHolder = new TravelVisitedPlaceViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull TravelVisitedPlaceViewHolder holder, int position) {
        populateData(holder, position);
        registerEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    private void populateData(TravelVisitedPlaceViewHolder holder, int position) {
        if (holder == null || dataSource == null || dataSource.size() < 0 || position >= dataSource.size()) return;
        Place place = dataSource.get(position);

        // Place photo
        if (place.photoReference != null && !place.photoReference.isEmpty()) {
            String photoUri = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=320&photoreference=" + place.photoReference + "&key=" + Constants.GM_API_KEY;
            Glide.with(context).load(photoUri).into(holder.ivPlacePhoto);
        }

        // Place name
        String placeName = (place.placeName == null || place.placeName == "") ? "Unknown Address" : place.placeName;
        holder.textPlaceName.setText(placeName);

        // Place address
        String placeAddress = (place.formattedAddress == null || place.formattedAddress == "") ? (place.latitude + "," + place.longitude) : place.formattedAddress;
        holder.textPlaceAddress.setText(placeAddress);

        // Place category
        PlaceType placeType = searchPlaceType(placeTypes, place.types);
        if (placeType != null) {
            holder.textPlaceType.setText(placeType.name);
            holder.textPlaceType.setVisibility(View.VISIBLE);
        } else {
            holder.textPlaceType.setVisibility(View.GONE);
        }

        // Place history
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

        // Place confirm
        if (place.confirmed == 1) {
            holder.textPlaceConfirm.setVisibility(View.GONE);
            holder.viewPlaceActionGroup.setVisibility(View.GONE);
        } else {
            holder.textPlaceConfirm.setVisibility(View.VISIBLE);
            holder.viewPlaceActionGroup.setVisibility(View.VISIBLE);
        }
    }

    private void registerEvents(TravelVisitedPlaceViewHolder holder, int position) {
        if (holder == null || dataSource == null || dataSource.size() < 0 || position >= dataSource.size()) return;
        Place place = dataSource.get(position);

        holder.btnConfirmPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTravelActionListerner != null) {
                    onTravelActionListerner.onPlaceConfirmed(place.id);
                }
            }
        });
        holder.btnEditPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTravelActionListerner != null) {
                    onTravelActionListerner.onPlaceEdited(place.id);
                }
            }
        });
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
