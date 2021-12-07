package com.genius.appdesign2.data;

public class DataPoint {

    private String key;
    private String desc;
    private String tid;
    private int index;
    private int response;
    private String issue_id;

    public DataPoint() {
    }

    public DataPoint(String desc, String tid, int index, int response, String issue_id) {
        this.desc = desc;
        this.tid = tid;
        this.index = index;
        this.response = response;
        this.issue_id = issue_id;
    }

    public DataPoint(String key, String desc, String tid, int index, int response, String issue_id) {
        this.key = key;
        this.desc = desc;
        this.tid = tid;
        this.index = index;
        this.response = response;
        this.issue_id = issue_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public String getIssue_id() {
        return issue_id;
    }

    public void setIssue_id(String issue_id) {
        this.issue_id = issue_id;
    }
}
