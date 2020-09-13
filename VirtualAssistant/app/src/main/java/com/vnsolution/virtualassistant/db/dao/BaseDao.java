package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

interface BaseDao<T> {
    @Insert
    void insert(T obj);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<T> obj);

    @Update
    void update(T obj);

    @Delete
    void delete(T obj);
}
