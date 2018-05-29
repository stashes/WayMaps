package com.waymaps.data.responseEntity;


/**
 * Created by Admin on 27.01.2018.
 */

public class User {


    private String id;

    private String firm_id;

    private String firm_blocked;

    private String manager;

    private String diler;

    private String user_title;

    private String login;

    private String firm_title;

    private String saldo;

    private String currency;

    private String unread_ticket;

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirm_id() {
        return firm_id;
    }

    public void setFirm_id(String firm_id) {
        this.firm_id = firm_id;
    }

    public String getFirm_blocked() {
        return firm_blocked;
    }

    public void setFirm_blocked(String firm_blocked) {
        this.firm_blocked = firm_blocked;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getDiler() {
        return diler;
    }

    public void setDiler(String diler) {
        this.diler = diler;
    }

    public String getUser_title() {
        return user_title;
    }

    public void setUser_title(String user_title) {
        this.user_title = user_title;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirm_title() {
        return firm_title;
    }

    public void setFirm_title(String firm_title) {
        this.firm_title = firm_title;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnread_ticket() {
        return unread_ticket;
    }

    public void setUnread_ticket(String unread_ticket) {
        this.unread_ticket = unread_ticket;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firm_id='" + firm_id + '\'' +
                ", firm_blocked='" + firm_blocked + '\'' +
                ", manager='" + manager + '\'' +
                ", diler='" + diler + '\'' +
                ", user_title='" + user_title + '\'' +
                ", login='" + login + '\'' +
                ", firm_title='" + firm_title + '\'' +
                ", saldo='" + saldo + '\'' +
                ", currency='" + currency + '\'' +
                ", unread_ticket='" + unread_ticket + '\'' +
                '}';
    }
}