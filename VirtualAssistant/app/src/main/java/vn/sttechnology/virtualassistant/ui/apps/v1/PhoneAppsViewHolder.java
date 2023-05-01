package vn.sttechnology.virtualassistant.ui.apps.v1;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

public class PhoneAppsViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageAppIcon;
    public TextView textAppName;
    public TextView textAppUsage;
    public OnRecyclerViewRowTouchListener onRecyclerViewRowTouchListener;

    public PhoneAppsViewHolder(View view) {
        super(view);

        imageAppIcon = view.findViewById(R.id.app_icon);
        textAppName = view.findViewById(R.id.app_name);
        textAppUsage = view.findViewById(R.id.app_last_usage);
    }
}