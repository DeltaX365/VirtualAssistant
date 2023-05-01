package vn.sttechnology.virtualassistant.ui.travel.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.ui.common.IRecyclerViewRow;
import vn.sttechnology.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

import java.util.Map;

public class PlaceDangerSimpleRow extends LinearLayout implements IRecyclerViewRow<Map<String, Object>> {
    private TextView mDangerAreaTextView;
    private TextView mUpdatedDateTextView;

    private Context mContext;
    private Map<String, Object> mDangerArea;
    private OnRecyclerViewRowTouchListener<Map<String, Object>> mOnRecyclerViewRowTouchListener;

    public PlaceDangerSimpleRow(Context context) {
        super(context);
        mContext = context;
    }

    public PlaceDangerSimpleRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PlaceDangerSimpleRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDangerAreaTextView = findViewById(R.id.danger_area);
        mUpdatedDateTextView = findViewById(R.id.updated_date);
    }

    @Override
    public void showData(Map<String, Object> dangerArea, String viewType) {
        mDangerArea = dangerArea;

        String placeName = (dangerArea != null && dangerArea.containsKey("address"))? (String) dangerArea.get("address") : "";
        mDangerAreaTextView.setText(placeName);

        String updatedDate = (dangerArea != null && dangerArea.containsKey("updated_date"))? (String) dangerArea.get("updated_date") : "";
        mUpdatedDateTextView.setText("Updated date: " + updatedDate);
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<Map<String, Object>> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }
}
