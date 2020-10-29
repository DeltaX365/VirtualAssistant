package com.vnsolution.virtualassistant.ui.common;

public interface RecyclerViewRow<T> {
    void showData(T item);
    void setOnItemClickListener(OnRecyclerViewRowTouchListener<T> onRecyclerViewRowTouchListener);
}
