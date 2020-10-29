package com.vnsolution.virtualassistant.ui.common;

public abstract class RecyclerViewRowTouchListener<T> implements OnRecyclerViewRowTouchListener<T> {
    @Override
    public void onTap(T item) {
    }

    @Override
    public void onLongTap(T item) {
    }

    @Override
    public void onDoubleTap(T item) {
    }

    @Override
    public void onAction(T item, String action) {
    }
}
