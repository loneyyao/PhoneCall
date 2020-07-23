package com.ajiew.phonecallapp.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Address {
    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    private int id;
    @ColumnInfo(name = "name")
    private String name;

    public Address(){}
    public Address(String address){
        this.name = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
