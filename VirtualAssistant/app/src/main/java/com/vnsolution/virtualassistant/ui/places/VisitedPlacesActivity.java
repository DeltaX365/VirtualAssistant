package com.vnsolution.virtualassistant.ui.places;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class VisitedPlacesActivity extends AppCompatActivity implements HasAndroidInjector, OnItemActionListerner {
    private VisitedPlacesRecycleView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout progressOverlay;

    private VisitedPlacesAdapter visitedPlaceAdapter;
    private List<Place> visitedPlaces = new ArrayList<>();
    private VisitedPlacesViewModel visitedPlaceViewModel;

    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_visited_places);

        recyclerView = findViewById(R.id.list_visited_places);
        progressOverlay =  findViewById(R.id.progress_overlay);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        visitedPlaceAdapter = new VisitedPlacesAdapter(visitedPlaces);
        recyclerView.setAdapter(visitedPlaceAdapter);
        recyclerView.setOnItemAction(this);

        // retrieve data
        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressOverlay.setAnimation(inAnimation);
        progressOverlay.setVisibility(View.VISIBLE);

        visitedPlaceViewModel = ViewModelProviders.of(this, viewModelFactory).get(VisitedPlacesViewModel.class);
        visitedPlaceViewModel.getVisitedPlaces().observe(this, new Observer<List<Place>>() {
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Item detail");
            builder.setMessage(message);
            builder.setCancelable(true);
            builder.setNeutralButton("Ok", null);
            builder.show();
        }
    }

    @Override
    public void onLongTap(View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}
