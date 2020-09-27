package com.vnsolution.virtualassistant.ui.travel.components.travelcategorizedplaces;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class TravelCategorizedVisitedPlacesViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivCategoryBackground;
    public TextView textCategoryName;
    public TextView textCategoryDetail;
    public OnItemActionListerner onItemActionListerner;

    public TravelCategorizedVisitedPlacesViewHolder(View view) {
        super(view);

        ivCategoryBackground = view.findViewById(R.id.category_background);
        textCategoryName = view.findViewById(R.id.category_name);
        textCategoryDetail = view.findViewById(R.id.category_detail);

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