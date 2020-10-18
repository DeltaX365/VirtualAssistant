package com.vnsolution.virtualassistant.ui.travel.components.travelvisitedplaces;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;

public class TravelVisitedPlaceViewHolder extends RecyclerView.ViewHolder {
    public View view;
    public ImageView ivPlacePhoto;
    public TextView textPlaceConfirm;
    public TextView textPlaceName;
    public TextView textPlaceAddress;
    public TextView textPlaceType;
    public TextView textPlaceTotalVisit;
    public TextView textPlaceEnterDate;
    public ViewGroup viewPlaceActionGroup;
    public Button btnConfirmPlace;
    public Button btnEditPlace;

    public TravelVisitedPlaceViewHolder(View view) {
        super(view);

        this.view = view;
        this.ivPlacePhoto = view.findViewById(R.id.place_photo);
        this.textPlaceConfirm = view.findViewById(R.id.place_confirm);
        this.textPlaceName = view.findViewById(R.id.place_name);
        this.textPlaceAddress = view.findViewById(R.id.place_address);
        this.textPlaceType = view.findViewById(R.id.place_type);
        this.textPlaceTotalVisit = view.findViewById(R.id.place_total_visit);
        this.textPlaceEnterDate = view.findViewById(R.id.place_enter_date);
        this.viewPlaceActionGroup = view.findViewById(R.id.place_action_group);
        this.btnConfirmPlace = view.findViewById(R.id.confirm_place);
        this.btnEditPlace = view.findViewById(R.id.edit_place);
    }
}