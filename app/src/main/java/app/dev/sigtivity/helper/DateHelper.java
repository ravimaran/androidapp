package app.dev.sigtivity.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ravi on 11/12/2015.
 */
public class DateHelper {

    public static String getDateSpan(Date fromDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date now = parseDate(dateFormat.format(new Date()));
        return getDateSpan(fromDate, now);
    }
    public static String getDateSpan(String from){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date now = parseDate(dateFormat.format(new Date()));
        Date fromDate = parseDate(from);
        return getDateSpan(fromDate, now);
    }

    public static String getDateSpan(Date start, Date end){
        long difference = end.getTime() - start.getTime();
        long seconds = difference / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;

        if(weeks > 0){
            return  weeks + "W";
        }

        if(days > 0){
            return days + "d";
        }

        if(hours > 0){
            return hours + "h";
        }

        if(minutes > 0){
            return  minutes + "m";
        }

        if(seconds > 0){
            return  minutes + "s";
        }

        return "";
    }

    public static Date parseDate(String strDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            Date dateTaken = sdf.parse(strDate);
            return  dateTaken;
        }catch(ParseException ex){
            ex.printStackTrace();
        }

        return null;
    }
}
