package com.vnsolution.virtualassistant.ui.travel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.vnsolution.virtualassistant.BuildConfig;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.db.convertor.DateConverter;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceSummary;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.model.StopPoint;
import com.vnsolution.virtualassistant.service.place.PlaceManager;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TravelDashboardActivity extends BaseActivity {
    private ViewGroup visitingPlaceViewGroup;
    private TextView visitingPlaceTextView;

    private RecyclerView recentVisitedPlacesRecyclerView;
    private RecyclerViewAdapter<Place> recentVisitedPlacesAdapter;
    private RecyclerView.LayoutManager recentVisitedPlacesLayoutManager;
    private TextView noRecentVisitedPlaceTextView;
    private List<Place> recentVisitedPlaces = new ArrayList<>();

    private RecyclerView mostVisitedPlacesRecyclerView;
    private RecyclerViewAdapter<Place> mostVisitedPlacesAdapter;
    private RecyclerView.LayoutManager mostVisitedPlacesLayoutManager;
    private TextView noMostVisitedPlaceTextView;
    private List<Place> mostVisitedPlaces = new ArrayList<>();

    private RecyclerView categorizedVisitedPlacesRecyclerView;
    private RecyclerViewAdapter<PlaceSummary> categorizedVisitedPlacesAdapter;
    private RecyclerView.LayoutManager categorizedVisitedPlacesLayoutManager;
    private List<PlaceSummary> placeSummaryList;

    private List<PlaceType> placeTypes = new ArrayList<>();

    @Inject
    public TravelViewModel vm;

    @Inject
    public PlaceManager placeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        refreshView();
    }

    private void setupView() {
        setContentView(R.layout.screen_travel_dashboard);
        setupActionBar();

        setupVisitingPlaceView();
        setupRecentlyVisitedPlacesView();
        setupMostVisitedPlacesView();
        setupCategorizedVisitedPlacesView();
    }

    private void refreshView() {
        placeTypes = placeManager.getPlaceTypes();

        refreshVisitingPlaceView();
        refreshRecentVisitedPlacesView();
        refreshMostVisitedPlacesView();
        refreshCategorizedVisitedPlacesView();
    }

    private void setupVisitingPlaceView() {
        visitingPlaceViewGroup = findViewById(R.id.visiting_place_group);
        visitingPlaceTextView = findViewById(R.id.visiting_place);
    }

    private void refreshVisitingPlaceView() {
        StopPoint stopPoint = getStopPointFromCache(); //this should be called via StopTrackingManager - to be changed
        if (stopPoint != null && stopPoint.placeId != null && stopPoint.placeId != "") {
            vm.getPlaceById(stopPoint.placeId).observe(this, new Observer<Place>() {
                @Override
                public void onChanged(Place place) {
                    String detail = "";
                    if (place != null) {
                        if (place.placeName != null && place.placeName != "" &&
                                place.formattedAddress != null && place.formattedAddress != "") {
                            detail += place.placeName + "\n" + place.formattedAddress;
                        } else if (place.latitude > 0 && place.longitude > 0) {
                            detail += "Unknown" + " (" + place.latitude + ", " + place.longitude + ")";
                        }
                        if (detail != "") {
                            detail += "\n\n Since " + DateConverter.toString(stopPoint.enter);
                            visitingPlaceTextView.setText(detail);
                        }
                    }
                    int visibility = (detail != null && detail != "") ? View.VISIBLE : View.GONE;
                    visitingPlaceViewGroup.setVisibility(visibility);
                }
            });
        } else {
            visitingPlaceViewGroup.setVisibility(View.GONE);
        }
    }

    private StopPoint getStopPointFromCache() {
        SharedPreferences sharedPref = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        String stopPointString = sharedPref.getString("StopTrackingService.StopPoint", "");
        Gson gson = new Gson();
        return !stopPointString.isEmpty()? gson.fromJson(stopPointString, StopPoint.class) : null;
    }

    private void setupRecentlyVisitedPlacesView() {
        recentVisitedPlacesRecyclerView = findViewById(R.id.list_recent_visited_places);
        recentVisitedPlacesRecyclerView.setHasFixedSize(true);

        recentVisitedPlacesLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentVisitedPlacesRecyclerView.setLayoutManager(recentVisitedPlacesLayoutManager);

        recentVisitedPlacesAdapter = new RecyclerViewAdapter<Place>(this, UiOptions.PLACE_LIST_RECENT_VISITED, R.layout.card_place);
        recentVisitedPlacesAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<Place>() {
            @Override
            public void onTap(Place place) {
                super.onTap(place);
                Intent intent = new Intent(TravelDashboardActivity.this, TravelViewActivity.class);
                intent.putExtra("PlaceId", place.id);
                startActivity(intent);
            }
        });
        recentVisitedPlacesRecyclerView.setAdapter(recentVisitedPlacesAdapter);
        noRecentVisitedPlaceTextView = findViewById(R.id.no_recent_visited_place);
    }

    private void refreshRecentVisitedPlacesView() {
        vm.getRecentVisitedPlaces().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                TravelDashboardActivity.this.recentVisitedPlaces = places;
                recentVisitedPlacesAdapter.setData(places);
                recentVisitedPlacesRecyclerView.setVisibility(places.size() > 0? View.VISIBLE : View.GONE);
                noRecentVisitedPlaceTextView.setVisibility(places.size() > 0? View.GONE : View.VISIBLE);
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
        noMostVisitedPlaceTextView = findViewById(R.id.no_most_visited_place);
    }

    private void refreshMostVisitedPlacesView() {
        vm.getMostVisitedPlaces().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                TravelDashboardActivity.this.mostVisitedPlaces = places;
                mostVisitedPlacesAdapter.setData(places);
                mostVisitedPlacesRecyclerView.setVisibility(places.size() > 0? View.VISIBLE : View.GONE);
                noMostVisitedPlaceTextView.setVisibility(places.size() > 0? View.GONE : View.VISIBLE);
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
    }

    private void refreshCategorizedVisitedPlacesView() {
        vm.getPlaceSummary().observe(this, new Observer<List<PlaceSummary>>() {
            @Override
            public void onChanged(@Nullable List<PlaceSummary> placeSummaryList) {
                TravelDashboardActivity.this.placeSummaryList = placeSummaryList;
                categorizedVisitedPlacesAdapter.setData(placeSummaryList);
            }
        });
    }
}
