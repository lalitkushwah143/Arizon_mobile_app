package com.genius.appdesign2.data;

public class DataContent {

    private String key;
    private String title;
    private String mid;
    private String desc;
    private String createdAt;

    public DataContent() {
    }

    public DataContent(String key, String title, String mid, String desc, String createdAt) {
        this.key = key;
        this.title = title;
        this.mid = mid;
        this.desc = desc;
        this.createdAt = createdAt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
