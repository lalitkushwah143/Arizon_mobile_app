package com.genius.appdesign2.data;

public class DataCompNew {

    private String key;
    private int index;
    private String title;
    private String value;
    private String module_id;
    private int response;
    private String issue_id;

    public DataCompNew() {
    }

    public DataCompNew(int index, String title, String value, String module_id, int response, String issue_id) {
        this.index = index;
        this.title = title;
        this.value = value;
        this.module_id = module_id;
        this.response = response;
        this.issue_id = issue_id;
    }

    public DataCompNew(String key, int index, String title, String value, String module_id, int response, String issue_id) {
        this.key = key;
        this.index = index;
        this.title = title;
        this.value = value;
        this.module_id = module_id;
        this.response = response;
        this.issue_id = issue_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
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
