package com.waymaps.data.responseEntity;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Admin on 05.02.2018.
 */

public class FinGet {
    private String date;
    private String debet;
    private String credit;
    private String balance;

    public FinGet() {

    }

    public FinGet(String date, String debet, String credit, String balance) {
        this.date = date;
        this.debet = debet;
        this.credit = credit;
        this.balance = balance;
    }

    public String getDate() {
        return date;
    }

    @JsonSetter("date(for_date)")
    public void setDate(String date) {
        this.date = date;
    }

    public String getDebet() {
        return debet;
    }

    public void setDebet(String debet) {
        this.debet = debet;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getBalance() {
        return balance;
    }

    @JsonSetter("balans")
    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "FinGet{" +
                "date='" + date + '\'' +
                ", debet='" + debet + '\'' +
                ", credit='" + credit + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }
}
