package com.waymaps.data.requestEntity.parameters;

import com.waymaps.util.DateTimeUtil;

import java.util.Date;

/**
 * Created by Admin on 05.02.2018.
 */

public class StartEndDate extends Parameter {
    private Date startDate;
    private Date endDate;


    public StartEndDate() {
    }

    public StartEndDate(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return DateTimeUtil.dateToStringWrapQuates(startDate) + ", " +
                DateTimeUtil.dateToStringWrapQuates(endDate);
    }

    @Override
    public String getParameters() {
        return toString();
    }
}
