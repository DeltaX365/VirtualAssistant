package com.vnsolution.virtualassistant.ui.travel.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.AppSetting;
import com.vnsolution.virtualassistant.common.UiSetting;
import com.vnsolution.virtualassistant.db.convertor.DateConverter;
import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceType;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.ui.common.IRecyclerViewRow;

import java.util.List;

public class PlaceSimpleRow extends LinearLayout implements IRecyclerViewRow<Place> {
    private ImageView mPhotoImageView;
    private TextView mConfirmTextView;
    private TextView mNameTextView;
    private TextView mAddressTextView;
    private TextView mCategoryTextView;
    private TextView mTotalVisitTextView;
    private TextView mEnterDateTextView;
    private ViewGroup mConfirmViewGroup;
    private Button mConfirmButton;
    private Button mEditButton;
    private ImageButton mFavouriteButton;

    private Context mContext;
    private Place mPlace;
    private List<PlaceType> mPlaceTypes;
    private String mViewType;
    private OnRecyclerViewRowTouchListener<Place> mOnRecyclerViewRowTouchListener;

    public PlaceSimpleRow(Context context) {
        super(context);
        mContext = context;
    }

    public PlaceSimpleRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PlaceSimpleRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mConfirmTextView = findViewById(R.id.place_confirm);
        mPhotoImageView = findViewById(R.id.place_photo);
        mConfirmTextView = findViewById(R.id.place_confirm);
        mNameTextView = findViewById(R.id.place_name);
        mAddressTextView = findViewById(R.id.place_address);
        mCategoryTextView = findViewById(R.id.place_type);
        mTotalVisitTextView = findViewById(R.id.place_total_visit);
        mEnterDateTextView = findViewById(R.id.place_enter_date);
        mConfirmViewGroup = findViewById(R.id.place_action_group);
        mConfirmButton = findViewById(R.id.confirm_place);
        mConfirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRecyclerViewRowTouchListener != null) {
                    mOnRecyclerViewRowTouchListener.onAction(mPlace, UiSetting.PLACE_ACTION_CONFIRM);
                }
            }
        });
        mEditButton = findViewById(R.id.edit_place);
        mEditButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRecyclerViewRowTouchListener != null) {
                    mOnRecyclerViewRowTouchListener.onAction(mPlace, UiSetting.PLACE_ACTION_EDIT);
                }
            }
        });
        mFavouriteButton = findViewById(R.id.favourite_place);
    }

    @Override
    public void showData(Place place, String viewType) {
        mPlace = place;
        mViewType = viewType;

        // Place photo
        if (place.photoReference != null && !place.photoReference.isEmpty()) {
            String photoUri = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=320&photoreference=" + place.photoReference + "&key=" + AppSetting.GM_API_KEY;
            Glide.with(mContext).load(photoUri).into(mPhotoImageView);
        }

        // Place name
        String placeName = (place.placeName == null || place.placeName == "") ? "Unknown Address" : place.placeName;
        mNameTextView.setText(placeName);

        // Place address
        String placeAddress = (place.formattedAddress == null || place.formattedAddress == "") ? (place.latitude + "," + place.longitude) : place.formattedAddress;
        mAddressTextView.setText(placeAddress);

        // Place category
        PlaceType placeType = searchPlaceType(mPlaceTypes, place.types);
        if (placeType != null) {
            mCategoryTextView.setText(placeType.name);
            mCategoryTextView.setVisibility(View.VISIBLE);
        } else {
            mCategoryTextView.setVisibility(View.GONE);
        }

        // Place history
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

        // Place confirm
        if (mViewType != null && mViewType != "" && mViewType.equals(UiSetting.PLACE_SUMMARY_YESTERDAY)) {
            int visibility = (place.confirmed == 1) ? View.GONE : View.VISIBLE;
            mConfirmTextView.setVisibility(visibility);
            mConfirmViewGroup.setVisibility(visibility);
            mTotalVisitTextView.setVisibility(View.GONE);
        } else {
            mConfirmTextView.setVisibility(View.GONE);
            mConfirmViewGroup.setVisibility(View.GONE);
            mTotalVisitTextView.setVisibility(View.VISIBLE);
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
