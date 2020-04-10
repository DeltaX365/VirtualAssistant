package com.vnsolution.virtualassistant.db.convertor;

import androidx.room.TypeConverter;

public class BooleanConverter {
    @TypeConverter
    public static Boolean toBoolean(int value) {
        return (value == 1) ? true : false;
    }

    @TypeConverter
    public static int toInt(Boolean value) {
        return value ? 1 : 0;
    }
}
