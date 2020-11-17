package com.vnsolution.virtualassistant.ui.phoneapps.components;

import android.content.Context;
import android.icu.text.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.ui.common.IRecyclerViewRow;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

public class AppCardRow extends MaterialCardView implements IRecyclerViewRow<PhoneApp> {
    private ImageView mAppIconImageView;
    private TextView mAppNameTextView;
    private TextView mAppVersionTextView;
    private TextView mAppUsageTextView;

    private Context mContext;
    private OnRecyclerViewRowTouchListener<PhoneApp> mOnRecyclerViewRowTouchListener;

    public AppCardRow(Context context) {
        super(context);
        mContext = context;
    }

    public AppCardRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AppCardRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAppIconImageView = findViewById(R.id.app_icon);
        mAppNameTextView = findViewById(R.id.app_name);
        mAppVersionTextView = findViewById(R.id.app_version);
        mAppUsageTextView = findViewById(R.id.app_usage);
    }

    @Override
    public void showData(PhoneApp phoneApp, String viewType) {
        Glide.with(mContext).load(phoneApp.appIcon).into(mAppIconImageView);

        mAppNameTextView.setText(phoneApp.appName);
        mAppVersionTextView.setText("v" + phoneApp.versionName);

        String appLastUsedTime = "";
        String appUsageTime = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && phoneApp.usageStats != null) {
            appLastUsedTime += DateUtils.formatSameDayTime(phoneApp.usageStats.getLastTimeUsed(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM).toString();
            appUsageTime += DateUtils.formatElapsedTime(phoneApp.usageStats.getTotalTimeInForeground() / 1000);
        }
        String appUsage = (viewType.equals(UiOptions.APP_LIST_RECENTLY_USAGE))? appLastUsedTime :
                (viewType.equals(UiOptions.APP_LIST_MOST_USAGE))? appUsageTime : "";
        mAppUsageTextView.setText(appUsage);
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<PhoneApp> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }
}
