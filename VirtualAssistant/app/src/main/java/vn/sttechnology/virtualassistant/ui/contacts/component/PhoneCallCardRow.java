package vn.sttechnology.virtualassistant.ui.contacts.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.model.PhoneCall;
import vn.sttechnology.virtualassistant.ui.common.IRecyclerViewRow;
import vn.sttechnology.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;
import vn.sttechnology.virtualassistant.util.BitmapUtils;

public class PhoneCallCardRow extends MaterialCardView implements IRecyclerViewRow<PhoneCall> {
    private ImageView mAvatarImageView;
    private TextView mContactNameTextView;
    private TextView mPhoneNumberTextView;

    private Context mContext;
    private PhoneCall mPhoneCall;
    private OnRecyclerViewRowTouchListener<PhoneCall> mOnRecyclerViewRowTouchListener;

    public PhoneCallCardRow(Context context) {
        super(context);
        mContext = context;
    }

    public PhoneCallCardRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PhoneCallCardRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAvatarImageView = findViewById(R.id.contact_avatar);
        mContactNameTextView = findViewById(R.id.contact_name);
        mPhoneNumberTextView = findViewById(R.id.phone_number);
    }

    @Override
    public void showData(PhoneCall phoneCall, String viewType) {
        mPhoneCall = phoneCall;
        String contactName = (phoneCall != null && phoneCall.contactRef != null) ? phoneCall.contactRef.name : "";

        Bitmap avatar = BitmapUtils.generateBitmapByText(mContext, contactName, 10, 60, 60);
        Glide.with(mContext).load(avatar).apply(RequestOptions.circleCropTransform()).circleCrop().into(mAvatarImageView);

        mContactNameTextView.setText(contactName);
        mPhoneNumberTextView.setText(phoneCall.phoneNumber);
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<PhoneCall> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }
}
