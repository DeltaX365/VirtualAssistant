package com.vnsolution.virtualassistant.ui.phonesms.v1;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.PhoneSms;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

import java.util.List;

public class SmsAdapter extends RecyclerView.Adapter<SmsViewHolder> {
    private Context context;
    private List<PhoneSms> dataSource;
    private OnItemActionListerner onItemActionListerner;

    public SmsAdapter(Context context, List<PhoneSms> dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    public void setData(List<PhoneSms> phoneSmsList) {
        this.dataSource = phoneSmsList;
        notifyDataSetChanged();
    }

    public void setHandler(OnItemActionListerner onItemActionListerner) {
        this.onItemActionListerner = onItemActionListerner;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public SmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_phone_call, parent, false);
        SmsViewHolder viewHolder = new SmsViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull SmsViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            holder.imageCallIcon.setImageResource(R.drawable.ic_baseline_inbox_24);
            holder.textPhoneNumber.setText("From: " + dataSource.get(position).address);
            holder.textCallDate.setText("Message: " + dataSource.get(position).body);
            holder.textCallDuration.setText("Date: " + DateUtils.formatDateTime(context, dataSource.get(position).dateSent, DateUtils.FORMAT_ABBREV_ALL));
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
