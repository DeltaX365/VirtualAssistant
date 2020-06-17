package com.vnsolution.virtualassistant.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class PlaceWithHistoryDetailEntity {
    @Embedded public PlaceEntity place;
    @Relation(
        parentColumn = "Id",
        entityColumn = "PlaceId"
    )
    public List<PlaceHistoryEntity> placeHistories;
}
