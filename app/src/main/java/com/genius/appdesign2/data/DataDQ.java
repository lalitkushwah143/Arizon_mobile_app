package com.genius.appdesign2.data;

import java.util.ArrayList;

public class DataDQ {

    String key;
    String title;
    ArrayList<DataSingle> arrayList;

    public DataDQ() {
    }

    public DataDQ(String key, String title, ArrayList<DataSingle> arrayList) {
        this.key = key;
        this.title = title;
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

    public ArrayList<DataSingle> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<DataSingle> arrayList) {
        this.arrayList = arrayList;
    }
}
