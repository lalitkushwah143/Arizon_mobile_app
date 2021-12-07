package com.genius.appdesign2.data;

public class DataMachines {

    private String key;
    private String title;
    private String desc;
    private String location;
    private String createdBy;

    public DataMachines() {
    }

    public DataMachines(String title, String desc, String location, String createdBy) {
        this.title = title;
        this.desc = desc;
        this.location = location;
        this.createdBy = createdBy;
    }

    public DataMachines(String key, String title, String desc, String location, String createdBy) {
        this.key = key;
        this.title = title;
        this.desc = desc;
        this.location = location;
        this.createdBy = createdBy;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
