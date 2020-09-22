package com.vnsolution.virtual_assistant.ui.travel;

import android.app.AlertDialog;
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

import com.vnsolution.virtual_assistant.R;
import com.vnsolution.virtual_assistant.model.Place;
import com.vnsolution.virtual_assistant.model.PlaceHistory;
import com.vnsolution.virtual_assistant.ui.common.OnItemActionListerner;
import com.vnsolution.virtual_assistant.ui.travel.travel_categorized_visited_places.TravelCategorizedVisitedPlacesAdapter;
import com.vnsolution.virtual_assistant.ui.travel.travel_categorized_visited_places.TravelCategorizedVisitedPlacesRecycleView;
import com.vnsolution.virtual_assistant.ui.travel.travel_most_visited_places.TravelMostVisitedPlacesAdapter;
import com.vnsolution.virtual_assistant.ui.travel.travel_most_visited_places.TravelMostVisitedPlacesRecycleView;
import com.vnsolution.virtual_assistant.ui.travel.travel_recently_visited_places.TravelRecentlyVisitedPlacesAdapter;
import com.vnsolution.virtual_assistant.ui.travel.travel_recently_visited_places.TravelRecentlyVisitedPlacesRecycleView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class TravelActivity extends AppCompatActivity implements HasAndroidInjector, OnItemActionListerner {
    private TravelRecentlyVisitedPlacesRecycleView recentlyVisitedPlacesRecyclerView;
    private TravelRecentlyVisitedPlacesAdapter recentlyVisitedPlacesAdapter;
    private RecyclerView.LayoutManager recentlyVisitedPlacesLayoutManager;
    private List<Place> recentlyVisitedPlaces = new ArrayList<>();

    private TravelMostVisitedPlacesRecycleView mostVisitedPlacesRecyclerView;
    private TravelMostVisitedPlacesAdapter mostVisitedPlacesAdapter;
    private RecyclerView.LayoutManager mostVisitedPlacesLayoutManager;
    private List<Place> mostVisitedPlaces = new ArrayList<>();

    private TravelCategorizedVisitedPlacesRecycleView categorizedVisitedPlacesRecyclerView;
    private TravelCategorizedVisitedPlacesAdapter categorizedVisitedPlacesAdapter;
    private RecyclerView.LayoutManager categorizedVisitedPlacesLayoutManager;
    private List<Place> categorizedVisitedPlaces = new ArrayList<>();

    private FrameLayout progressOverlay;
    private TravelViewModel vm;

    @Inject
    public DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = ViewModelProviders.of(this, viewModelFactory).get(TravelViewModel.class);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.screen_travel);
        progressOverlay = findViewById(R.id.progress_overlay);

        setupRecentlyVisitedPlacesView();
        setupMostVisitedPlacesView();
        setupCategorizedVisitedPlacesView();
    }

    private void setupRecentlyVisitedPlacesView() {
        recentlyVisitedPlacesRecyclerView = findViewById(R.id.list_recently_visited_places);
        recentlyVisitedPlacesRecyclerView.setHasFixedSize(true);

        recentlyVisitedPlacesLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentlyVisitedPlacesRecyclerView.setLayoutManager(recentlyVisitedPlacesLayoutManager);

        recentlyVisitedPlacesAdapter = new TravelRecentlyVisitedPlacesAdapter(this, recentlyVisitedPlaces);
        recentlyVisitedPlacesRecyclerView.setAdapter(recentlyVisitedPlacesAdapter);
        recentlyVisitedPlacesRecyclerView.setOnItemAction(this);

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressOverlay.setAnimation(inAnimation);
        progressOverlay.setVisibility(View.VISIBLE);

        vm.getVisitedPlaces(1).observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                TravelActivity.this.recentlyVisitedPlaces = places;
                recentlyVisitedPlacesAdapter.setData(places);

                AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressOverlay.setAnimation(outAnimation);
                progressOverlay.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setupMostVisitedPlacesView() {
        mostVisitedPlacesRecyclerView = findViewById(R.id.list_most_visited_places);
        mostVisitedPlacesRecyclerView.setHasFixedSize(true);

        mostVisitedPlacesLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mostVisitedPlacesRecyclerView.setLayoutManager(mostVisitedPlacesLayoutManager);

        mostVisitedPlacesAdapter = new TravelMostVisitedPlacesAdapter(this, mostVisitedPlaces);
        mostVisitedPlacesRecyclerView.setAdapter(mostVisitedPlacesAdapter);
        mostVisitedPlacesRecyclerView.setOnItemAction(this);

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressOverlay.setAnimation(inAnimation);
        progressOverlay.setVisibility(View.VISIBLE);

        vm.getVisitedPlaces(2).observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                TravelActivity.this.mostVisitedPlaces = places;
                mostVisitedPlacesAdapter.setData(places);

                AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressOverlay.setAnimation(outAnimation);
                progressOverlay.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setupCategorizedVisitedPlacesView() {
        categorizedVisitedPlacesRecyclerView = findViewById(R.id.list_categorized_visited_places);
        categorizedVisitedPlacesRecyclerView.setHasFixedSize(true);

        categorizedVisitedPlacesLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categorizedVisitedPlacesRecyclerView.setLayoutManager(categorizedVisitedPlacesLayoutManager);

        categorizedVisitedPlacesAdapter = new TravelCategorizedVisitedPlacesAdapter(this, categorizedVisitedPlaces);
        categorizedVisitedPlacesRecyclerView.setAdapter(categorizedVisitedPlacesAdapter);
        categorizedVisitedPlacesRecyclerView.setOnItemAction(this);

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressOverlay.setAnimation(inAnimation);
        progressOverlay.setVisibility(View.VISIBLE);

        vm.getVisitedPlaces(5).observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                TravelActivity.this.categorizedVisitedPlaces = places;
                categorizedVisitedPlacesAdapter.setData(places);

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
        if (recentlyVisitedPlaces != null && position >= 0 && position <= recentlyVisitedPlaces.size() - 1) {
            Place place = recentlyVisitedPlaces.get(position);
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
