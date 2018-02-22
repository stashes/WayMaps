package com.waymaps.data.responseEntity;

/**
 * Created by Admin on 05.02.2018.
 */

public class FirmList {
    private String id_firm;
    private String title_firm;
    private String id_user;
    private String title_user;

    public FirmList() {
    }

    public FirmList(String id_firm, String title_firm, String id_user, String title_user) {
        this.id_firm = id_firm;
        this.title_firm = title_firm;
        this.id_user = id_user;
        this.title_user = title_user;
    }

    public String getId_firm() {
        return id_firm;
    }

    public void setId_firm(String id_firm) {
        this.id_firm = id_firm;
    }

    public String getTitle_firm() {
        return title_firm;
    }

    public void setTitle_firm(String title_firm) {
        this.title_firm = title_firm;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getTitle_user() {
        return title_user;
    }

    public void setTitle_user(String title_user) {
        this.title_user = title_user;
    }

    @Override
    public String toString() {
        return "FirmList{" +
                "id_firm='" + id_firm + '\'' +
                ", title_firm='" + title_firm + '\'' +
                ", id_user='" + id_user + '\'' +
                ", title_user='" + title_user + '\'' +
                '}';
    }
}
