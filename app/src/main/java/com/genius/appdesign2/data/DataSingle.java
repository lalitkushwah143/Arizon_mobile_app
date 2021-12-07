package com.genius.appdesign2.data;

public class DataSingle {

    String title;
    String value;

    public DataSingle() {
    }

    public DataSingle(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
