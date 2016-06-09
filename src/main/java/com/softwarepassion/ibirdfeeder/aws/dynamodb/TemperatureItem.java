package com.softwarepassion.ibirdfeeder.aws.dynamodb;

import java.util.Calendar;

public class TemperatureItem {
    private long timestamp;
    private double temperature;
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;

    public TemperatureItem(long timestamp, double temperature) {
        this.timestamp = timestamp;
        this.temperature = temperature;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        this.day = cal.get(Calendar.DAY_OF_MONTH);
        this.month = cal.get(Calendar.MONTH) + 1; //cos months in Calendar are counted from 0
        this.year = cal.get(Calendar.YEAR);
        this.hour = cal.get(Calendar.HOUR_OF_DAY);
        this.minute = cal.get(Calendar.MINUTE);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
