package com.waymaps.data.requestEntity;

/**
 * Created by Admin on 04.02.2018.
 */

public class UpdateCredentials extends Action {
    private String user_id;
    private String identificator;
    private String os;

    public UpdateCredentials(String action) {
        super(action);
    }

    public UpdateCredentials(String action, String user_id, String identificator, String os) {
        super(action);
        this.user_id = user_id;
        this.identificator = identificator;
        this.os = os;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIdentificator() {
        return identificator;
    }

    public void setIdentificator(String identificator) {
        this.identificator = identificator;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
