package com.genius.appdesign2.data;

public class DataStep {

    private String key;
    private int index;
    private String title;
    private String desc;
    private String format;
    private String url;
    private String type;
    private String manual_id;

    public DataStep() {
    }

    public DataStep(String key, int index, String title, String desc, String format, String url, String type, String manual_id) {
        this.key = key;
        this.index = index;
        this.title = title;
        this.desc = desc;
        this.format = format;
        this.url = url;
        this.type = type;
        this.manual_id = manual_id;
    }

    public DataStep(int index, String title, String desc, String format, String url, String type, String manual_id) {
        this.index = index;
        this.title = title;
        this.desc = desc;
        this.format = format;
        this.url = url;
        this.type = type;
        this.manual_id = manual_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getManual_id() {
        return manual_id;
    }

    public void setManual_id(String manual_id) {
        this.manual_id = manual_id;
    }
}
