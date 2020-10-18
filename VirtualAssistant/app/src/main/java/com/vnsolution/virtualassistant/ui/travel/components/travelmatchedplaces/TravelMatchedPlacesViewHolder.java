package com.vnsolution.virtualassistant.ui.travel.components.travelmatchedplaces;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class TravelMatchedPlacesViewHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView textPlaceName;
    public TextView textPlaceAddress;
    public TextView textPlaceType;

    public TravelMatchedPlacesViewHolder(View view) {
        super(view);
        this.view = view;
        this.textPlaceName = view.findViewById(R.id.place_name);
        this.textPlaceAddress = view.findViewById(R.id.place_address);
        this.textPlaceType = view.findViewById(R.id.place_type);
    }
}