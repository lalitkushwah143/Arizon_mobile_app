package com.genius.appdesign2.data;

public class DataCompNew2 {

    private String key;
    private String desc;
    private String req;
    private String inst;
    private String connection;
    private String module_id;
    private int index;
    private int response;
    private String issue_id;

    public DataCompNew2() {
    }

    public DataCompNew2(String desc, String req, String inst, String connection, String module_id, int index, int response, String issue_id) {
        this.desc = desc;
        this.req = req;
        this.inst = inst;
        this.connection = connection;
        this.module_id = module_id;
        this.index = index;
        this.response = response;
        this.issue_id = issue_id;
    }

    public DataCompNew2(String key, String desc, String req, String inst, String connection, String module_id, int index, int response, String issue_id) {
        this.key = key;
        this.desc = desc;
        this.req = req;
        this.inst = inst;
        this.connection = connection;
        this.module_id = module_id;
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

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }

    public String getInst() {
        return inst;
    }

    public void setInst(String inst) {
        this.inst = inst;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
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
