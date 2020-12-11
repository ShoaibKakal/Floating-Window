package com.shoaib.floatingwindow;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Status.class, version = 1, exportSchema = false)
public abstract class StatusDatabase extends RoomDatabase {

    private static StatusDatabase statusDatabase;

    public static synchronized StatusDatabase getStatusDatabase(Context context){
        if (statusDatabase == null){
            statusDatabase = Room.databaseBuilder(context, StatusDatabase.class, "status_db")
                    .build();
        }

        return statusDatabase;
    }

    public abstract StatusDao statusDao();
}
