package com.camellya.gbt32960_3_tcp_server.protocol;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Getter
public class TimeModel {

    private final Byte year;
    private final Byte month;
    private final Byte day;
    private final Byte hour;
    private final Byte minute;
    private final Byte second;

    public TimeModel(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        year = (byte) (calendar.get(Calendar.YEAR) - 2000);
        month = (byte) (calendar.get(Calendar.MONTH) + 1);
        day = (byte) (calendar.get(Calendar.DAY_OF_MONTH));
        hour = (byte) (calendar.get(Calendar.HOUR_OF_DAY));
        minute = (byte) (calendar.get(Calendar.MINUTE));
        second = (byte) (calendar.get(Calendar.SECOND));
    }

    public TimeModel(List<Byte> bytes) {
        if (bytes.size() < 6) {
            year = null;
            month = null;
            day = null;
            hour = null;
            minute = null;
            second = null;
            return;
        }
        year = bytes.get(0);
        month = bytes.get(1);
        day = bytes.get(2);
        hour = bytes.get(3);
        minute = bytes.get(4);
        second = bytes.get(5);
    }

    @Override
    public String toString() {
        return String.format("20%02d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
    }

    public static List<Byte> getNow() {
        List<Byte> bytes = new ArrayList<>(6);
        Calendar instance = Calendar.getInstance();
        bytes.add((byte) (2000 - instance.get(Calendar.YEAR)));
        bytes.add((byte) (instance.get(Calendar.MONTH) + 1));
        bytes.add((byte) (instance.get(Calendar.DAY_OF_MONTH)));
        bytes.add((byte) (instance.get(Calendar.HOUR_OF_DAY)));
        bytes.add((byte) (instance.get(Calendar.MINUTE)));
        bytes.add((byte) (instance.get(Calendar.SECOND)));
        return bytes;
    }

    public List<Byte> getBytes() {
        List<Byte> bytes = new ArrayList<>(6);
        bytes.add(year);
        bytes.add(month);
        bytes.add(day);
        bytes.add(hour);
        bytes.add(minute);
        bytes.add(second);
        return bytes;
    }

    public Date getDate() {
        Calendar instance = Calendar.getInstance();
        instance.set(2000 + year, month - 1, day, hour, minute, second);
        return instance.getTime();
    }

}
