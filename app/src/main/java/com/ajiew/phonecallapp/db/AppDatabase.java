package com.ajiew.phonecallapp.db;

import android.content.Context;
import android.database.SQLException;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Address.class,CallLog.class}, version = 4, exportSchema = false)
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
                        Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                                .addMigrations(MIGRATION_2_3,MIGRATION_3_4)
                                .allowMainThreadQueries()
                                .build();
            }
            return INSTANCE;
        }
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //此处对于数据库中的所有更新都需要写下面的代码

            try {
                database.execSQL("ALTER TABLE Address ADD COLUMN type INTEGER NOT NULL DEFAULT 1");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //此处对于数据库中的所有更新都需要写下面的代码

            try {
                database.execSQL("ALTER TABLE CallLog ADD COLUMN callTime VARCHAR(100) DEFAULT NULL");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    };
}
