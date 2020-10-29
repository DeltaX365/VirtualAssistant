package com.vnsolution.virtualassistant.ui.phonesms.v1;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

public class SmsViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageCallIcon;
    public TextView textPhoneNumber;
    public TextView textCallDate;
    public TextView textCallDuration;
    public OnRecyclerViewRowTouchListener onRecyclerViewRowTouchListener;

    public SmsViewHolder(View view) {
        super(view);

        imageCallIcon = view.findViewById(R.id.call_icon);
        textPhoneNumber = view.findViewById(R.id.phone_number);
        textCallDate = view.findViewById(R.id.call_date);
        textCallDuration = view.findViewById(R.id.call_duration);
    }
}