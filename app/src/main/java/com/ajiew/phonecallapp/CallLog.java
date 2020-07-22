package com.ajiew.phonecallapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CallLog {
    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    private int id;
    @ColumnInfo(name = "call")
    private String call;
    @ColumnInfo(name = "address")
    private String address;

    public CallLog(){}
    public CallLog(String call, String address) {
        this.call = call;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
