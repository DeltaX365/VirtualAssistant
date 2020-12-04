package com.vnsolution.virtualassistant.ui.phoneapp.component;

import android.content.Context;
import android.icu.text.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.ui.common.IRecyclerViewRow;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

public class AppCardFullWidthRow extends MaterialCardView implements IRecyclerViewRow<PhoneApp>, PopupMenu.OnMenuItemClickListener {
    private ImageView mAppIconImageView;
    private TextView mAppNameTextView;
    private TextView mAppPackageNameTextView;
    private TextView mAppVersionTextView;
    private TextView mAppLastUsageTextView;
    private TextView mAppTotalUsageTextView;
    private TextView mAppOptionsTextView;

    private Context mContext;
    private PhoneApp mPhoneApp;
    private OnRecyclerViewRowTouchListener<PhoneApp> mOnRecyclerViewRowTouchListener;

    public AppCardFullWidthRow(Context context) {
        super(context);
        mContext = context;
    }

    public AppCardFullWidthRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AppCardFullWidthRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAppIconImageView = findViewById(R.id.app_icon);
        mAppNameTextView = findViewById(R.id.app_name);
        mAppPackageNameTextView = findViewById(R.id.app_package_name);
        mAppVersionTextView = findViewById(R.id.app_version);
        mAppLastUsageTextView = findViewById(R.id.app_last_usage);
        mAppTotalUsageTextView = findViewById(R.id.app_total_usage);
        mAppOptionsTextView = findViewById(R.id.app_options);

        mAppOptionsTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.setOnMenuItemClickListener(AppCardFullWidthRow.this);
                popupMenu.inflate(R.menu.menu_app_options);
                popupMenu.show();
            }
        });
    }

    @Override
    public void showData(PhoneApp phoneApp, String viewType) {
        mPhoneApp = phoneApp;
        Glide.with(mContext).load(phoneApp.appIcon).into(mAppIconImageView);

        mAppNameTextView.setText(phoneApp.appName);
        mAppPackageNameTextView.setText(phoneApp.packageName);
        mAppVersionTextView.setText("Ver: " + phoneApp.versionName);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && phoneApp.usageStats != null) {
            String appLastUsageTime = DateUtils.formatSameDayTime(phoneApp.usageStats.getLastTimeUsed(), System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM).toString();
            String appTotalUsageTime = DateUtils.formatElapsedTime(phoneApp.usageStats.getTotalTimeInForeground() / 1000);
            mAppLastUsageTextView.setText("Last usage: " + appLastUsageTime);
            mAppTotalUsageTextView.setText("Total usage: " + appTotalUsageTime);
        }
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<PhoneApp> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String action = "";
        switch (item.getItemId()) {
            case R.id.menu_item_launch:
                action = UiOptions.APP_ACTION_LAUNCH;
                break;
            case R.id.menu_item_lock:
                action = UiOptions.APP_ACTION_LOCK;
                break;
            case R.id.menu_item_uninstall:
                action = UiOptions.APP_ACTION_UNINSTALL;
                break;
            default:
                return false;
        }
        if (mPhoneApp != null && action != null && action != "" && mOnRecyclerViewRowTouchListener != null) {
            mOnRecyclerViewRowTouchListener.onAction(mPhoneApp, action);
        }
        return true;
    }
}
