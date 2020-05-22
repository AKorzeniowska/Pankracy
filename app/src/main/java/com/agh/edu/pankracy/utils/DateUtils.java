package com.agh.edu.pankracy.utils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ITALIAN); //losowe Italian

    static String getFormattedDate(long dateInMillis){
        // FIXME: Wrong date!
        Date time = new java.util.Date((long)dateInMillis);
        return time.toString();
    }

    public static long getNumberOfDaysBetweenGivenDateAndNextWateringMyGodThatsALongAssMethodName(Date givenDate, Date lastWatering, int wateringFrequency){
        Calendar c = Calendar.getInstance();
        c.setTime(lastWatering);
        c.add(Calendar.DATE, wateringFrequency);
        Date nextWatering = c.getTime();
        long days = ChronoUnit.DAYS.between(parseDateToLocalDateTime(givenDate), parseDateToLocalDateTime(nextWatering));
        return days;
    }

    static LocalDateTime parseDateToLocalDateTime(Date date){
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
