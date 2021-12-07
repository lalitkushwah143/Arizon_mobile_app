package com.genius.appdesign2.data;

public class DataLyo {

    private int temp1;
    private int time1;
    private int time2;
    private int pressure;

    public DataLyo() {
    }

    public DataLyo(int temp1, int time1, int time2, int pressure) {
        this.temp1 = temp1;
        this.time1 = time1;
        this.time2 = time2;
        this.pressure = pressure;
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
