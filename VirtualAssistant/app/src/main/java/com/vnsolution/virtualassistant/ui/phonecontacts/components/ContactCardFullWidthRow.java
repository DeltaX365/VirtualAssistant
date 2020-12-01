package com.vnsolution.virtualassistant.ui.phonecontacts.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.common.IRecyclerViewRow;
import com.vnsolution.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

import java.util.Random;

public class ContactCardFullWidthRow extends MaterialCardView implements IRecyclerViewRow<PhoneContact> {
    private ImageView mAvatarImageView;
    private TextView mContactNameTextView;
    private TextView mPhoneNumbersTextView;
    private TextView mTotalIncomeCallTextView;
    private TextView mTotalOutcomeCallTextView;
    private TextView mTotalSmsTextView;

    private Context mContext;
    private PhoneContact mPhoneContact;
    private OnRecyclerViewRowTouchListener<PhoneContact> mOnRecyclerViewRowTouchListener;

    public ContactCardFullWidthRow(Context context) {
        super(context);
        mContext = context;
    }

    public ContactCardFullWidthRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ContactCardFullWidthRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAvatarImageView = findViewById(R.id.contact_avatar);
        mContactNameTextView = findViewById(R.id.contact_name);
        mPhoneNumbersTextView = findViewById(R.id.contact_phone_number);
        mTotalIncomeCallTextView = findViewById(R.id.total_call_income);
        mTotalOutcomeCallTextView = findViewById(R.id.total_call_outcome);
        mTotalSmsTextView = findViewById(R.id.total_sms);
    }

    @Override
    public void showData(PhoneContact phoneContact, String viewType) {
        mPhoneContact = phoneContact;

        String avatarText = (phoneContact.name != null && phoneContact.name.length() > 1) ? phoneContact.name.substring(0, 2) : "";
        Bitmap avatar = generateBitmapByText(avatarText.toUpperCase(), 30, 60, 60);
        Glide.with(mContext).load(avatar).apply(RequestOptions.circleCropTransform()).circleCrop().into(mAvatarImageView);

        mContactNameTextView.setText(phoneContact.name);
        mPhoneNumbersTextView.setText(phoneContact.phoneNumbers);
        mTotalIncomeCallTextView.setText("INCOMING: " + phoneContact.totalIncomeCall);
        mTotalOutcomeCallTextView.setText("OUTGOING: " + phoneContact.totalOutcomeCall);
        mTotalSmsTextView.setText("SMS: " + phoneContact.totalSms);
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<PhoneContact> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }

    private Bitmap generateBitmapByText(String text, float textSize, int bitmapWidth, int bitmapHeight) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth , bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        Random random = new Random();
        int randomColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        paint.setColor(randomColor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPaint(paint);

        paint.setTextSize(textSize);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float baseline = -paint.ascent(); // ascent() is negative
        float measureWidth = paint.measureText(text); // round
//        float measureHeight = baseline + paint.descent();
        canvas.drawText(text,(bitmapWidth - measureWidth)/2, (bitmapHeight - (paint.descent() + paint.ascent()))/2, paint);

        return bitmap;
    }
}
