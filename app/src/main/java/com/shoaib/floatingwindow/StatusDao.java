package com.shoaib.floatingwindow;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface StatusDao {

    @Query("SELECT * FROM status")
    Flowable<List<Status>> getAllStatus();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable addToHistory(Status status);


    @Delete
    Completable removeFromHistory(Status status);
}
