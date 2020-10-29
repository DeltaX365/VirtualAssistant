package com.vnsolution.virtualassistant.ui.phoneapps.v1;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

public class PhoneAppsViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageAppIcon;
    public TextView textAppName;
    public TextView textAppUsage;
    public TextView textAppLastUsedTime;
    public OnRecyclerViewRowTouchListener onRecyclerViewRowTouchListener;

    public PhoneAppsViewHolder(View view) {
        super(view);

        imageAppIcon = view.findViewById(R.id.app_icon);
        textAppName = view.findViewById(R.id.app_name);
        textAppUsage = view.findViewById(R.id.app_usage);
        textAppLastUsedTime = view.findViewById(R.id.app_last_used_time);
    }
}