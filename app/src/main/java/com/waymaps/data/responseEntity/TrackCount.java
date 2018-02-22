package com.waymaps.data.responseEntity;

/**
 * Created by Admin on 05.02.2018.
 */

public class TrackCount {
    private String coun;
    private String odo;

    public TrackCount() {
    }

    public TrackCount(String coun, String odo) {
        this.coun = coun;
        this.odo = odo;
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
                '}';
    }
}
