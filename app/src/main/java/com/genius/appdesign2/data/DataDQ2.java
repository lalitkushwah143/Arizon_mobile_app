package com.genius.appdesign2.data;

import java.util.ArrayList;

public class DataDQ2 {

    String key;
    String title;
    String time;
    ArrayList<DataSingle2> arrayList;

    public DataDQ2() {
    }

    public DataDQ2(String key, String title, String time, ArrayList<DataSingle2> arrayList) {
        this.key = key;
        this.title = title;
        this.time = time;
        this.arrayList = arrayList;
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

    public ArrayList<DataSingle2> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<DataSingle2> arrayList) {
        this.arrayList = arrayList;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
