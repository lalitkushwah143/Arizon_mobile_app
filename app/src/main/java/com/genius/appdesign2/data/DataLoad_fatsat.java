package com.genius.appdesign2.data;

import java.util.ArrayList;

public class DataLoad_fatsat {

    private String name;
    private String start;
    private String stop;
    private String time;


    public DataLoad_fatsat() {
    }

    public DataLoad_fatsat(String name, String start, String stop, String time) {
        this.name = name;
        this.start = start;
        this.stop = stop;
        this.time = time;
    }

    public DataLoad_fatsat(String start, String stop, String time) {
        this.start = start;
        this.stop = stop;
        this.time = time;
    }

//    public String getKey() {
//        return name;
//    }
//
//    public void setKey(String name) {
//        this.name = name;
//    }

    public String getTitle() {
        return name;
    }

    public void setTitle(String start) {
        this.start = name;
    }


    public String getStart() {
        return start;
    }
    public void setStart(String start) {
        this.start = start;
    }

    public String getMid() {
        return stop;
    }

    public void setMid(String stop) {
        this.stop = stop;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
