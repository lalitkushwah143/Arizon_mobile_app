package com.genius.appdesign2.data;

public class DataSteps {

    private String key;
    private String title;
    private String cid;
    private String desc;
    private String createdAt;
    private String img;
    private String uniqueKey;

    public DataSteps() {
    }

    public DataSteps(String key, String title, String cid, String desc, String createdAt, String img, String uniqueKey) {
        this.key = key;
        this.title = title;
        this.cid = cid;
        this.desc = desc;
        this.createdAt = createdAt;
        this.img = img;
        this.uniqueKey = uniqueKey;
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

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
