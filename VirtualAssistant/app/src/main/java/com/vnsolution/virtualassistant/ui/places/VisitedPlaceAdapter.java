package com.vnsolution.virtualassistant.ui.places;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.PlaceHistory;

import java.util.List;

public class VisitedPlaceAdapter extends RecyclerView.Adapter<VisitedPlaceAdapter.VisitedPlacesViewHolder> {
    private List<PlaceHistory> dataSource;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class VisitedPlacesViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView textPlaceName;
        public TextView textPlaceAddress;

        public VisitedPlacesViewHolder(View view) {
            super(view);
            view.setOnLongClickListener(this);
            textPlaceName = view.findViewById(R.id.text_place_name);
            textPlaceAddress = view.findViewById(R.id.text_place_address);
        }

        @Override
        public boolean onLongClick(View v) {
            Thread deletePlaceThread = new Thread(new Runnable() {
                @Override
                public void run() {

                }
            });
            deletePlaceThread.start();
            return false;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public VisitedPlaceAdapter(List<PlaceHistory> data) {
        dataSource = data;
    }

    public void setData(List<PlaceHistory> placeHistories) {
        dataSource = placeHistories;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public VisitedPlaceAdapter.VisitedPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_visited_place, parent, false);
        VisitedPlacesViewHolder viewHolder = new VisitedPlacesViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull VisitedPlaceAdapter.VisitedPlacesViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textPlaceName.setText(dataSource.get(position).placeName);
        holder.textPlaceAddress.setText(dataSource.get(position).formattedAddress);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}
