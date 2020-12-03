package com.vnsolution.virtualassistant.ui.phonecontacts.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.common.IRecyclerViewRow;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.util.BitmapUtils;

import java.util.Random;

public class ContactCardRow extends MaterialCardView implements IRecyclerViewRow<PhoneContact> {
    private ImageView mAvatarImageView;
    private TextView mContactNameTextView;
    private TextView mPhoneNumbersTextView;

    private Context mContext;
    private PhoneContact mPhoneContact;
    private OnRecyclerViewRowTouchListener<PhoneContact> mOnRecyclerViewRowTouchListener;

    public ContactCardRow(Context context) {
        super(context);
        mContext = context;
    }

    public ContactCardRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ContactCardRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAvatarImageView = findViewById(R.id.contact_avatar);
        mContactNameTextView = findViewById(R.id.contact_name);
        mPhoneNumbersTextView = findViewById(R.id.contact_phone_number);
    }

    @Override
    public void showData(PhoneContact phoneContact, String viewType) {
        mPhoneContact = phoneContact;

        Bitmap avatar = BitmapUtils.generateBitmapByText(mContext, phoneContact.name, 12, 60, 60);
        Glide.with(mContext).load(avatar).apply(RequestOptions.circleCropTransform()).circleCrop().into(mAvatarImageView);

        mContactNameTextView.setText(phoneContact.name);
        mPhoneNumbersTextView.setText(phoneContact.phoneNumbers);
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<PhoneContact> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }
}
