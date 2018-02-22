package com.waymaps.data.responseEntity;

/**
 * Created by Admin on 05.02.2018.
 */

public class PointData {
    private String speed;
    private String sat;
    private String gsm;
    private String power;
    private String ignition;
    private String motor;
    private String voltage;
    private String note;

    public PointData() {
    }

    public PointData(String speed, String sat, String gsm, String power, String ignition, String motor, String voltage, String note) {
        this.speed = speed;
        this.sat = sat;
        this.gsm = gsm;
        this.power = power;
        this.ignition = ignition;
        this.motor = motor;
        this.voltage = voltage;
        this.note = note;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "PointData{" +
                "speed='" + speed + '\'' +
                ", sat='" + sat + '\'' +
                ", gsm='" + gsm + '\'' +
                ", power='" + power + '\'' +
                ", ignition='" + ignition + '\'' +
                ", motor='" + motor + '\'' +
                ", voltage='" + voltage + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

}
