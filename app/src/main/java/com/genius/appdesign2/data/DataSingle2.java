package com.genius.appdesign2.data;

public class DataSingle2 {

    String title;
    String value;
    int response;

    public DataSingle2() {
    }

    public DataSingle2(String title, String value, int response) {
        this.title = title;
        this.value = value;
        this.response = response;
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

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }
}
