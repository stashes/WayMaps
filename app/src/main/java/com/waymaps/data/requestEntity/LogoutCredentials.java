package com.waymaps.data.requestEntity;

/**
 * Created by Admin on 03.02.2018.
 */

public class LogoutCredentials extends Action {

    private String identificator;
    private String userId;

    public LogoutCredentials(String action) {
        super(action);
    }

    public LogoutCredentials(String action, String identificator, String userId) {
        super(action);
        this.identificator = identificator;
        this.userId = userId;
    }

    public String getIdentificator() {
        return identificator;
    }

    public void setIdentificator(String identificator) {
        this.identificator = identificator;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
