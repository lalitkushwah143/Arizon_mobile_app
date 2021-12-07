package com.genius.appdesign2.data;

public class DataIssue {

    private String key;
    private String module;
    private String title;
    private String content;
    private Boolean flag;

    public DataIssue() {
    }

    public DataIssue(String key, String module, String title, String content, Boolean flag) {
        this.key = key;
        this.module = module;
        this.title = title;
        this.content = content;
        this.flag = flag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }
}
