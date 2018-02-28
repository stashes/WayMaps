package com.waymaps.data.responseEntity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by nazar on 28.02.2018.
 */

public class TicketList {
    private String id;
    private String created_date;
    private String tracker_id;
    private String tracker_title;
    private String text;

    public TicketList() {
    }

    public TicketList(String id, String created_date, String tracker_id, String tracker_title, String text) {
        this.id = id;
        this.created_date = created_date;
        this.tracker_id = tracker_id;
        this.tracker_title = tracker_title;
        this.text = text;
    }
    @JsonGetter("id")
    public String getId() {
        return id;
    }
    @JsonSetter("id")
    public void setId(String id) {
        this.id = id;
    }
    @JsonGetter("created_date")
    public String getCreated_date() {
        return created_date;
    }
    @JsonSetter("created_date")
    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
    @JsonGetter("tracker_id")
    public String getTracker_id() {
        return tracker_id;
    }
    @JsonSetter("tracker_id")
    public void setTracker_id(String tracker_id) {
        this.tracker_id = tracker_id;
    }
    @JsonGetter("tracker_title")
    public String getTracker_title() {
        return tracker_title;
    }
    @JsonSetter("tracker_title")
    public void setTracker_title(String tracker_title) {
        this.tracker_title = tracker_title;
    }
    @JsonGetter("text")
    public String getText() {
        return text;
    }
    @JsonSetter("text")
    public void setText(String text) {
        this.text = text;
    }
}
