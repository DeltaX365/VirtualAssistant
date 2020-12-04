package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.PhoneCallEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneCallSummaryEntity;

import java.util.List;

@Dao
public interface PhoneCallDao extends BaseDao<PhoneCallEntity> {
    @Query("SELECT * FROM PhoneCall")
    List<PhoneCallEntity> getAll();

    @Query("SELECT Type, COUNT(*) AS Total, SUM(Duration) AS Duration " +
            "FROM PhoneCall " +
            "GROUP BY Type")
    List<PhoneCallSummaryEntity> getCallStat();

    @Query("SELECT Type, COUNT(*) AS Total, SUM(Duration) AS Duration " +
            "FROM PhoneCall " +
            "WHERE :phoneNumbers LIKE '%' || PhoneNumber ||'%' " +
            "GROUP BY Type")
    List<PhoneCallSummaryEntity> getCallStatByNumbers(String phoneNumbers);

    @Query("SELECT * " +
            "FROM PhoneCall " +
            "WHERE :phoneNumbers LIKE '%' || PhoneNumber ||'%' " +
            "ORDER BY Date DESC")
    List<PhoneCallEntity> getCallHistoryByNumbers(String phoneNumbers);

    @Query("SELECT * " +
            "FROM " +
            "( " +
            "SELECT * " +
            "FROM PhoneCall " +
            "ORDER BY Date DESC " +
            ") " +
            "GROUP BY PhoneNumber " +
            "ORDER BY Date DESC " +
            "LIMIT 0, 10")
    List<PhoneCallEntity> getRecentCalls();

    @Query("SELECT * " +
            "FROM " +
            "( " +
            "SELECT * " +
            "FROM PhoneCall " +
            "WHERE Type = :type " +
            "ORDER BY Date DESC " +
            ") " +
            "GROUP BY PhoneNumber " +
            "ORDER BY Date DESC " +
            "LIMIT 0, 10")
    List<PhoneCallEntity> getRecentCallsByType(int type);

    @Query("SELECT * " +
            "FROM " +
            "( " +
            "SELECT *, SUM(Duration) AS TotalDuration " +
            "FROM PhoneCall " +
            "GROUP BY PhoneNumber " +
            ") " +
            "ORDER BY TotalDuration DESC " +
            "LIMIT 0, 10")
    List<PhoneCallEntity> getMostCalls();

    @Query("SELECT * " +
            "FROM " +
            "( " +
            "SELECT *, SUM(Duration) AS TotalDuration " +
            "FROM PhoneCall " +
            "WHERE Type = :type " +
            "GROUP BY PhoneNumber " +
            ") " +
            "ORDER BY TotalDuration DESC " +
            "LIMIT 0, 10")
    List<PhoneCallEntity> getMostCallsByType(int type);

    @Query("DELETE FROM PhoneCall")
    void reset();
}
