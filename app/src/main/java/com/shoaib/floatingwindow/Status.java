package com.shoaib.floatingwindow;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "status")
public class Status {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String Url;

    private String XSeconds;

    private String YSeconds;


//    public Status(String title, String url, String XSeconds, String YSeconds) {
//        this.title = title;
//        this.Url = url;
//        this.XSeconds = XSeconds;
//        this.YSeconds = YSeconds;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getXSeconds() {
        return XSeconds;
    }

    public void setXSeconds(String XSeconds) {
        this.XSeconds = XSeconds;
    }

    public String getYSeconds() {
        return YSeconds;
    }

    public void setYSeconds(String YSeconds) {
        this.YSeconds = YSeconds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
