package com.vnsolution.virtualassistant.ui.phoneapps.v1;

import android.content.Context;
import android.icu.text.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

import java.util.List;

public class PhoneAppsAdapter extends RecyclerView.Adapter<PhoneAppsViewHolder> {
    private Context context;
    private List<PhoneApp> dataSource;
    private OnRecyclerViewRowTouchListener onRecyclerViewRowTouchListener;

    public PhoneAppsAdapter(Context context, List<PhoneApp> dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    public void setData(List<PhoneApp> applications) {
        this.dataSource = applications;
        notifyDataSetChanged();
    }

    public void setHandler(OnRecyclerViewRowTouchListener onRecyclerViewRowTouchListener) {
        this.onRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public PhoneAppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_app, parent, false);
        PhoneAppsViewHolder viewHolder = new PhoneAppsViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull PhoneAppsViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            Glide.with(context).load(dataSource.get(position).appIcon).into(holder.imageAppIcon);
            holder.textAppName.setText(dataSource.get(position).appName + " (v" + dataSource.get(position).versionName + ")");
            String appLastUsedTime = "";
            String appUsageTime = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                appLastUsedTime += DateUtils.formatSameDayTime(dataSource.get(position).usageStats.getLastTimeUsed(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM);
                appUsageTime += DateUtils.formatElapsedTime(dataSource.get(position).usageStats.getTotalTimeInForeground() / 1000);
            }
//            holder.textAppLastUsedTime.setText("Last time used: " + appLastUsedTime);
            holder.textAppUsage.setText("App usage: " + appUsageTime);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        holder.onRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}
