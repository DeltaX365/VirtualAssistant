package com.vnsolution.virtualassistant.ui.places;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class VisitedPlacesViewHolder extends RecyclerView.ViewHolder {
    public TextView textPlaceName;
    public TextView textPlaceAddress;
    public OnItemActionListerner onItemActionListerner;

    public VisitedPlacesViewHolder(View view) {
        super(view);
        textPlaceName = view.findViewById(R.id.text_place_name);
        textPlaceAddress = view.findViewById(R.id.text_place_address);
        view.setOnTouchListener(new View.OnTouchListener() {

            private GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (onItemActionListerner != null) {
                        onItemActionListerner.onDoubleTap(view, getAdapterPosition());
                    }
                    return true;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (onItemActionListerner != null) {
                        onItemActionListerner.onTap(view, getAdapterPosition());
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    if (onItemActionListerner != null) {
                        onItemActionListerner.onLongTap(view, getAdapterPosition());
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
}