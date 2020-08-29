package com.vnsolution.virtualassistant.ui.contacts;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class ContactsViewHolder extends RecyclerView.ViewHolder {
    public ImageView avatar;
    public TextView name;
    public TextView phoneNumber;
    public OnItemActionListerner onItemActionListerner;

    public ContactsViewHolder(View view) {
        super(view);

        avatar = view.findViewById(R.id.contact_avatar);
        name = view.findViewById(R.id.contact_name);
        phoneNumber = view.findViewById(R.id.contact_phone_number);

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