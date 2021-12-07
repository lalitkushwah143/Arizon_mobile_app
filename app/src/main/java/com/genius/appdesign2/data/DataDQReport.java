package com.genius.appdesign2.data;

import java.util.ArrayList;

public class DataDQReport {

    private String key;
    private String title;
    private String desc;
    private String mid;
    private String date;
    private ArrayList<DataComponent2> arrayList;

    public DataDQReport() {
    }

    public DataDQReport(String key, String title, String desc, String mid, String date, ArrayList<DataComponent2> arrayList) {
        this.key = key;
        this.title = title;
        this.desc = desc;
        this.mid = mid;
        this.date = date;
        this.arrayList = arrayList;
    }

    public DataDQReport(String title, String desc, String mid, String date, ArrayList<DataComponent2> arrayList) {
        this.title = title;
        this.desc = desc;
        this.mid = mid;
        this.date = date;
        this.arrayList = arrayList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public ArrayList<DataComponent2> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<DataComponent2> arrayList) {
        this.arrayList = arrayList;
    }
}
