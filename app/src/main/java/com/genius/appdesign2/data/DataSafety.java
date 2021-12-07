package com.genius.appdesign2.data;

public class DataSafety {

    private String key;
    private String desc;
    private String cause;
    private String action;
    private int index;
    private int response;
    private String issue_id;

    public DataSafety() {
    }

    public DataSafety(String desc, String cause, String action, int index, int response, String issue_id) {
        this.desc = desc;
        this.cause = cause;
        this.action = action;
        this.index = index;
        this.response = response;
        this.issue_id = issue_id;
    }

    public DataSafety(String key, String desc, String cause, String action, int index, int response, String issue_id) {
        this.key = key;
        this.desc = desc;
        this.cause = cause;
        this.action = action;
        this.index = index;
        this.response = response;
        this.issue_id = issue_id;
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

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
