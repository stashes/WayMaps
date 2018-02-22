package com.waymaps.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by Admin on 05.02.2018.
 */

public class DateTimeUtil {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String dateToString(Date date){
        return dateFormat.format(date);
    }

    public static String dateToStringWrapQuates(Date date){
        return "\'" + dateFormat.format(date) + "\'";
    }
}
