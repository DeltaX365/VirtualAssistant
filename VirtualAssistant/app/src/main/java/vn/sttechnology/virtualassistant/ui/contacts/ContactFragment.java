package vn.sttechnology.virtualassistant.ui.contacts;

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

import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.di.Injectable;
import vn.sttechnology.virtualassistant.model.PhoneContact;
import vn.sttechnology.virtualassistant.ui.apps.v1.PhoneAppsRecycleView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ContactFragment extends Fragment implements Injectable {
    private PhoneAppsRecycleView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ContactAdapter contactAdapter;
    private List<PhoneContact> phoneContacts = new ArrayList<>();
    private Context mContext;

    @Inject
    public ContactViewModel contactViewModel;

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
        View root = inflater.inflate(R.layout.screen_app_list, container, false);

        if (mContext != null) {
            recyclerView = root.findViewById(R.id.list_phone_apps);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter
            contactAdapter = new ContactAdapter(mContext, phoneContacts);
            recyclerView.setAdapter(contactAdapter);

            // retrieve data
            contactViewModel.getContacts(mContext,false).observe(getViewLifecycleOwner(), new Observer<List<PhoneContact>>() {
                @Override
                public void onChanged(List<PhoneContact> phoneContacts) {
                    phoneContacts = phoneContacts;
                    contactAdapter.setData(phoneContacts);
                }
            });
        }

        return root;
    }
}
