package com.vnsolution.virtualassistant.ui.travel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceSummary;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.service.stop_tracking.StopTrackingManager;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TravelDashboardActivity extends BaseActivity {
    private RecyclerView recentlyVisitedPlacesRecyclerView;
    private RecyclerViewAdapter<Place> recentlyVisitedPlacesAdapter;
    private RecyclerView.LayoutManager recentlyVisitedPlacesLayoutManager;
    private List<Place> recentlyVisitedPlaces = new ArrayList<>();

    private RecyclerView mostVisitedPlacesRecyclerView;
    private RecyclerViewAdapter<Place> mostVisitedPlacesAdapter;
    private RecyclerView.LayoutManager mostVisitedPlacesLayoutManager;
    private List<Place> mostVisitedPlaces = new ArrayList<>();

    private RecyclerView categorizedVisitedPlacesRecyclerView;
    private RecyclerViewAdapter<PlaceSummary> categorizedVisitedPlacesAdapter;
    private RecyclerView.LayoutManager categorizedVisitedPlacesLayoutManager;
    private List<PlaceSummary> placeSummaryList;

    private FrameLayout progressOverlay;
    private TravelViewModel vm;
    private List<PlaceType> placeTypes = new ArrayList<>();

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Inject
    public StopTrackingManager stopTrackingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = ViewModelProviders.of(this, viewModelFactory).get(TravelViewModel.class);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.screen_travel_dashboard);
        setupActionBar();

        progressOverlay = findViewById(R.id.progress_overlay);
        placeTypes = stopTrackingManager.getPlaceTypes();

        setupRecentlyVisitedPlacesView();
        setupMostVisitedPlacesView();
        setupCategorizedVisitedPlacesView();
    }

    private void setupRecentlyVisitedPlacesView() {
        recentlyVisitedPlacesRecyclerView = findViewById(R.id.list_recently_visited_places);
        recentlyVisitedPlacesRecyclerView.setHasFixedSize(true);

        recentlyVisitedPlacesLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentlyVisitedPlacesRecyclerView.setLayoutManager(recentlyVisitedPlacesLayoutManager);

        recentlyVisitedPlacesAdapter = new RecyclerViewAdapter<Place>(this, UiOptions.PLACE_LIST_RECENTLY_VISITED, R.layout.card_place);
        recentlyVisitedPlacesAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<Place>() {
            @Override
            public void onTap(Place place) {
                super.onTap(place);
                Intent intent = new Intent(TravelDashboardActivity.this, TravelViewActivity.class);
                intent.putExtra("PlaceId", place.id);
                startActivity(intent);
            }
        });
        recentlyVisitedPlacesRecyclerView.setAdapter(recentlyVisitedPlacesAdapter);

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressOverlay.setAnimation(inAnimation);
        progressOverlay.setVisibility(View.VISIBLE);

        vm.getRecentlyVisitedPlaces().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                TravelDashboardActivity.this.recentlyVisitedPlaces = places;
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

        mostVisitedPlacesAdapter = new RecyclerViewAdapter<Place>(this, UiOptions.PLACE_LIST_MOST_VISITED, R.layout.card_place);
        mostVisitedPlacesAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<Place>() {
            @Override
            public void onTap(Place place) {
                super.onTap(place);
                Intent intent = new Intent(TravelDashboardActivity.this, TravelViewActivity.class);
                intent.putExtra("PlaceId", place.id);
                startActivity(intent);
            }
        });
        mostVisitedPlacesRecyclerView.setAdapter(mostVisitedPlacesAdapter);

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressOverlay.setAnimation(inAnimation);
        progressOverlay.setVisibility(View.VISIBLE);

        vm.getMostVisitedPlaces().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                TravelDashboardActivity.this.mostVisitedPlaces = places;
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

        categorizedVisitedPlacesAdapter = new RecyclerViewAdapter<PlaceSummary>(this, UiOptions.PLACE_LIST_SUMMARY, R.layout.card_place_category);
        categorizedVisitedPlacesAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PlaceSummary>() {
            @Override
            public void onTap(PlaceSummary item) {
                super.onTap(item);
                Intent intent = new Intent(TravelDashboardActivity.this, TravelHistoryActivity.class);
                intent.putExtra("UiOption", item.type);
                startActivity(intent);
            }
        });
        categorizedVisitedPlacesRecyclerView.setAdapter(categorizedVisitedPlacesAdapter);

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressOverlay.setAnimation(inAnimation);
        progressOverlay.setVisibility(View.VISIBLE);

        vm.getPlaceSummary().observe(this, new Observer<List<PlaceSummary>>() {
            @Override
            public void onChanged(@Nullable List<PlaceSummary> placeSummaryList) {
                TravelDashboardActivity.this.placeSummaryList = placeSummaryList;
                categorizedVisitedPlacesAdapter.setData(placeSummaryList);

                AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressOverlay.setAnimation(outAnimation);
                progressOverlay.setVisibility(View.INVISIBLE);
            }
        });
    }
}
