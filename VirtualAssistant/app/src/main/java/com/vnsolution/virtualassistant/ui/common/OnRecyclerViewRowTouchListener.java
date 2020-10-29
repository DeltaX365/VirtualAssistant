package com.vnsolution.virtualassistant.ui.common;

public interface OnRecyclerViewRowTouchListener<T> {
    void onTap(T item);
    void onLongTap(T item);
    void onDoubleTap(T item);
    void onAction(T item, String action);
}
