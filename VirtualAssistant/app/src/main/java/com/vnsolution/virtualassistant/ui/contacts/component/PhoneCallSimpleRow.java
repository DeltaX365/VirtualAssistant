package com.vnsolution.virtualassistant.ui.contacts.component;

import android.content.Context;
import android.provider.CallLog;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.PhoneCall;
import com.vnsolution.virtualassistant.ui.common.IRecyclerViewRow;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PhoneCallSimpleRow extends LinearLayout implements IRecyclerViewRow<PhoneCall> {
    public ImageView mIconImageView;
    public TextView mContactNameTextView;
    public TextView mPhoneNumberTextView;
    public TextView mDateTextView;
    public TextView mTimeTextView;
    public TextView mDurationTextView;

    private Context mContext;
    private PhoneCall mPhoneCall;
    private OnRecyclerViewRowTouchListener<PhoneCall> mOnRecyclerViewRowTouchListener;

    public PhoneCallSimpleRow(Context context) {
        super(context);
        mContext = context;
    }

    public PhoneCallSimpleRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PhoneCallSimpleRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIconImageView = findViewById(R.id.call_icon);
        mContactNameTextView = findViewById(R.id.contact_name);
        mPhoneNumberTextView = findViewById(R.id.phone_number);
        mDateTextView = findViewById(R.id.contact_date);
        mTimeTextView = findViewById(R.id.contact_time);
        mDurationTextView = findViewById(R.id.contact_duration);
    }

    @Override
    public void showData(PhoneCall phoneCall, String viewType) {
        mPhoneCall = phoneCall;

        switch (phoneCall.type) {
            case android.provider.CallLog.Calls.INCOMING_TYPE:
                mIconImageView.setImageResource(R.drawable.ic_call_received_black_24dp);
                break;
            case android.provider.CallLog.Calls.OUTGOING_TYPE:
                mIconImageView.setImageResource(R.drawable.ic_call_made_black_24dp);
                break;
            case android.provider.CallLog.Calls.MISSED_TYPE:
                mIconImageView.setImageResource(R.drawable.ic_call_missed_black_24dp);
                break;
            case CallLog.Calls.BLOCKED_TYPE:
                mIconImageView.setImageResource(R.drawable.ic_baseline_phone_locked_24);
                break;
        }

        String contactName = (phoneCall.contactRef != null) ? phoneCall.contactRef.name : "Unknown";
        mContactNameTextView.setText(contactName);

        mPhoneNumberTextView.setText(phoneCall.phoneNumber);

        Date phoneCallDate = new Date(phoneCall.date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
        mDateTextView.setText(dateFormat.format(phoneCallDate));

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        mTimeTextView.setText(timeFormat.format(phoneCallDate));

        mDurationTextView.setText(DateUtils.formatElapsedTime(phoneCall.duration));
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<PhoneCall> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }
}
