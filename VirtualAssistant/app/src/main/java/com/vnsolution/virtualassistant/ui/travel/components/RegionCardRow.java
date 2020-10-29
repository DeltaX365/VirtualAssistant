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

public class RegionCardRow extends MaterialCardView implements RecyclerViewRow<String> {
    private ImageView mPhotoImageView;
    private TextView mNameTextView;
    private TextView mTotalVisitTextView;

    private Context mContext;
    private OnRecyclerViewRowTouchListener<String> mOnRecyclerViewRowTouchListener;

    public RegionCardRow(Context context) {
        super(context);
        mContext = context;
    }

    public RegionCardRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public RegionCardRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPhotoImageView = findViewById(R.id.background);
        mNameTextView = findViewById(R.id.title);
        mTotalVisitTextView = findViewById(R.id.subtitle);
    }

    @Override
    public void showData(String regionName) {
        mNameTextView.setText(regionName.trim());
        mTotalVisitTextView.setText("0 visits");
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<String> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }
}
