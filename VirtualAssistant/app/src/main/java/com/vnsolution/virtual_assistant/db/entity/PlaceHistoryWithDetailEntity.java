package com.vnsolution.virtual_assistant.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class PlaceHistoryWithDetailEntity {
    @Embedded public PlaceHistoryEntity placeHistory;
    @Relation(
        parentColumn = "PlaceId",
        entityColumn = "Id"
    )
    public PlaceEntity place;
}
