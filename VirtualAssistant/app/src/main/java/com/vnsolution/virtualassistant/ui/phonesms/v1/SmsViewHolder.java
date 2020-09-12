package com.vnsolution.virtualassistant.ui.phonesms.v1;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class SmsViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageCallIcon;
    public TextView textPhoneNumber;
    public TextView textCallDate;
    public TextView textCallDuration;
    public OnItemActionListerner onItemActionListerner;

    public SmsViewHolder(View view) {
        super(view);

        imageCallIcon = view.findViewById(R.id.call_icon);
        textPhoneNumber = view.findViewById(R.id.phone_number);
        textCallDate = view.findViewById(R.id.call_date);
        textCallDuration = view.findViewById(R.id.call_duration);

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