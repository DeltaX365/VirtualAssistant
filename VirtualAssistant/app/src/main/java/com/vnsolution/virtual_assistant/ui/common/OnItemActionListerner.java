package com.vnsolution.virtual_assistant.ui.common;

import android.view.View;

public interface OnItemActionListerner {
    void onTap(View view, int position);
    void onLongTap(View view, int position);
    void onDoubleTap(View view, int position);
}
