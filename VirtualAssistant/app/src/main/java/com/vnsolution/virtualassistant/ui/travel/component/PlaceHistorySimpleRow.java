package com.vnsolution.virtualassistant.ui.travel.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.PlaceHistory;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.ui.common.IRecyclerViewRow;

import java.text.SimpleDateFormat;

public class PlaceHistorySimpleRow extends LinearLayout implements IRecyclerViewRow<PlaceHistory> {
    private TextView mHistoryTextView;

    private Context mContext;
    private PlaceHistory mPlaceHistory;
    private OnRecyclerViewRowTouchListener<PlaceHistory> mOnRecyclerViewRowTouchListener;

    public PlaceHistorySimpleRow(Context context) {
        super(context);
        mContext = context;
    }

    public PlaceHistorySimpleRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PlaceHistorySimpleRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHistoryTextView = findViewById(R.id.history);
    }

    @Override
    public void showData(PlaceHistory placeHistory, String viewType) {
        mPlaceHistory = placeHistory;

        // Place history
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String history = formatter.format(placeHistory.enterTime) + " . " + formatter.format(placeHistory.existTime);
        mHistoryTextView.setText(history);
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<PlaceHistory> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }
}
