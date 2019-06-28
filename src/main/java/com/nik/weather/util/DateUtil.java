package com.nik.weather.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Boolean isDateBeforeToday(Date date) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date today = c.getTime();
        return date.before(today);
    }
}
