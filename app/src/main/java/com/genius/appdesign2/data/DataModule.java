package com.genius.appdesign2.data;

public class DataModule {

    private String key;
    private String title;
    private String desc;
    private String mid;

    public DataModule() {
    }

    public DataModule(String key, String title, String desc, String mid) {
        this.key = key;
        this.title = title;
        this.desc = desc;
        this.mid = mid;
    }

    public DataModule(String title, String desc, String mid) {
        this.title = title;
        this.desc = desc;
        this.mid = mid;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }
}
