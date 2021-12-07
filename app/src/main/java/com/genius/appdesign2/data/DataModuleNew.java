package com.genius.appdesign2.data;

public class DataModuleNew {

    private String key;
    private String title;
    private String desc;
    private int index;
    private int type;

    public DataModuleNew() {
    }

    public DataModuleNew(String title, String desc, int index) {
        this.title = title;
        this.desc = desc;
        this.index = index;
    }

    public DataModuleNew(String key, String title, String desc, int index) {
        this.key = key;
        this.title = title;
        this.desc = desc;
        this.index = index;
    }

    public DataModuleNew(String title, String desc, int index, int type) {
        this.title = title;
        this.desc = desc;
        this.index = index;
        this.type = type;
    }

    public DataModuleNew(String key, String title, String desc, int index, int type) {
        this.key = key;
        this.title = title;
        this.desc = desc;
        this.index = index;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
