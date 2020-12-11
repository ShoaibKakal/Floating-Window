package com.shoaib.floatingwindow;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class HistoryViewModel extends AndroidViewModel {
    private StatusDatabase statusDatabase;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        statusDatabase = StatusDatabase.getStatusDatabase(application);
    }

    public Flowable<List<Status>> getAllStatus(){
        return statusDatabase.statusDao().getAllStatus();
    }

    public Completable removeFromHistory(Status status){
        return statusDatabase.statusDao().removeFromHistory(status);
    }
}
