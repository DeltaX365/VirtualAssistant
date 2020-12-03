package com.vnsolution.virtualassistant.ui.phonecontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

import java.util.List;

public class PhoneContactAdapter extends RecyclerView.Adapter<PhoneContactViewHolder> {
    private Context context;
    private List<PhoneContact> dataSource;
    private OnRecyclerViewRowTouchListener onRecyclerViewRowTouchListener;

    public PhoneContactAdapter(Context context, List<PhoneContact> dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    public void setData(List<PhoneContact> phoneContacts) {
        this.dataSource = phoneContacts;
        notifyDataSetChanged();
    }

    public void setHandler(OnRecyclerViewRowTouchListener onRecyclerViewRowTouchListener) {
        this.onRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public PhoneContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_contact_full_width, parent, false);
        PhoneContactViewHolder viewHolder = new PhoneContactViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull PhoneContactViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
//            Glide.with(context).load(dataSource.get(position).photoUri).into(holder.avatar);
            holder.name.setText(dataSource.get(position).name);
            holder.phoneNumbers.setText("Number: " + dataSource.get(position).phoneNumbers);
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
