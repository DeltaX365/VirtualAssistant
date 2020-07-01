package com.vnsolution.virtualassistant.ui.applications;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public class InstalledApplicationsViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageAppIcon;
    public TextView textAppName;
    public TextView textVersionCode;
    public TextView textVersionName;
    public TextView textPackageName;
    public OnItemActionListerner onItemActionListerner;

    public InstalledApplicationsViewHolder(View view) {
        super(view);

        imageAppIcon = view.findViewById(R.id.app_icon);
        textAppName = view.findViewById(R.id.app_name);
        textVersionCode = view.findViewById(R.id.version_code);
        textVersionName = view.findViewById(R.id.version_name);
        textPackageName = view.findViewById(R.id.package_name);

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