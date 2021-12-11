package com.randeztrying.sil.Helpers;

import java.util.Calendar;

public class StaticHelper {

    public static String getCoolerTime(String mills, boolean returnTime) {
        long timeInMilliseconds = Long.parseLong(mills);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMilliseconds);

        int d = c.get(Calendar.DAY_OF_MONTH);
        int mo = c.get(Calendar.MONTH) + 1;
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);

        String min;
        if (m < 10) min = "0" + m;
        else min = String.valueOf(m);

        String month = "";
        switch (mo) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
        }

        if (returnTime) {
            return h + ":" + min;
        } else {
            return d + " " + month;
        }
    }
}
