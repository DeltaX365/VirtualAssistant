package com.vnsolution.virtualassistant.ui.travel.events;

import com.vnsolution.virtualassistant.ui.common.OnItemActionListerner;

public interface OnTravelActionListerner extends OnItemActionListerner {
    void onPlaceConfirmed(String placeId);
    void onPlaceEdited(String placeId);
}
