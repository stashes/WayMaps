package com.waymaps.data.responseEntity;

/**
 * Created by Admin on 05.02.2018.
 */

public class TrackCount {
    private String coun;
    private String odo;
    private String max_speed;

    public TrackCount() {
    }

    public TrackCount(String coun, String odo, String max_speed) {
        this.coun = coun;
        this.odo = odo;
        this.max_speed = max_speed;
    }

    public String getMax_speed() {
        return max_speed;
    }

    public void setMax_speed(String max_speed) {
        this.max_speed = max_speed;
    }

    public String getCoun() {
        return coun;
    }

    public void setCoun(String coun) {
        this.coun = coun;
    }

    public String getOdo() {
        return odo;
    }

    public void setOdo(String odo) {
        this.odo = odo;
    }

    @Override
    public String toString() {
        return "TrackCount{" +
                "coun='" + coun + '\'' +
                ", odo='" + odo + '\'' +
                ", max_speed='" + max_speed + '\'' +
                '}';
    }
}
