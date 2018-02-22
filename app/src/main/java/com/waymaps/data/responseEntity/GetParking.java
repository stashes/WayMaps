package com.waymaps.data.responseEntity;

/**
 * Created by Admin on 05.02.2018.
 */

public class GetParking {
    private String start_date;
    private String end_date;
    private String parking_lat;
    private String parking_lon;
    private String duration;
    private String p_odometr;

    public GetParking() {
    }

    public GetParking(String start_date, String end_date, String parking_lat,
                      String parking_lon, String duration, String p_odometr) {
        this.start_date = start_date;
        this.end_date = end_date;
        this.parking_lat = parking_lat;
        this.parking_lon = parking_lon;
        this.duration = duration;
        this.p_odometr = p_odometr;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getParking_lat() {
        return parking_lat;
    }

    public void setParking_lat(String parking_lat) {
        this.parking_lat = parking_lat;
    }

    public String getParking_lon() {
        return parking_lon;
    }

    public void setParking_lon(String parking_lon) {
        this.parking_lon = parking_lon;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getP_odometr() {
        return p_odometr;
    }

    public void setP_odometr(String p_odometr) {
        this.p_odometr = p_odometr;
    }

    @Override
    public String toString() {
        return "GetParking{" +
                "start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", parking_lat='" + parking_lat + '\'' +
                ", parking_lon='" + parking_lon + '\'' +
                ", duration='" + duration + '\'' +
                ", p_odometr='" + p_odometr + '\'' +
                '}';
    }
}
