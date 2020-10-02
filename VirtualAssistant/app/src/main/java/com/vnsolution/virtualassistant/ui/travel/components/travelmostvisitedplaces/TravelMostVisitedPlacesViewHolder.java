package com.vnsolution.virtualassistant.ui.travel.components.travelmostvisitedplaces;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class TravelMostVisitedPlacesViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivPlacePhoto;
    public TextView textPlaceName;
    public TextView textPlaceAddress;
    public TextView textPlaceType;
    public TextView textPlaceTotalVisit;
    public TextView textPlaceEnterDate;
    public OnItemActionListerner onItemActionListerner;

    public TravelMostVisitedPlacesViewHolder(View view) {
        super(view);

        ivPlacePhoto = view.findViewById(R.id.place_photo);
        textPlaceName = view.findViewById(R.id.place_name);
        textPlaceAddress = view.findViewById(R.id.place_address);
        textPlaceType = view.findViewById(R.id.place_type);
        textPlaceTotalVisit = view.findViewById(R.id.place_total_visit);
        textPlaceEnterDate = view.findViewById(R.id.place_enter_date);

//        view.setOnTouchListener(new View.OnTouchListener() {
//
//            private GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
//                @Override
//                public boolean onDoubleTap(MotionEvent e) {
//                    if (onItemActionListerner != null) {
//                        onItemActionListerner.onDoubleTap(view, getAdapterPosition());
//                    }
//                    return true;
//                }
//
//                @Override
//                public boolean onSingleTapConfirmed(MotionEvent e) {
//                    if (onItemActionListerner != null) {
//                        onItemActionListerner.onTap(view, getAdapterPosition());
//                    }
//                    return true;
//                }
//
//                @Override
//                public void onLongPress(MotionEvent e) {
//                    if (onItemActionListerner != null) {
//                        onItemActionListerner.onLongTap(view, getAdapterPosition());
//                    }
//                }
//            });
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                gestureDetector.onTouchEvent(event);
//                return true;
//            }
//        });
    }
}