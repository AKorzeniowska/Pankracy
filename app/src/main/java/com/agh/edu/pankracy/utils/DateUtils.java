package com.agh.edu.pankracy.utils;

import java.util.Date;

class DateUtils {
    static String getFormattedDate(long dateInMillis){
        // FIXME: Wrong date!
        Date time = new java.util.Date((long)dateInMillis);
        return time.toString();
    }
}
