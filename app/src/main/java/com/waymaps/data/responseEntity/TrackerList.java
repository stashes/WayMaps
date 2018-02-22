package com.waymaps.data.responseEntity;

/**
 * Created by Admin on 05.02.2018.
 */

public class TrackerList {
    private String id;
    private String title;
    private String color;
    private String marker;
    private String maxspeed;

    public TrackerList() {
    }

    public TrackerList(String id, String title, String color, String marker, String maxspeed) {
        this.id = id;
        this.title = title;
        this.color = color;
        this.marker = marker;
        this.maxspeed = maxspeed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getMaxspeed() {
        return maxspeed;
    }

    public void setMaxspeed(String maxspeed) {
        this.maxspeed = maxspeed;
    }
}
