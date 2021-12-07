package com.genius.appdesign2.data;

public class DataAbbre {

    private String key;
    private String shorts;
    private String full;
    private int index;

    public DataAbbre() {
    }

    public DataAbbre(String key, String shorts, String full, int index) {
        this.key = key;
        this.shorts = shorts;
        this.full = full;
        this.index = index;
    }

    public DataAbbre(String shorts, String full, int index) {
        this.shorts = shorts;
        this.full = full;
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getShorts() {
        return shorts;
    }

    public void setShorts(String shorts) {
        this.shorts = shorts;
    }

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
