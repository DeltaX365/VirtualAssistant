package com.vnsolution.virtualassistant.ui.common;

public interface IRecyclerViewRow<T> {
    void showData(T item, String viewType);
    void setOnItemClickListener(OnRecyclerViewRowTouchListener<T> onRecyclerViewRowTouchListener);
}
