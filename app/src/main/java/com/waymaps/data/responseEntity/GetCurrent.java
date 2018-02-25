package com.waymaps.data.responseEntity;

/**
 * Created by Admin on 19.02.2018.
 */

public class GetCurrent {
    private String id;
    private String l_date;
    private String lat;
    private String lon;
    private String speed;
    private String sat;
    private String gsm;
    private String power;
    private String ignition;
    private String vector;
    private String motor;
    private String voltage;
    private String dff;
    private String q_dff;
    private String last_parking_start;
    private String status;
    private String tracker_title;
    private String group_id;
    private String color;
    private String marker;
    private String note;

    public GetCurrent() {
    }

    public GetCurrent(String id, String l_date, String lat, String lon, String speed, String sat, String gsm, String power, String ignition, String vector, String motor, String voltage, String dff, String q_dff, String last_parking_start, String status, String tracker_title, String group_id, String color, String marker, String note) {
        this.id = id;
        this.l_date = l_date;
        this.lat = lat;
        this.lon = lon;
        this.speed = speed;
        this.sat = sat;
        this.gsm = gsm;
        this.power = power;
        this.ignition = ignition;
        this.vector = vector;
        this.motor = motor;
        this.voltage = voltage;
        this.dff = dff;
        this.q_dff = q_dff;
        this.last_parking_start = last_parking_start;
        this.status = status;
        this.tracker_title = tracker_title;
        this.group_id = group_id;
        this.color = color;
        this.marker = marker;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getL_date() {
        return l_date;
    }

    public void setL_date(String l_date) {
        this.l_date = l_date;
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

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getSat() {
        return sat;
    }

    public void setSat(String sat) {
        this.sat = sat;
    }

    public String getGsm() {
        return gsm;
    }

    public void setGsm(String gsm) {
        this.gsm = gsm;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getIgnition() {
        return ignition;
    }

    public void setIgnition(String ignition) {
        this.ignition = ignition;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getDff() {
        return dff;
    }

    public void setDff(String dff) {
        this.dff = dff;
    }

    public String getQ_dff() {
        return q_dff;
    }

    public void setQ_dff(String q_dff) {
        this.q_dff = q_dff;
    }

    public String getLast_parking_start() {
        return last_parking_start;
    }

    public void setLast_parking_start(String last_parking_start) {
        this.last_parking_start = last_parking_start;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTracker_title() {
        return tracker_title;
    }

    public void setTracker_title(String tracker_title) {
        this.tracker_title = tracker_title;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getVector() {
        return vector;
    }

    public void setVector(String vector) {
        this.vector = vector;
    }
}
