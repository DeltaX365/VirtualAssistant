package com.vnsolution.virtualassistant.ui.places;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;

import java.util.List;

public class VisitedPlacesAdapter extends RecyclerView.Adapter<VisitedPlacesAdapter.VisitedPlacesViewHolder> {
    private List<String> dataSource;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class VisitedPlacesViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public VisitedPlacesViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public VisitedPlacesAdapter(List<String> data) {
        dataSource = data;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public VisitedPlacesAdapter.VisitedPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visited_place, parent, false);
        VisitedPlacesViewHolder vh = new VisitedPlacesViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull VisitedPlacesAdapter.VisitedPlacesViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(dataSource.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}
