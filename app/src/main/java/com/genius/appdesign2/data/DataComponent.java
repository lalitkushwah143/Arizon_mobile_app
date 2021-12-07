package com.genius.appdesign2.data;

public class DataComponent {

    private String key;
    private String title;
    private String value;
    private String module_id;

    public DataComponent() {
    }

    public DataComponent(String key, String title, String value, String module_id) {
        this.key = key;
        this.title = title;
        this.value = value;
        this.module_id = module_id;
    }

    public DataComponent(String title, String value, String module_id) {
        this.title = title;
        this.value = value;
        this.module_id = module_id;
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
}
