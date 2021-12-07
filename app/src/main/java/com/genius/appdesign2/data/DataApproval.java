package com.genius.appdesign2.data;

import com.google.firebase.Timestamp;

public class DataApproval {

    private String key;
    private String name;
    private String url;
    private Timestamp timestamp;

    public DataApproval() {
    }

    public DataApproval(String name, String url, Timestamp timestamp) {
        this.name = name;
        this.url = url;
        this.timestamp = timestamp;
    }

    public DataApproval(String key, String name, String url, Timestamp timestamp) {
        this.key = key;
        this.name = name;
        this.url = url;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
