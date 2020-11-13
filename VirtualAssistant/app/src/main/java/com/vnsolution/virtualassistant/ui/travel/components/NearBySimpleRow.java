package com.vnsolution.virtualassistant.ui.travel.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.api.dto.GoogleMapNearByDto;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.ui.common.IRecyclerViewRow;
import com.vnsolution.virtualassistant.util.StringUtils;

import java.util.List;

public class NearBySimpleRow extends LinearLayout implements IRecyclerViewRow<GoogleMapNearByDto.NearBy> {
    private TextView mNameTextView;
    private TextView mAddressTextView;
    private TextView mCategoryTextView;

    private Context mContext;
    private GoogleMapNearByDto.NearBy mNearBy;
    private List<PlaceType> mPlaceTypes;
    private OnRecyclerViewRowTouchListener<GoogleMapNearByDto.NearBy> mOnRecyclerViewRowTouchListener;

    public NearBySimpleRow(Context context) {
        super(context);
        mContext = context;
    }

    public NearBySimpleRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public NearBySimpleRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNameTextView = findViewById(R.id.place_name);
        mAddressTextView = findViewById(R.id.place_address);
        mCategoryTextView = findViewById(R.id.place_type);
    }

    @Override
    public void showData(GoogleMapNearByDto.NearBy nearBy) {
        mNearBy = nearBy;

        // Place name
        String placeName = (nearBy.name == null || nearBy.name == "") ? "Unknown Address" : nearBy.name;
        mNameTextView.setText(placeName);

        // Place address
        double latitude = (nearBy.geometry != null && nearBy.geometry.location != null)? nearBy.geometry.location.lat : 0;
        double longitude = (nearBy.geometry != null && nearBy.geometry.location != null)? nearBy.geometry.location.lng : 0;
        latitude = Math.round(latitude * Math.pow(10, 7)) / Math.pow(10, 7);
        longitude = Math.round(longitude * Math.pow(10, 7)) / Math.pow(10, 7);
        String placeAddress = (nearBy.vicinity == null || nearBy.vicinity == "") ? (latitude + "," + longitude) : nearBy.vicinity;
        mAddressTextView.setText(placeAddress);

        // Place category
        PlaceType placeType = searchPlaceType(mPlaceTypes, StringUtils.join(", ", nearBy.types));
        if (placeType != null) {
            mCategoryTextView.setText(placeType.name);
            mCategoryTextView.setVisibility(View.VISIBLE);
        } else {
            mCategoryTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<GoogleMapNearByDto.NearBy> onRecyclerViewRowTouchListener) {
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
