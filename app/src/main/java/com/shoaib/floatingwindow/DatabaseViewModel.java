package com.shoaib.floatingwindow;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import io.reactivex.Completable;

public class  DatabaseViewModel  extends AndroidViewModel {

    private StatusDatabase statusDatabase;

    public DatabaseViewModel(@NonNull Application application) {
        super(application);

        this.statusDatabase = StatusDatabase.getStatusDatabase(application);

    }

    public Completable addToHistory(Status status){
        return statusDatabase.statusDao().addToHistory(status);
    }

    public Completable removeFromHistory(Status status){
        return statusDatabase.statusDao().removeFromHistory(status);
    }
}
