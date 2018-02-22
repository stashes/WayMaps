package com.waymaps.data.responseEntity;

/**
 * Created by Admin on 05.02.2018.
 */

public class GetTrack {
    private String id;
    private String date;
    private String lat;
    private String lon;
    private String odometr;
    private String count_dff;
    private String over_speed;


    public GetTrack() {
    }

    public GetTrack(String id, String date, String lat, String lon, String odometr,
                    String count_dff, String over_speed) {
        this.id = id;
        this.date = date;
        this.lat = lat;
        this.lon = lon;
        this.odometr = odometr;
        this.count_dff = count_dff;
        this.over_speed = over_speed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getOdometr() {
        return odometr;
    }

    public void setOdometr(String odometr) {
        this.odometr = odometr;
    }

    public String getCount_dff() {
        return count_dff;
    }

    public void setCount_dff(String count_dff) {
        this.count_dff = count_dff;
    }

    public String getOver_speed() {
        return over_speed;
    }

    public void setOver_speed(String over_speed) {
        this.over_speed = over_speed;
    }

    @Override
    public String toString() {
        return "GetTrack{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", odometr='" + odometr + '\'' +
                ", count_dff='" + count_dff + '\'' +
                ", over_speed='" + over_speed + '\'' +
                '}';
    }
}
