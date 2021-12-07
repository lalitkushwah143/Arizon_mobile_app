package com.genius.appdesign2.data;

public class DataRecipe{

    private int index;
    private String step;
    private String rid;
    private int temp1;
    private int time1;
    private int time2;
    private int pressure;

    public DataRecipe() {
    }

    public DataRecipe(int index, String step, String rid, int temp1, int time1, int time2, int pressure) {
        this.index = index;
        this.step = step;
        this.rid = rid;
        this.temp1 = temp1;
        this.time1 = time1;
        this.time2 = time2;
        this.pressure = pressure;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public int getTemp1() {
        return temp1;
    }

    public void setTemp1(int temp1) {
        this.temp1 = temp1;
    }

    public int getTime1() {
        return time1;
    }

    public void setTime1(int time1) {
        this.time1 = time1;
    }

    public int getTime2() {
        return time2;
    }

    public void setTime2(int time2) {
        this.time2 = time2;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }
}
