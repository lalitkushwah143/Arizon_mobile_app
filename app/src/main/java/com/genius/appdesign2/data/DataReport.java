package com.genius.appdesign2.data;

public class DataReport {

    private String id;
    private String url;
    private String time;

    public DataReport() {
    }

    public DataReport(String id, String url, String time) {
        this.id = id;
        this.url = url;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
