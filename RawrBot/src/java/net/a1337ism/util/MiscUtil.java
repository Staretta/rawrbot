package net.a1337ism.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MiscUtil {

    public static int randomNumber(int fileLength) {
        // Chooses a random number based on the length of a list.
        int randomNumber = (int) (Math.random() * fileLength);
        return randomNumber;
    }

    public static String randomSelection(String[] fileList) {
        // Chooses a random selection from a list of lines.
        String randomLine = fileList[randomNumber(fileList.length)];
        return randomLine;
    }

    public static String timeFormat(int oldTime) {
        // Make a pretty string (like this: "N days" or "N minutes" or "N hours", etc.)
        int seconds = (int) (System.currentTimeMillis() / 1000) - oldTime;
        String a_second;
        String formatTime = seconds + " " + (a_second = (seconds == 1) ? "second" : "seconds");

        int minutes = seconds / 60;
        if (minutes > 0) {
            String a_minute;
            formatTime = minutes + " " + (a_minute = (minutes == 1) ? "minute" : "minutes");
        }
        int hours = minutes / 60;
        if (hours > 0) {
            String an_hour;
            formatTime = hours + " " + (an_hour = (hours == 1) ? "hour" : "hours");
        }
        int days = hours / 24;
        if (days > 0) {
            String a_day;
            formatTime = days + " " + (a_day = (days == 1) ? "day" : "days");
        }

        // Return the pretty string.
        return formatTime;
    }

    public static String formatTime(long total_seconds) throws Exception {
        // Store the total seconds
        // long total_seconds = getSystemUptime() / 1000;

        // Helper variables.
        int MINUTE = 60;
        int HOUR = MINUTE * 60;
        int DAY = HOUR * 24;

        // Get the days, hours, etc.

        long days = total_seconds / DAY;
        long hours = (total_seconds % DAY) / HOUR;
        long minutes = (total_seconds % HOUR) / MINUTE;
        long seconds = total_seconds % MINUTE;

        // Build a pretty string. (like this: "N days, N hours, N minutes, N seconds")
        String uptime = "";
        if (days > 0) {
            String a_day;
            uptime += days + " " + (a_day = (days == 1) ? "day" : "days") + ", ";
        }
        if (uptime.length() > 0 || hours > 0) {
            String an_hour;
            uptime += hours + " " + (an_hour = (hours == 1) ? "hour" : "hours") + ", ";
        }
        if (uptime.length() > 0 || minutes > 0) {
            String a_minute;
            uptime += minutes + " " + (a_minute = (minutes == 1) ? "minute" : "minutes") + ", ";
        }
        String a_second;
        uptime += seconds + " " + (a_second = (seconds == 1) ? "second" : "seconds");

        // Return the ugly bastard.
        return uptime;
    }

    public static String dateFormat() {
        // Set up the date stuff. Seems a bit redundant though. Argh.
        String dateNow = null;
        Date date = new Date();
        dateNow = new SimpleDateFormat("MM/dd/yy HH:mm:ss").format(date);

        return dateNow;
    }

}