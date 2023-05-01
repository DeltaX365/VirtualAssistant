package vn.sttechnology.virtualassistant.ui.contacts;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    public ImageView avatar;
    public TextView name;
    public TextView phoneNumbers;
    public OnRecyclerViewRowTouchListener onRecyclerViewRowTouchListener;

    public ContactViewHolder(View view) {
        super(view);

        avatar = view.findViewById(R.id.contact_avatar);
        name = view.findViewById(R.id.contact_name);
        phoneNumbers = view.findViewById(R.id.phone_number);
    }
}