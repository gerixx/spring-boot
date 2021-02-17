package com.smec.cc.accountsmanagement;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeUtilTest {

    @Test
    public void toDayBeginUTC() throws ParseException {
        Date dateTime = DateTimeUtil.dformatExact.parse("2021-02-17T14:51:15.022+0000");
        Date dayExpected = DateTimeUtil.dformatExact.parse("2021-02-17T00:00:00.000+0000");

        // when
        long dayBeginTime = DateTimeUtil.toDayBeginUTC(dateTime.getTime());

        // then
        assertEquals(dayExpected, new Date(dayBeginTime));
    }

    @Test
    public void toDayAsStringUTC() throws ParseException {
        Date dateTime = DateTimeUtil.dformatExact.parse("2021-02-17T00:01:01.022+0000");
        assertEquals("2021-02-17", DateTimeUtil.toDayAsStringUTC(dateTime.getTime()));
    }
}