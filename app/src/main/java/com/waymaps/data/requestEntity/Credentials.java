package com.waymaps.data.requestEntity;


/**
 * Created by Admin on 27.01.2018.
 */

public class Credentials extends Action {

    private String login;
    private String pass;
    private String identificator;
    private String os;
    private String format;

    public Credentials(String action, String login, String pass, String identificator, String os, String format) {
        super(action);
        this.login = login;
        this.pass = pass;
        this.identificator = identificator;
        this.os = os;
        this.format = format;
    }

    public Credentials(String action) {
        super(action);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
