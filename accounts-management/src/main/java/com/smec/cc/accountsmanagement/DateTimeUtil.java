package com.smec.cc.accountsmanagement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {
    public static final SimpleDateFormat dformatExact = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); // example: "2021-02-17T14:51:15.022+0100"
    public static final SimpleDateFormat dformatDayUTC = new SimpleDateFormat("yyyy-MM-dd");

    static  {
        dformatDayUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static final long millisOfDay = 24 * 60 * 60 * 1000;

    public static long toDayBeginUTC(Long timestamp) {
        return millisOfDay * (timestamp / millisOfDay);
    }

    public static Date parseDateFrom(String dateTime) throws ParseException {
        return dformatExact.parse(dateTime);
    }

    public static String toDayAsStringUTC(Long dayInMills) {
        return dformatDayUTC.format(new Date(dayInMills));
    }
}
