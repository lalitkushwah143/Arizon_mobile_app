package com.genius.appdesign2.data;

import com.google.firebase.Timestamp;

public class DataDQNew {

    private String key;
    private String mid;
    private String name;
    private String desc;
    private Timestamp timestamp;

    public DataDQNew() {
    }

    public DataDQNew(String key, String mid, String name, String desc, Timestamp timestamp) {
        this.key = key;
        this.mid = mid;
        this.name = name;
        this.desc = desc;
        this.timestamp = timestamp;
    }

    public DataDQNew(String mid, String name, String desc, Timestamp timestamp) {
        this.mid = mid;
        this.name = name;
        this.desc = desc;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
