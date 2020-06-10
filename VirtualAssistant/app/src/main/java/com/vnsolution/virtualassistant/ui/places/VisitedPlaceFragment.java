package com.vnsolution.virtualassistant.ui.places;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.di.Injectable;
import com.vnsolution.virtualassistant.model.PlaceHistory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class VisitedPlaceFragment extends Fragment implements Injectable {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private VisitedPlaceAdapter visitedPlaceAdapter;
    private List<PlaceHistory> visitedPlaces = new ArrayList<>();
    private VisitedPlaceViewModel visitedPlaceViewModel;
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
        visitedPlaceViewModel = ViewModelProviders.of(this, viewModelFactory).get(VisitedPlaceViewModel.class);
        View root = inflater.inflate(R.layout.fragment_visited_places, container, false);

        if (mContext != null) {
            recyclerView = root.findViewById(R.id.list_visited_places);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter
            visitedPlaceAdapter = new VisitedPlaceAdapter(visitedPlaces);
            recyclerView.setAdapter(visitedPlaceAdapter);

            visitedPlaceViewModel.getVisitedPlaces().observe(getViewLifecycleOwner(), new Observer<List<PlaceHistory>>() {
                @Override
                public void onChanged(@Nullable List<PlaceHistory> placeHistories) {
                    visitedPlaceAdapter.setData(placeHistories);
                }
            });
        }

        return root;
    }
}
