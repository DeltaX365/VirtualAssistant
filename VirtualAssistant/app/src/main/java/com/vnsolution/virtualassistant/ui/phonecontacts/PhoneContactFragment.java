package com.vnsolution.virtualassistant.ui.phonecontacts;

import android.content.Context;
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

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.di.Injectable;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.phoneapps.v1.PhoneAppsRecycleView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PhoneContactFragment extends Fragment implements Injectable {
    private PhoneAppsRecycleView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PhoneContactAdapter phoneContactAdapter;
    private List<PhoneContact> phoneContacts = new ArrayList<>();
    private Context mContext;

    @Inject
    public PhoneContactViewModel phoneContactViewModel;

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
            phoneContactAdapter = new PhoneContactAdapter(mContext, phoneContacts);
            recyclerView.setAdapter(phoneContactAdapter);

            // retrieve data
            phoneContactViewModel.getContacts(mContext,false).observe(getViewLifecycleOwner(), new Observer<List<PhoneContact>>() {
                @Override
                public void onChanged(List<PhoneContact> phoneContacts) {
                    phoneContacts = phoneContacts;
                    phoneContactAdapter.setData(phoneContacts);
                }
            });
        }

        return root;
    }
}
