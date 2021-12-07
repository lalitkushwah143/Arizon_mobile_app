package com.genius.appdesign2.data;

public class DataUser {

    private String key;
    private String email;
    private String role;

    public DataUser() {
    }

    public DataUser(String key, String email, String role) {
        this.key = key;
        this.email = email;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
