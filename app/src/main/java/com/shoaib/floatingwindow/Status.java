package com.shoaib.floatingwindow;

public class Status {

    private String title;
    private String Url;
    private String XSeconds;
    private String YSeconds;


    public Status(String title, String url, String XSeconds, String YSeconds) {
        this.title = title;
        Url = url;
        this.XSeconds = XSeconds;
        this.YSeconds = YSeconds;
    }

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
}
