package com.ajiew.phonecallapp;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AddressDao {
    @Query("SELECT * FROM Address")
    List<Address> getAll();

    @Query("SELECT * FROM Address WHERE name = :name")
    Address getUserByMobile(String name);

    @Insert
    void insertAll(Address... address);

    @Delete
    void delete(Address address);

    @Update
    void update(Address address);
}
