package com.vnsolution.virtualassistant.ui.common;

public interface IRecyclerViewRow<T> {
    void showData(T item);
    void setOnItemClickListener(OnRecyclerViewRowTouchListener<T> onRecyclerViewRowTouchListener);
}
