package com.genius.appdesign2.data;

import java.util.ArrayList;

public class DataRealTime {

    private String key;
    private String recipe_name;
    private String recipe_id;
    private ArrayList<Float> temp_points;
    private String time;

    public DataRealTime() {
    }

    public DataRealTime(String key, String recipe_name, String recipe_id, ArrayList<Float> temp_points, String time) {
        this.key = key;
        this.recipe_name = recipe_name;
        this.recipe_id = recipe_id;
        this.temp_points = temp_points;
        this.time = time;
    }

    public DataRealTime(String recipe_name, String recipe_id, ArrayList<Float> temp_points, String time) {
        this.recipe_name = recipe_name;
        this.recipe_id = recipe_id;
        this.temp_points = temp_points;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    public String getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public ArrayList<Float> getTemp_points() {
        return temp_points;
    }

    public void setTemp_points(ArrayList<Float> temp_points) {
        this.temp_points = temp_points;
    }
}
