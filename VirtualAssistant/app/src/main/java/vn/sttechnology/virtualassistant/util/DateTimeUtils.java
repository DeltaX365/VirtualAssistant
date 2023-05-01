package vn.sttechnology.virtualassistant.util;

import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {
    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static String formatElapsedTime(long elapsedSeconds) {
        long hours = elapsedSeconds / 3600;
        long minutes = (elapsedSeconds % 3600) / 60;
        long seconds = elapsedSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
