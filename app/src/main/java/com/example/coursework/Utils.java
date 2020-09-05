package com.example.coursework;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility methods for turning the date to time format and returning the country the device is located in.
 */

public class Utils {

    //Finds the country that the device is located in.
    public static String getCountry(){
        Locale locale = Locale.getDefault();
        String country = String.valueOf(locale.getCountry());
        return country.toLowerCase();
    }

    //Changes the string date, and returns it as a time to be used on the article.
    public static String DateToTimeFormat(String oldStringDate){
        PrettyTime prettyTime = new PrettyTime(new Locale(getCountry()));
        String time = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.ENGLISH);
            Date date = sdf.parse(oldStringDate);
            time = prettyTime.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

}
