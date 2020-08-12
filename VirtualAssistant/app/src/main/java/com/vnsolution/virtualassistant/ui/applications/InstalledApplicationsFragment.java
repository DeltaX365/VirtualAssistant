package com.vnsolution.virtualassistant.ui.applications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.di.Injectable;
import com.vnsolution.virtualassistant.model.Application;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class InstalledApplicationsFragment extends Fragment implements Injectable, OnItemActionListerner {
    private InstalledApplicationsRecycleView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout progressOverlay;

    private InstalledApplicationsAdapter installedApplicationAdapter;
    private List<Application> installedApplications = new ArrayList<>();
    private InstalledApplicationsViewModel applicationViewModel;
    private Context mContext;

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
        applicationViewModel = new InstalledApplicationsViewModel(getContext());
        View root = inflater.inflate(R.layout.fragment_installed_applications, container, false);

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
            installedApplicationAdapter = new InstalledApplicationsAdapter(mContext, installedApplications);
            recyclerView.setAdapter(installedApplicationAdapter);
            recyclerView.setOnItemAction(this);

            // retrieve data
            AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressOverlay.setAnimation(inAnimation);
            progressOverlay.setVisibility(View.VISIBLE);

            applicationViewModel.getInstalledApplications(false, false).observe(getViewLifecycleOwner(), new Observer<List<Application>>() {
                @Override
                public void onChanged(List<Application> applications) {
                    installedApplications = applications;
                    installedApplicationAdapter.setData(applications);

                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressOverlay.setAnimation(outAnimation);
                    progressOverlay.setVisibility(View.INVISIBLE);
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
