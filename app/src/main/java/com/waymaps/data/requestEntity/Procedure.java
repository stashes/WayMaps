package com.waymaps.data.requestEntity;

/**
 * Created by Admin on 04.02.2018.
 */

public class Procedure extends Action {

    private String name;
    private String params;
    private String format;
    private String identficator;
    private String user_id;

    public Procedure(String action) {
        super(action);
    }

    public Procedure(String action, String name, String params, String format, String identficator, String user_id) {
        super(action);
        this.name = name;
        this.params = params;
        this.format = format;
        this.identficator = identficator;
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getIdentficator() {
        return identficator;
    }

    public void setIdentficator(String identficator) {
        this.identficator = identficator;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
