package com.vnsolution.virtualassistant.ui.app.v1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.di.Injectable;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.ui.app.AppViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PhoneAppsFragment extends Fragment implements Injectable {
    public static final String ARG_SECTION_NUMBER = "section_number";

    private PhoneAppsRecycleView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout progressOverlay;

    private PhoneAppsAdapter adapter;
    private List<PhoneApp> installedApplications = new ArrayList<>();
    private AppViewModel vm;
    private Context mContext;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Inject
    public PhoneAppsFragment() {
        super();
    }

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

    public static PhoneAppsFragment newInstance(int index) {
        PhoneAppsFragment fragment = new PhoneAppsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = ViewModelProviders.of(this, viewModelFactory).get(AppViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        vm.setIndex(index);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.screen_app_list, container, false);

        if (mContext != null) {
            recyclerView = root.findViewById(R.id.list_applications);
            progressOverlay =  root.findViewById(R.id.progress_overlay);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter
            adapter = new PhoneAppsAdapter(mContext, installedApplications);
            recyclerView.setAdapter(adapter);

            // retrieve data
            AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressOverlay.setAnimation(inAnimation);
            progressOverlay.setVisibility(View.VISIBLE);

            vm.getPhoneAppsAsync(mContext,false, false).observe(getViewLifecycleOwner(), new Observer<List<PhoneApp>>() {
                @Override
                public void onChanged(List<PhoneApp> applications) {
                    installedApplications = applications;
                    adapter.setData(applications);

                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressOverlay.setAnimation(outAnimation);
                    progressOverlay.setVisibility(View.INVISIBLE);
                }
            });
        }

        return root;
    }
}
