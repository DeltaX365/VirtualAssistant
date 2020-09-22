package com.vnsolution.virtual_assistant.ui.phone_contacts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtual_assistant.R;
import com.vnsolution.virtual_assistant.di.Injectable;
import com.vnsolution.virtual_assistant.model.PhoneContact;
import com.vnsolution.virtual_assistant.ui.phone_apps.v1.PhoneAppsRecycleView;
import com.vnsolution.virtual_assistant.ui.common.OnItemActionListerner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PhoneContactsFragment extends Fragment implements Injectable, OnItemActionListerner {
    private PhoneAppsRecycleView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PhoneContactsAdapter phoneContactsAdapter;
    private List<PhoneContact> phoneContacts = new ArrayList<>();
    private Context mContext;

    @Inject
    public PhoneContactsViewModel phoneContactsViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.screen_phone_apps, container, false);

        if (mContext != null) {
            recyclerView = root.findViewById(R.id.list_applications);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter
            phoneContactsAdapter = new PhoneContactsAdapter(mContext, phoneContacts);
            recyclerView.setAdapter(phoneContactsAdapter);
            recyclerView.setOnItemAction(this);

            // retrieve data
            phoneContactsViewModel.getContacts(mContext,false).observe(getViewLifecycleOwner(), new Observer<List<PhoneContact>>() {
                @Override
                public void onChanged(List<PhoneContact> phoneContacts) {
                    phoneContacts = phoneContacts;
                    phoneContactsAdapter.setData(phoneContacts);
                }
            });
        }

        return root;
    }

    @Override
    public void onTap(View view, int position) {
    }

    @Override
    public void onLongTap(View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Are you sure?");
        builder.setMessage("Removed item will never be restored. Tap Yes to remove this item.");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    public void onDoubleTap(View view, int position) {
    }
}
