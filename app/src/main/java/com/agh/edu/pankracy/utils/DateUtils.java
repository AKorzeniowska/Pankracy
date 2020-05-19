package com.agh.edu.pankracy.utils;

import java.util.Date;

public class DateUtils {
    static final long HOUR = 3600;
    public static int TIMEZONE_OFFSET = 0;

    static Date getDateFromMillis(long dateInMillis){
        return new java.util.Date((dateInMillis + TIMEZONE_OFFSET) * 1000);
    }

    static String getFormattedDate(long dateInMillis){
        return getDateFromMillis(dateInMillis).toString();
    }
}
