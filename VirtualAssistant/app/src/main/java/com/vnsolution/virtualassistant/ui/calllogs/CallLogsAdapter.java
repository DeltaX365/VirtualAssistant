package com.vnsolution.virtualassistant.ui.calllogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.CallLog;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

import java.util.List;

public class CallLogsAdapter extends RecyclerView.Adapter<CallLogsViewHolder> {
    private Context context;
    private List<CallLog> dataSource;
    private OnItemActionListerner onItemActionListerner;

    public CallLogsAdapter(Context context, List<CallLog> dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    public void setData(List<CallLog> callLogs) {
        this.dataSource = callLogs;
        notifyDataSetChanged();
    }

    public void setHandler(OnItemActionListerner onItemActionListerner) {
        this.onItemActionListerner = onItemActionListerner;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CallLogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_call_log, parent, false);
        CallLogsViewHolder viewHolder = new CallLogsViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull CallLogsViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            switch (dataSource.get(position).callDirectionCode) {
                case android.provider.CallLog.Calls.INCOMING_TYPE:
                    holder.imageCallIcon.setImageResource(R.drawable.ic_call_received_black_24dp);
                    break;
                case android.provider.CallLog.Calls.OUTGOING_TYPE:
                    holder.imageCallIcon.setImageResource(R.drawable.ic_call_made_black_24dp);
                    break;
                case android.provider.CallLog.Calls.MISSED_TYPE:
                    holder.imageCallIcon.setImageResource(R.drawable.ic_call_missed_black_24dp);
                    break;
            }
            holder.textPhoneNumber.setText(dataSource.get(position).phoneNumber);
            holder.textCallDate.setText("Time: " + dataSource.get(position).callDate);
            holder.textCallDuration.setText("Duration: " + dataSource.get(position).callDuration);
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
