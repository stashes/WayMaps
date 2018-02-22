package com.waymaps.data.responseEntity;

/**
 * Created by Admin on 05.02.2018.
 */

public class Report {
    private String total_odometr;
    private String parking_count;
    private String total_period;
    private String parking_time;
    private String action_time;
    private String speed_action;
    private String speed_avg;
    private String stop_time;
    private String max_speed;
    private String overspeed_count;
    private String overspeed_seconds;
    private String overspeed_percent;

    public Report() {
    }

    public Report(String total_odometr, String parking_count, String total_period,
                  String parking_time, String action_time, String speed_action, String speed_avg,
                  String stop_time, String max_speed, String overspeed_count,
                  String overspeed_seconds, String overspeed_percent) {
        this.total_odometr = total_odometr;
        this.parking_count = parking_count;
        this.total_period = total_period;
        this.parking_time = parking_time;
        this.action_time = action_time;
        this.speed_action = speed_action;
        this.speed_avg = speed_avg;
        this.stop_time = stop_time;
        this.max_speed = max_speed;
        this.overspeed_count = overspeed_count;
        this.overspeed_seconds = overspeed_seconds;
        this.overspeed_percent = overspeed_percent;
    }

    public String getTotal_odometr() {
        return total_odometr;
    }

    public void setTotal_odometr(String total_odometr) {
        this.total_odometr = total_odometr;
    }

    public String getParking_count() {
        return parking_count;
    }

    public void setParking_count(String parking_count) {
        this.parking_count = parking_count;
    }

    public String getTotal_period() {
        return total_period;
    }

    public void setTotal_period(String total_period) {
        this.total_period = total_period;
    }

    public String getParking_time() {
        return parking_time;
    }

    public void setParking_time(String parking_time) {
        this.parking_time = parking_time;
    }

    public String getAction_time() {
        return action_time;
    }

    public void setAction_time(String action_time) {
        this.action_time = action_time;
    }

    public String getSpeed_action() {
        return speed_action;
    }

    public void setSpeed_action(String speed_action) {
        this.speed_action = speed_action;
    }

    public String getSpeed_avg() {
        return speed_avg;
    }

    public void setSpeed_avg(String speed_avg) {
        this.speed_avg = speed_avg;
    }

    public String getStop_time() {
        return stop_time;
    }

    public void setStop_time(String stop_time) {
        this.stop_time = stop_time;
    }

    public String getMax_speed() {
        return max_speed;
    }

    public void setMax_speed(String max_speed) {
        this.max_speed = max_speed;
    }

    public String getOverspeed_count() {
        return overspeed_count;
    }

    public void setOverspeed_count(String overspeed_count) {
        this.overspeed_count = overspeed_count;
    }

    public String getOverspeed_seconds() {
        return overspeed_seconds;
    }

    public void setOverspeed_seconds(String overspeed_seconds) {
        this.overspeed_seconds = overspeed_seconds;
    }

    public String getOverspeed_percent() {
        return overspeed_percent;
    }

    public void setOverspeed_percent(String overspeed_percent) {
        this.overspeed_percent = overspeed_percent;
    }

    @Override
    public String toString() {
        return "Report{" +
                "total_odometr='" + total_odometr + '\'' +
                ", parking_count='" + parking_count + '\'' +
                ", total_period='" + total_period + '\'' +
                ", parking_time='" + parking_time + '\'' +
                ", action_time='" + action_time + '\'' +
                ", speed_action='" + speed_action + '\'' +
                ", speed_avg='" + speed_avg + '\'' +
                ", stop_time='" + stop_time + '\'' +
                ", max_speed='" + max_speed + '\'' +
                ", overspeed_count='" + overspeed_count + '\'' +
                ", overspeed_seconds='" + overspeed_seconds + '\'' +
                ", overspeed_percent='" + overspeed_percent + '\'' +
                '}';
    }
}
