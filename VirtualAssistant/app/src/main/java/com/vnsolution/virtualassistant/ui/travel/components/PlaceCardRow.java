package com.vnsolution.virtualassistant.ui.travel.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.db.convertor.DateConverter;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRow;

import java.util.List;

public class PlaceCardRow extends MaterialCardView implements RecyclerViewRow<Place> {
    private ImageView mPhotoImageView;
    private TextView mNameTextView;
    private TextView mAddressTextView;
    private TextView mTypeTextView;
    private TextView mTotalVisitTextView;
    private TextView mEnterDateTextView;

    private Context mContext;
    private List<PlaceType> mPlaceTypes;
    private OnRecyclerViewRowTouchListener<Place> mOnRecyclerViewRowTouchListener;

    public PlaceCardRow(Context context) {
        super(context);
        mContext = context;
    }

    public PlaceCardRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PlaceCardRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPhotoImageView = findViewById(R.id.place_photo);
        mNameTextView = findViewById(R.id.place_name);
        mAddressTextView = findViewById(R.id.place_address);
        mTypeTextView = findViewById(R.id.place_type);
        mTotalVisitTextView = findViewById(R.id.place_total_visit);
        mEnterDateTextView = findViewById(R.id.place_enter_date);
    }

    @Override
    public void showData(Place place) {
        // Place photo
        if (place.photoReference != null && !place.photoReference.isEmpty()) {
            String photoUri = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=320&photoreference=" + place.photoReference + "&key=" + Constants.GM_API_KEY;
            Glide.with(mContext).load(photoUri).into(mPhotoImageView);
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

        // Visit history
        if (place.placeHistories != null && place.placeHistories.size() > 0) {
            String totalVisit = place.placeHistories.size() + " " + ((place.placeHistories.size() == 1)? "visit" : "visits");
            mTotalVisitTextView.setText(totalVisit);
            mTotalVisitTextView.setVisibility(View.VISIBLE);

            int latestIndex = place.placeHistories.size() - 1;
            String latestEnterTime = DateConverter.toString(place.placeHistories.get(latestIndex).enterTime);
            mEnterDateTextView.setText(latestEnterTime);
            mEnterDateTextView.setVisibility(View.VISIBLE);
        } else {
            mTotalVisitTextView.setVisibility(View.GONE);
            mEnterDateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<Place> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
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
