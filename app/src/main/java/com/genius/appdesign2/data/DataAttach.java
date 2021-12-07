package com.genius.appdesign2.data;

public class DataAttach {

    private String key;
    private String desc;
    private String rev;
    private String dno;
    private int index;

    public DataAttach() {
    }

    public DataAttach(String desc, String rev, String dno, int index) {
        this.desc = desc;
        this.rev = rev;
        this.dno = dno;
        this.index = index;
    }

    public DataAttach(String key, String desc, String rev, String dno, int index) {
        this.key = key;
        this.desc = desc;
        this.rev = rev;
        this.dno = dno;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public String getDno() {
        return dno;
    }

    public void setDno(String dno) {
        this.dno = dno;
    }
}
