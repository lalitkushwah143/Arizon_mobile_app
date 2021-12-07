package com.genius.appdesign2.data;

public class DataTitle {

    private String key;
    private int index;
    private String title;

    public DataTitle() {
    }

    public DataTitle(int index, String title) {
        this.index = index;
        this.title = title;
    }

    public DataTitle(String key, int index, String title) {
        this.key = key;
        this.index = index;
        this.title = title;
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
}
