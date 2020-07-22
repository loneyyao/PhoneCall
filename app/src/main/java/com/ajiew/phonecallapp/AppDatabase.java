package com.ajiew.phonecallapp;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Address.class,CallLog.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "phone.db";
    private static AppDatabase INSTANCE;
    private static final Object sLock = new Object();
    public abstract AddressDao addressDao();
    public abstract CallLogDao callLogDao();

    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE =
                        Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME).allowMainThreadQueries()
                                .build();
            }
            return INSTANCE;
        }
    }
}
