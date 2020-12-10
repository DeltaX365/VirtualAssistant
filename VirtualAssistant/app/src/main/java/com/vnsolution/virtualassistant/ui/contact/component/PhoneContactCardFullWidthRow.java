package com.vnsolution.virtualassistant.ui.contact.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.UiOptions;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.common.IRecyclerViewRow;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.util.BitmapUtils;

public class PhoneContactCardFullWidthRow extends MaterialCardView implements IRecyclerViewRow<PhoneContact> {
    private ImageView mAvatarImageView;
    private TextView mContactNameTextView;
    private TextView mPhoneNumbersTextView;
    private ImageButton mCallActionButton;

    private Context mContext;
    private PhoneContact mPhoneContact;
    private OnRecyclerViewRowTouchListener<PhoneContact> mOnRecyclerViewRowTouchListener;

    public PhoneContactCardFullWidthRow(Context context) {
        super(context);
        mContext = context;
    }

    public PhoneContactCardFullWidthRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PhoneContactCardFullWidthRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAvatarImageView = findViewById(R.id.contact_avatar);
        mContactNameTextView = findViewById(R.id.contact_name);
        mPhoneNumbersTextView = findViewById(R.id.phone_number);
        mCallActionButton = findViewById(R.id.action_call);
        mCallActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRecyclerViewRowTouchListener != null) {
                    mOnRecyclerViewRowTouchListener.onAction(mPhoneContact, UiOptions.CONTACT_ACTION_CALL);
                }
            }
        });
    }

    @Override
    public void showData(PhoneContact phoneContact, String viewType) {
        mPhoneContact = phoneContact;

        Bitmap avatar = BitmapUtils.generateBitmapByText(mContext, phoneContact.name, 10, 60, 60);
        Glide.with(mContext).load(avatar).apply(RequestOptions.circleCropTransform()).circleCrop().into(mAvatarImageView);

        mContactNameTextView.setText(phoneContact.name);
        mPhoneNumbersTextView.setText(phoneContact.phoneNumbers);
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<PhoneContact> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }
}
