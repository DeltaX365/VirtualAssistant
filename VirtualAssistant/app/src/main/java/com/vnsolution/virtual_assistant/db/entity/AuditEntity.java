package com.vnsolution.virtual_assistant.db.entity;

import androidx.room.ColumnInfo;

import java.util.Date;

public class AuditEntity extends BaseEntity {
    @ColumnInfo(name = "CreatedDate")
    public Date createdDate;

    @ColumnInfo(name = "UpdatedDate")
    public Date updatedDate;
}
