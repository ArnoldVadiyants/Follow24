package com.newstee.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Arnold on 03.05.2016.
 */
public class MPUtilities {
    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy, HH:mm";
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String TIME_FORMAT = "HH:mm";
    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
int value=  percentage.intValue();
      if(value <=1)
      {
          return 1;
      }
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }
    public String getDateTimeFormat(long milliseconds)
    {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        dateFormat.setTimeZone(tz);
        Date d = new Date(milliseconds);
        String dateForView = dateFormat.format(d);
        return dateForView;
    }
    public String getDateTimeFormat(String seconds)
    { long l;
        try {
            l =  Long.parseLong(seconds)* 1000L;
        }catch (NumberFormatException e)
        {
            return "";
        }

        return getDateTimeFormat(l);
    }

    public String getDateOrTimeFormat(long milliseconds) {
        Calendar calToday = Calendar.getInstance();
        TimeZone tz = calToday.getTimeZone();
        Calendar calYesterday = Calendar.getInstance(); // today
        calYesterday.add(Calendar.DAY_OF_YEAR, -1); // yesterday
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        dateFormat.setTimeZone(tz);
        Date d = new Date(milliseconds);
        String dateForView = dateFormat.format(d);
        Calendar cal2 = Calendar.getInstance();
        try {
            Date d2 = dateFormat.parse(dateForView);
            cal2.setTime(d2);
        } catch (ParseException e) {
            cal2.setTime(d);
        }
        SimpleDateFormat dateFormat1;
        if (calToday.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && calToday.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
            dateFormat1 = new SimpleDateFormat(TIME_FORMAT);
        } else if (calYesterday.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && calYesterday.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
            dateFormat1 = new SimpleDateFormat(DATE_TIME_FORMAT);
        } else {
            dateFormat1 = new SimpleDateFormat(DATE_FORMAT);
        }
        dateFormat1.setTimeZone(tz);
        return dateFormat1.format(d);

    }

    public String getDateOrTimeFormat(String seconds) {
        long l;
        try {
            l = Long.parseLong(seconds) * 1000L;
        } catch (NumberFormatException e) {
            return "";
        }

        return getDateOrTimeFormat(l);
    }
}