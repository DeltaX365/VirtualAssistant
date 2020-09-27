package com.vnsolution.virtualassistant.db.convertor;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Date toDate(String dateFormatted) {
        if(dateFormatted != null && !dateFormatted.isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return formatter.parse(dateFormatted);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return new Date();
    }

    @TypeConverter
    public static String toString(Date date) {
        if(date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(date);
        }
        return "";
    }
}
