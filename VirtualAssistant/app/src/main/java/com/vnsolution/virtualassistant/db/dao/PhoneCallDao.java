package com.vnsolution.virtualassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.vnsolution.virtualassistant.db.entity.PhoneCallEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneCallSummaryEntity;
import com.vnsolution.virtualassistant.db.entity.PhoneContactEntity;

import java.util.List;

@Dao
public interface PhoneCallDao extends BaseDao<PhoneCallEntity> {
    @Query("SELECT * FROM PhoneCall ORDER BY Date DESC")
    List<PhoneCallEntity> getAll();

    @Query("SELECT * FROM PhoneCall WHERE Id = :id")
    PhoneCallEntity getById(String id);

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
            "LIMIT 0, 10")
    List<PhoneCallEntity> getRecentCalls();

    @Query("SELECT * " +
            "FROM " +
            "( " +
            "SELECT PhoneCall.* " +
            "FROM PhoneCall " +
            "LEFT JOIN PhoneContact ON PhoneContact.PhoneNumber LIKE '%' || PhoneCall.PhoneNumber || '%' " +
            "WHERE NULLIF(TRIM(PhoneContact.DisplayName), '') IS NOT NULL " +
            "ORDER BY PhoneCall.Date DESC " +
            ") " +
            "GROUP BY PhoneNumber " +
            "LIMIT 0, 10")
    List<PhoneCallEntity> getRecentAuthorizedCalls();

    @Query("SELECT * " +
            "FROM " +
            "( " +
            "SELECT * " +
            "FROM PhoneCall " +
            "WHERE Type = :type " +
            "ORDER BY Date DESC " +
            ") " +
            "GROUP BY PhoneNumber " +
            "LIMIT 0, 10")
    List<PhoneCallEntity> getRecentCalls(int type);

    @Query("SELECT * " +
            "FROM " +
            "( " +
            "SELECT PhoneCall.* " +
            "FROM PhoneCall " +
            "LEFT JOIN PhoneContact ON PhoneContact.PhoneNumber LIKE '%' || PhoneCall.PhoneNumber || '%' " +
            "WHERE PhoneCall.Type = :type AND NULLIF(TRIM(PhoneContact.DisplayName), '') IS NOT NULL " +
            "ORDER BY PhoneCall.Date DESC " +
            ") " +
            "GROUP BY PhoneNumber " +
            "LIMIT 0, 10")
    List<PhoneCallEntity> getRecentAuthorizedCalls(int type);

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
            "SELECT PhoneCall.*, SUM(Duration) AS TotalDuration " +
            "FROM PhoneCall " +
            "LEFT JOIN PhoneContact ON PhoneContact.PhoneNumber LIKE '%' || PhoneCall.PhoneNumber || '%' " +
            "WHERE NULLIF(TRIM(PhoneContact.DisplayName), '') IS NOT NULL " +
            "GROUP BY PhoneCall.PhoneNumber " +
            ") " +
            "ORDER BY TotalDuration DESC " +
            "LIMIT 0, 10")
    List<PhoneCallEntity> getMostAuthorizedCalls();

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
    List<PhoneCallEntity> getMostCalls(int type);

    @Query("SELECT * " +
            "FROM " +
            "( " +
            "SELECT PhoneCall.*, SUM(Duration) AS TotalDuration " +
            "FROM PhoneCall " +
            "LEFT JOIN PhoneContact ON PhoneContact.PhoneNumber LIKE '%' || PhoneCall.PhoneNumber || '%' " +
            "WHERE PhoneCall.Type = :type AND NULLIF(TRIM(PhoneContact.DisplayName), '') IS NOT NULL " +
            "GROUP BY PhoneCall.PhoneNumber " +
            ") " +
            "ORDER BY TotalDuration DESC " +
            "LIMIT 0, 10")
    List<PhoneCallEntity> getMostAuthorizedCalls(int type);

    @Query("DELETE FROM PhoneCall")
    void reset();
}
