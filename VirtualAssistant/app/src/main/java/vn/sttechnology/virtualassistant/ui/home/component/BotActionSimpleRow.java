package vn.sttechnology.virtualassistant.ui.home.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.chip.Chip;
import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;
import vn.sttechnology.virtualassistant.ui.common.IRecyclerViewRow;

public class BotActionSimpleRow extends LinearLayout implements IRecyclerViewRow<String> {
    private Chip mBotActionChip;

    private Context mContext;
    private OnRecyclerViewRowTouchListener<String> mOnRecyclerViewRowTouchListener;

    public BotActionSimpleRow(Context context) {
        super(context);
        mContext = context;
    }

    public BotActionSimpleRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public BotActionSimpleRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBotActionChip = findViewById(R.id.bot_action);
        mBotActionChip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRecyclerViewRowTouchListener != null) {
                    mOnRecyclerViewRowTouchListener.onTap(mBotActionChip.getText().toString());
                }
            }
        });
    }

    @Override
    public void showData(String action, String viewType) {
        mBotActionChip.setText(action);
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<String> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }
}
