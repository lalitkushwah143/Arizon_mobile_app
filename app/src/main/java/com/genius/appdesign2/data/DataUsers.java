package com.genius.appdesign2.data;

public class DataUsers {

    private String key;
    private String email;
    private String fName;
    private String lName;
    private String pass;
    private String phone;
    private String role;
    private String url;

    public DataUsers() {
    }

    public DataUsers(String key, String email, String fName, String lName, String pass, String phone, String role, String url) {
        this.key = key;
        this.email = email;
        this.fName = fName;
        this.lName = lName;
        this.pass = pass;
        this.phone = phone;
        this.role = role;
        this.url = url;
    }

    public DataUsers(String email, String fName, String lName, String pass, String phone, String role, String url) {
        this.email = email;
        this.fName = fName;
        this.lName = lName;
        this.pass = pass;
        this.phone = phone;
        this.role = role;
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
