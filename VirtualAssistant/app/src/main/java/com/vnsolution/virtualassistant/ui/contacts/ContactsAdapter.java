package com.vnsolution.virtualassistant.ui.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.Contact;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsViewHolder> {
    private Context context;
    private List<Contact> dataSource;
    private OnItemActionListerner onItemActionListerner;

    public ContactsAdapter(Context context, List<Contact> dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    public void setData(List<Contact> contacts) {
        this.dataSource = contacts;
        notifyDataSetChanged();
    }

    public void setHandler(OnItemActionListerner onItemActionListerner) {
        this.onItemActionListerner = onItemActionListerner;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_contact, parent, false);
        ContactsViewHolder viewHolder = new ContactsViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            Glide.with(context).load(dataSource.get(position).photoUri).into(holder.avatar);
            holder.name.setText(dataSource.get(position).name);
            holder.phoneNumber.setText("Number: " + dataSource.get(position).mobileNumbers.get(0));
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
