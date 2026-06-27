package com.example.sencare.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public static Date parseDateTime(String date, String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
            return sdf.parse(date + " " + time);
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean isFutureBooking(String date, String time) {
        Date bookingDate = parseDateTime(date, time);
        if (bookingDate == null) return false;

        Date now = new Date();
        return bookingDate.after(now);
    }

    public static String formatDate(int year, int month, int day) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
    }

    public static String formatTime(int hour, int minute) {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }
}