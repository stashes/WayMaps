package com.waymaps.util;

import android.content.Context;

import com.waymaps.R;

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

    public static String getDiffBetweenDate(Date date1, Date date2,Context context){
        long diff = (date1.getTime() - date2.getTime());
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000) % 1000;

        return new String((diffDays == 0 ? "" : (diffDays + context.getResources().getString(R.string.day) + " ")) +
                (diffHours == 0 ? "" : (diffHours + context.getResources().getString(R.string.hour) + " ")) +
                (diffMinutes == 0 ? "" : (diffMinutes + context.getResources().getString(R.string.minute) + " ")) +
                (diffSeconds == 0 ? "" : (diffSeconds + context.getResources().getString(R.string.second))));
    }
}
