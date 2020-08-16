package com.vnsolution.virtualassistant.ui.places;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.di.Injectable;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceHistory;
import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class VisitedPlacesFragment extends Fragment implements Injectable, OnItemActionListerner {
    private VisitedPlacesRecycleView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout progressOverlay;

    private VisitedPlacesAdapter visitedPlaceAdapter;
    private List<Place> visitedPlaces = new ArrayList<>();
    private VisitedPlacesViewModel visitedPlaceViewModel;
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
        visitedPlaceViewModel = ViewModelProviders.of(this, viewModelFactory).get(VisitedPlacesViewModel.class);
        View root = inflater.inflate(R.layout.screen_visited_places, container, false);

        if (mContext != null) {
            recyclerView = root.findViewById(R.id.list_visited_places);
            progressOverlay =  root.findViewById(R.id.progress_overlay);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter
            visitedPlaceAdapter = new VisitedPlacesAdapter(mContext, visitedPlaces);
            recyclerView.setAdapter(visitedPlaceAdapter);
            recyclerView.setOnItemAction(this);

            // retrieve data
            AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressOverlay.setAnimation(inAnimation);
            progressOverlay.setVisibility(View.VISIBLE);

            visitedPlaceViewModel.getVisitedPlaces().observe(getViewLifecycleOwner(), new Observer<List<Place>>() {
                @Override
                public void onChanged(@Nullable List<Place> places) {
                    visitedPlaces = places;
                    visitedPlaceAdapter.setData(places);

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
        String message = "";
        if (visitedPlaces != null && position >= 0 && position <= visitedPlaces.size() - 1) {
            Place place = visitedPlaces.get(position);
            message += "Name: " + place.placeName + "\n";
            message += "Address: " + place.formattedAddress + "\n";
            message += "Location: (" + place.latitude + ", " + place.longitude + ")" + "\n\n";
            message += "History:" + "\n";
            int index = 0;
            for(PlaceHistory placeHistory: place.placeHistories) {
                message += index + ". " + "\n";
                message += "Enter: " + placeHistory.enterTime + "\n";
                message += "Exist: " + placeHistory.existTime + "\n";
                index++;
            }
        }
        if (!message.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Item detail");
            builder.setMessage(message);
            builder.setCancelable(true);
            builder.setNeutralButton("Ok", null);
            builder.show();
        }
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
