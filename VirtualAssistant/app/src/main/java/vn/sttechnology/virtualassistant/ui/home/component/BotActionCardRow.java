package vn.sttechnology.virtualassistant.ui.home.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.common.BotSetting;
import vn.sttechnology.virtualassistant.ui.common.IRecyclerViewRow;
import vn.sttechnology.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

public class BotActionCardRow extends MaterialCardView implements IRecyclerViewRow<String> {
    private ImageView mBotAvatarImageView;
    private TextView mBotActionTextView;

    private Context mContext;
    private OnRecyclerViewRowTouchListener<String> mOnRecyclerViewRowTouchListener;

    public BotActionCardRow(Context context) {
        super(context);
        mContext = context;
    }

    public BotActionCardRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public BotActionCardRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBotAvatarImageView = findViewById(R.id.bot_avatar);
        mBotActionTextView = findViewById(R.id.bot_action);
        mBotActionTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRecyclerViewRowTouchListener != null) {
                    mOnRecyclerViewRowTouchListener.onTap(mBotActionTextView.getText().toString());
                }
            }
        });
    }

    @Override
    public void showData(String action, String viewType) {
        switch (action) {
            case BotSetting.SHOW_TRAVEL_HISTORY:
                mBotAvatarImageView.setImageResource(R.drawable.ic_baseline_airplanemode_active_24);
                break;
            case BotSetting.COVID_TRACKING:
                mBotAvatarImageView.setImageResource(R.drawable.ic_baseline_coronavirus_24);
                break;
            case BotSetting.MANAGE_APPS:
                mBotAvatarImageView.setImageResource(R.drawable.ic_baseline_apps_24);
                break;
            case BotSetting.SHOW_APP_USAGE:
                mBotAvatarImageView.setImageResource(R.drawable.ic_baseline_leaderboard_24);
                break;
            case BotSetting.MANAGE_CONTACTS:
                mBotAvatarImageView.setImageResource(R.drawable.ic_baseline_people_24);
                break;
            case BotSetting.SHOW_LOCKED_CONTACTS:
                mBotAvatarImageView.setImageResource(R.drawable.ic_baseline_phone_locked_24);
                break;
            case BotSetting.SHOW_CALL_STAT:
                mBotAvatarImageView.setImageResource(R.drawable.ic_baseline_query_stats_24);
                break;
            case BotSetting.SHOW_CALL_LOGS:
                mBotAvatarImageView.setImageResource(R.drawable.ic_baseline_format_list_bulleted_24);
                break;
            case BotSetting.SHOW_SMS:
                mBotAvatarImageView.setImageResource(R.drawable.ic_baseline_sms_24);
                break;
            case BotSetting.SHOW_REMINDERS:
                mBotAvatarImageView.setImageResource(R.drawable.ic_baseline_alarm_24);
                break;
            case BotSetting.MANAGE_PERMISSIONS:
                mBotAvatarImageView.setImageResource(R.drawable.ic_baseline_vpn_key_24);
                break;
            case BotSetting.SHOW_SETTINGS:
                mBotAvatarImageView.setImageResource(R.drawable.ic_baseline_settings_24);
                break;
        }
        mBotActionTextView.setText(action);
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<String> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }
}
