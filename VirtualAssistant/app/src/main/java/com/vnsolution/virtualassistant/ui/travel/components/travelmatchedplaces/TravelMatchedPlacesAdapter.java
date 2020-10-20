package com.vnsolution.virtualassistant.ui.travel.components.travelmatchedplaces;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.api.dto.GoogleMapNearByDto;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.ui.travel.components.travelvisitedplaces.TravelVisitedPlaceViewHolder;
import com.vnsolution.virtualassistant.ui.travel.events.OnTravelActionListerner;
import com.vnsolution.virtualassistant.util.StringUtils;

import java.util.List;

public class TravelMatchedPlacesAdapter extends RecyclerView.Adapter<TravelMatchedPlacesViewHolder> {
    private Context context;
    private List<GoogleMapNearByDto.NearBy> dataSource;
    private List<PlaceType> placeTypes;
    private OnTravelActionListerner onTravelActionListerner;

    public TravelMatchedPlacesAdapter(Context context, List<GoogleMapNearByDto.NearBy> dataSource, List<PlaceType> placeTypes) {
        this.context = context;
        this.dataSource = dataSource;
        this.placeTypes = placeTypes;
    }

    public void setData(List<GoogleMapNearByDto.NearBy> nearByList) {
        this.dataSource = nearByList;
        notifyDataSetChanged();
    }

    public void setHandler(OnTravelActionListerner onTravelActionListerner) {
        this.onTravelActionListerner = onTravelActionListerner;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public TravelMatchedPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_matched_place, parent, false);
        TravelMatchedPlacesViewHolder viewHolder = new TravelMatchedPlacesViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull TravelMatchedPlacesViewHolder holder, int position) {
        populateData(holder, position);
        registerEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    private void populateData(TravelMatchedPlacesViewHolder holder, int position) {
        if (holder == null || dataSource == null || dataSource.size() < 0 || position >= dataSource.size()) return;
        GoogleMapNearByDto.NearBy nearBy = dataSource.get(position);

        // Place name
        String placeName = (nearBy.name == null || nearBy.name == "") ? "Unknown Address" : nearBy.name;
        holder.textPlaceName.setText(placeName);

        // Place address
        double latitude = (nearBy.geometry != null && nearBy.geometry.location != null)? nearBy.geometry.location.lat : 0;
        double longitude = (nearBy.geometry != null && nearBy.geometry.location != null)? nearBy.geometry.location.lng : 0;
        latitude = Math.round(latitude * Math.pow(10, 7)) / Math.pow(10, 7);
        longitude = Math.round(longitude * Math.pow(10, 7)) / Math.pow(10, 7);
        String placeAddress = (nearBy.vicinity == null || nearBy.vicinity == "") ? (latitude + "," + longitude) : nearBy.vicinity;
        holder.textPlaceAddress.setText(placeAddress);

        // Place category
        PlaceType placeType = searchPlaceType(placeTypes, StringUtils.join(", ", nearBy.types));
        if (placeType != null) {
            holder.textPlaceType.setText(placeType.name);
            holder.textPlaceType.setVisibility(View.VISIBLE);
        } else {
            holder.textPlaceType.setVisibility(View.GONE);
        }
    }

    private void registerEvents(TravelMatchedPlacesViewHolder holder, int position) {
        if (holder == null) return;

        holder.view.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (onTravelActionListerner != null) {
                        onTravelActionListerner.onDoubleTap(holder.view, position);
                    }
                    return true;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (onTravelActionListerner != null) {
                        onTravelActionListerner.onTap(holder.view, position);
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    if (onTravelActionListerner != null) {
                        onTravelActionListerner.onLongTap(holder.view, position);
                    }
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
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
