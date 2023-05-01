package vn.sttechnology.virtualassistant.ui.reminders.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.card.MaterialCardView;
import vn.sttechnology.virtualassistant.R;
import vn.sttechnology.virtualassistant.common.ReminderSetting;
import vn.sttechnology.virtualassistant.common.UiSetting;
import vn.sttechnology.virtualassistant.model.Reminder;
import vn.sttechnology.virtualassistant.ui.common.IRecyclerViewRow;
import vn.sttechnology.virtualassistant.ui.common.OnRecyclerViewRowTouchListener;

import java.text.SimpleDateFormat;

public class ReminderCardFullWidthRow extends MaterialCardView implements IRecyclerViewRow<Reminder>, PopupMenu.OnMenuItemClickListener {
    private TextView mReminderTitleTextView;
    private TextView mReminderActionTextView;
    private TextView mReminderDateTextView;
    private TextView mReminderRepeatTextView;
    private TextView mReminderOptionsTextView;

    private Context mContext;
    private Reminder mReminder;
    private OnRecyclerViewRowTouchListener<Reminder> mOnRecyclerViewRowTouchListener;

    public ReminderCardFullWidthRow(Context context) {
        super(context);
        mContext = context;
    }

    public ReminderCardFullWidthRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ReminderCardFullWidthRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mReminderTitleTextView = findViewById(R.id.reminder_title);
        mReminderActionTextView = findViewById(R.id.reminder_action);
        mReminderDateTextView = findViewById(R.id.reminder_date);
        mReminderRepeatTextView = findViewById(R.id.reminder_repeat);

        mReminderOptionsTextView = findViewById(R.id.reminder_options);
        mReminderOptionsTextView.setOnClickListener(new OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.setOnMenuItemClickListener(ReminderCardFullWidthRow.this);
                popupMenu.inflate(R.menu.menu_reminder_options);

                MenuPopupHelper menuPopupHelper = new MenuPopupHelper(mContext, (MenuBuilder) popupMenu.getMenu(), v);
                menuPopupHelper.setForceShowIcon(true);
                menuPopupHelper.show();
            }
        });
    }

    @Override
    public void showData(Reminder reminder, String viewType) {
        mReminder = reminder;

        mReminderTitleTextView.setText(reminder.name);

        String action = reminder.action;
        if (action != null && !action.isEmpty() && !action.equals(ReminderSetting.ACTION_NONE)
            && reminder.extendedData != null && !reminder.extendedData.isEmpty()) {
            action += " " + reminder.extendedData;
        }
        mReminderActionTextView.setText("Action: " + action);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy 'at' HH:mm");
        mReminderDateTextView.setText(dateFormat.format(reminder.date));

        String repeat = (reminder.repeat != null)? reminder.repeat : "";
        mReminderRepeatTextView.setText(repeat);
    }

    @Override
    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<Reminder> onRecyclerViewRowTouchListener) {
        this.mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String action = "";
        switch (item.getItemId()) {
            case R.id.menu_item_edit:
                action = UiSetting.REMINDER_ACTION_EDIT;
                break;
            case R.id.menu_item_remove:
                action = UiSetting.REMINDER_ACTION_REMOVE;
                break;
            default:
                return false;
        }
        if (mReminder != null && action != null && action != "" && mOnRecyclerViewRowTouchListener != null) {
            mOnRecyclerViewRowTouchListener.onAction(mReminder, action);
        }
        return true;
    }
}
