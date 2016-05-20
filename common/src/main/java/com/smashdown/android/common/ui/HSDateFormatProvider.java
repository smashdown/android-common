package com.smashdown.android.common.ui;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class HSDateFormatProvider {

    public static SimpleDateFormat simpleDateFormat    = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat storeIdleTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    // Android DateTime
    public static DateFormat DF_DATE_MEDIUM = DateFormat.getDateInstance(DateFormat.MEDIUM);
    public static DateFormat DF_DATE_SHORT  = DateFormat.getDateInstance(DateFormat.SHORT);
    public static DateFormat DF_DATE_LONG   = DateFormat.getDateInstance(DateFormat.LONG);
    ;

    public static DateFormat DF_TIME_SHORT_01  = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    public static DateFormat DF_TIME_SHORT_02  = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    public static DateFormat DF_TIME_SHORT_03  = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
    public static DateFormat DF_TIME_MEDIUM_01 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    public static DateFormat DF_TIME_MEDIUM_02 = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    public static DateFormat DF_TIME_MEDIUM_03 = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
    public static DateFormat DF_TIME_LONG_01   = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
    public static DateFormat DF_TIME_LONG_02   = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG);
    public static DateFormat DF_TIME_LONG_03   = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);

    // JodaTime DateFormat
    public static DateTimeFormatter DTF_DATE_FULL   = DateTimeFormat.fullDate();
    public static DateTimeFormatter DTF_DATE_MEDIUM = DateTimeFormat.mediumDate();
    public static DateTimeFormatter DTF_DATE_SHORT  = DateTimeFormat.shortDate();

    public static DateTimeFormatter DTF_TIME_FULL   = DateTimeFormat.fullTime();
    public static DateTimeFormatter DTF_TIME_MEDIUM = DateTimeFormat.mediumTime();
    public static DateTimeFormatter DTF_TIME_SHORT  = DateTimeFormat.shortTime();

    public static DateTimeFormatter DTF_DATE_TIME_FULL   = DateTimeFormat.fullDateTime();
    public static DateTimeFormatter DTF_DATE_TIME_MEDIUM = DateTimeFormat.mediumDateTime();
    public static DateTimeFormatter DTF_DATE_TIME_SHORT  = DateTimeFormat.shortDateTime();

}
