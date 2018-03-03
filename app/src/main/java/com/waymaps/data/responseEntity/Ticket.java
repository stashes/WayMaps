package com.waymaps.data.responseEntity;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by nazar on 01.03.2018.
 */

public class Ticket {
    private String id;
    private String ticketId;
    private String text;
    private String createdDate;
    private String userTitle;
    private String userId;
    private String readDate;

    public Ticket(String id, String ticketId, String text, String createdDate, String userTitle, String userId, String readDate) {
        this.id = id;
        this.ticketId = ticketId;
        this.text = text;
        this.createdDate = createdDate;
        this.userTitle = userTitle;
        this.userId = userId;
        this.readDate = readDate;
    }

    public Ticket() {
    }

    public String getId() {
        return id;
    }
    @JsonSetter("id")
    public void setId(String id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }
    @JsonSetter("ticket_id")
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getText() {
        return text;
    }
    @JsonSetter("text")
    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedDate() {
        return createdDate;
    }
    @JsonSetter("created_date")
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUserTitle() {
        return userTitle;
    }
    @JsonSetter("user_title")
    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getUserId() {
        return userId;
    }
    @JsonSetter("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReadDate() {
        return readDate;
    }
    @JsonSetter("read_date")
    public void setReadDate(String readDate) {
        this.readDate = readDate;
    }
}
