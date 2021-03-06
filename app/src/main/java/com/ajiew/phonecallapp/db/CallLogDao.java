package com.ajiew.phonecallapp.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CallLogDao {
    @Query("SELECT * FROM CallLog ORDER BY id DESC")
    List<CallLog> getAll();


    @Insert
    void insertAll(CallLog... callLog);

    @Delete
    void delete(CallLog callLog);

    @Update
    void update(CallLog callLog);
}
