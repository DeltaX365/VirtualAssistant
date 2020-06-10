package com.vnsolution.virtualassistant.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.vnsolution.virtualassistant.model.PlaceHistory;

public class PlaceHistoryWithDetailEntity {
    @Embedded public PlaceHistoryEntity placeHistory;
    @Relation(
        parentColumn = "PlaceId",
        entityColumn = "Id"
    )
    public PlaceEntity place;
}
