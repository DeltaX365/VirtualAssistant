package com.vnsolution.virtualassistant.ui.travel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceHistory;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;

import java.util.List;

import javax.inject.Inject;

public class TravelViewActivity extends BaseActivity {
    private ImageView mPhotoImageView;
    private TextView mNameTextView;
    private TextView mAddressTextView;
    private TextView mTypeTextView;
    private TextView mTotalVisitTextView;
    private RecyclerView mPlaceHistoryRecyclerView;
    private RecyclerViewAdapter<PlaceHistory> mPlaceHistoryAdapter;
    private RecyclerView.LayoutManager mPlaceHistoryLayoutManager;
    private FloatingActionButton mEditPlaceBtn;

    private TravelViewModel travelViewModel;
    private Place visitedPlace;
    private List<PlaceType> mPlaceTypes;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        travelViewModel = ViewModelProviders.of(this, viewModelFactory).get(TravelViewModel.class);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.screen_travel_view);
        setupActionBar();

        mPhotoImageView = findViewById(R.id.place_photo);
        mNameTextView = findViewById(R.id.place_name);
        mAddressTextView = findViewById(R.id.place_address);
        mTypeTextView = findViewById(R.id.place_type);
        mTotalVisitTextView = findViewById(R.id.place_total_visit);

        mPlaceHistoryRecyclerView = findViewById(R.id.place_history);
        mPlaceHistoryRecyclerView.setHasFixedSize(true);
        mPlaceHistoryLayoutManager = new LinearLayoutManager(this);
        mPlaceHistoryRecyclerView.setLayoutManager(mPlaceHistoryLayoutManager);
        mPlaceHistoryAdapter = new RecyclerViewAdapter<PlaceHistory>(this, UiOptions.PLACE_LIST_HISTORY, R.layout.item_place_history);
        mPlaceHistoryRecyclerView.setAdapter(mPlaceHistoryAdapter);

        mEditPlaceBtn = findViewById(R.id.edit_place);
        mEditPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TravelViewActivity.this, TravelEditActivity.class);
                intent.putExtra("PlaceId", visitedPlace.id);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String placeId = intent.getStringExtra("PlaceId");
            travelViewModel.getPlaceById(placeId).observe(this, new Observer<Place>() {
                @Override
                public void onChanged(Place place) {
                    visitedPlace = place;
                    presentVisitedPlace(visitedPlace);
                    mPlaceHistoryAdapter.setData(place.placeHistories);
                }
            });
        }
    }

    private void presentVisitedPlace(Place place) {
        if (place != null) {
            // Place photo
            if (place.photoReference != null && !place.photoReference.isEmpty()) {
                String photoUri = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=320&photoreference=" + place.photoReference + "&key=" + Constants.GM_API_KEY;
                Glide.with(this).load(photoUri).into(mPhotoImageView);
            }

            // Place name
            String placeName = (place.placeName == null || place.placeName == "") ? "Unknown" : place.placeName;
            mNameTextView.setText(placeName);

            // Place address
            String placeAddress = (place.formattedAddress == null || place.formattedAddress == "") ? (place.latitude + "," + place.longitude) : place.formattedAddress;
            mAddressTextView.setText(placeAddress);

            // Place category
            PlaceType placeType = searchPlaceType(mPlaceTypes, place.types);
            if (placeType != null) {
                mTypeTextView.setText(placeType.name);
                mTypeTextView.setVisibility(View.VISIBLE);
            } else {
                mTypeTextView.setVisibility(View.GONE);
            }

            // Visit total
            if (place.placeHistories != null && place.placeHistories.size() > 0) {
                String totalVisit = place.placeHistories.size() + " " + ((place.placeHistories.size() == 1)? "visit" : "visits");
                mTotalVisitTextView.setText("History . " + totalVisit);
                mTotalVisitTextView.setVisibility(View.VISIBLE);
            } else {
                mTotalVisitTextView.setVisibility(View.GONE);
            }
        }
    }

    private PlaceType searchPlaceType(List<PlaceType> placeTypes, String tags) {
        PlaceType placeType = null;
        if (placeTypes != null && tags != null) {
            for (int i = 0; i < placeTypes.size(); i++) {
                if (tags.contains(placeTypes.get(i).tag)) {
                    placeType = placeTypes.get(i);
                }
            }
        }
        return placeType;
    }
}