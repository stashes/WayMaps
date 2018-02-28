package com.waymaps.data.requestEntity;

/**
 * Created by Admin on 27.01.2018.
 */

public class Action {

    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    public static final String CALL = "call";
    public static final String SESSION_UPDATE = "session_update";
    public static final String TRACK_COUNT = "m_track_count";
    public static final String FIRM_LIST = "m_firm_list";
    public static final String FIN_GET = "m_fin_get";
    public static final String TICKET_LIST = "m_ticket_list";
    public static final String TICKET_GET = "m_ticket_get";
    public static final String TICKET_ADD = "ticket_add";
    public static final String GET_CURRENT = "m_get_current";
    public static final String COMMENT_ADD = "comment_add";
    public static final String TRACKER_LIST = "m_tracker_list";
    public static final String GET_GROUPS = "m_get_groups";
    public static final String REPORT = "m_report";
    public static final String GET_TRACK = "m_get_track";
    public static final String GET_PARKING = "m_get_parking";
    public static final String POINT_DATA = "m_point_data";






    private String action;

    public Action(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
