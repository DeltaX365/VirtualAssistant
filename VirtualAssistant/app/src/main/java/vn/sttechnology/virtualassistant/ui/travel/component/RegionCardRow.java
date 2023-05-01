package vn.sttechnology.virtualassistant.ui.travel.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;
import vn.sttechnology.virtualassistant.ui.common.IRecyclerViewRow;

public class RegionCardRow extends MaterialCardView implements IRecyclerViewRow<String> {
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
    public void showData(String regionName, String viewType) {
        mNameTextView.setText(regionName.trim());
        mTotalVisitTextView.setText("0 visits");
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<String> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }
}
