package com.genius.appdesign2.data;

public class DataSpecs {

    private String key;
    private int index;
    private String title;
    private String input;

    public DataSpecs() {
    }

    public DataSpecs(String key, int index, String title, String input) {
        this.key = key;
        this.index = index;
        this.title = title;
        this.input = input;
    }

    public DataSpecs(int index, String title, String input) {
        this.index = index;
        this.title = title;
        this.input = input;
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

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
