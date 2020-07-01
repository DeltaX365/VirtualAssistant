package com.vnsolution.virtualassistant.ui.applications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.Application;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

import java.util.List;

public class InstalledApplicationsAdapter extends RecyclerView.Adapter<InstalledApplicationsViewHolder> {
    private List<Application> dataSource;
    private OnItemActionListerner onItemActionListerner;

    public InstalledApplicationsAdapter(List<Application> dataSource) {
        this.dataSource = dataSource;
    }

    public void setData(List<Application> applications) {
        this.dataSource = applications;
        notifyDataSetChanged();
    }

    public void setHandler(OnItemActionListerner onItemActionListerner) {
        this.onItemActionListerner = onItemActionListerner;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public InstalledApplicationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_installed_application, parent, false);
        InstalledApplicationsViewHolder viewHolder = new InstalledApplicationsViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull InstalledApplicationsViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            holder.imageAppIcon.setImageDrawable(dataSource.get(position).appIcon);
            holder.textAppName.setText(dataSource.get(position).appName);
            holder.textVersionCode.setText(dataSource.get(position).versionCode);
            holder.textVersionName.setText(dataSource.get(position).versionName);
            holder.textPackageName.setText(dataSource.get(position).packageName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        holder.onItemActionListerner = onItemActionListerner;
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}
