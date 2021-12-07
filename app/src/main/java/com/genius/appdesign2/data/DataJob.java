package com.genius.appdesign2.data;

import com.google.firebase.Timestamp;

public class DataJob {

    private String key;
    private String title;
    private String rid;
    private String mid;
    private String desc;
    private String email;
    private Timestamp date;
    private Boolean status;

    public DataJob() {
    }

    public DataJob(String title, String rid, String mid, String desc, String email, Timestamp date, Boolean status) {
        this.title = title;
        this.rid = rid;
        this.mid = mid;
        this.desc = desc;
        this.email = email;
        this.date = date;
        this.status = status;
    }

    public DataJob(String key, String title, String rid, String mid, String desc, String email, Timestamp date, Boolean status) {
        this.key = key;
        this.title = title;
        this.rid = rid;
        this.mid = mid;
        this.desc = desc;
        this.email = email;
        this.date = date;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
