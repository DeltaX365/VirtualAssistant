package com.vnsolution.virtualassistant.ui.travel.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiSetting;
import com.vnsolution.virtualassistant.model.PlaceSummary;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.ui.common.IRecyclerViewRow;

public class PlaceCategoryCardRow extends MaterialCardView implements IRecyclerViewRow<PlaceSummary> {
    private ImageView mBackgroundImageView;
    private TextView mTypeTextView;
    private TextView mTotalTextView;

    private Context mContext;
    private OnRecyclerViewRowTouchListener<PlaceSummary> mOnRecyclerViewRowTouchListener;

    public PlaceCategoryCardRow(Context context) {
        super(context);
        mContext = context;
    }

    public PlaceCategoryCardRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PlaceCategoryCardRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mBackgroundImageView = findViewById(R.id.category_background);
        this.mTypeTextView = findViewById(R.id.category_type);
        this.mTotalTextView = findViewById(R.id.category_total);
    }

    @Override
    public void showData(PlaceSummary placeSummary, String viewType) {
        int total = (placeSummary != null) ? placeSummary.total : 0;
        String type = (placeSummary != null) ? placeSummary.type : "";
        switch (type) {
            case UiSetting.PLACE_SUMMARY_YESTERDAY:
                mTypeTextView.setText("Yesterday");
                mTotalTextView.setText(total + " " + ((total == 1)? "place" : "places"));
                break;
            case UiSetting.PLACE_SUMMARY_ALL:
                mTypeTextView.setText("Places");
                mTotalTextView.setText(total + " " + ((total == 1)? "place" : "places"));
                break;
            case UiSetting.PLACE_SUMMARY_CITY:
                mTypeTextView.setText("Cities");
                mTotalTextView.setText(total + " " + ((total == 1)? "city" : "cities"));
                break;
            case UiSetting.PLACE_SUMMARY_COUNTRY:
                mTypeTextView.setText("Countries");
                mTotalTextView.setText(total + " " + ((total == 1)? "country" : "countries"));
                break;
        }
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<PlaceSummary> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }
}
