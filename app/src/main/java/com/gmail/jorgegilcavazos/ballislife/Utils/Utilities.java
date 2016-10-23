package com.gmail.jorgegilcavazos.ballislife.Utils;

public final class Utilities {

    public static String getPeriodString(String periodValue, String periodName) {
        if (periodValue.equals("")) {
            return "";
        }
        int period = Integer.parseInt(periodValue);
        int overtimePeriod = period - 4;
        if (period <= 4) {
            return period + " " + periodName;
        } else {
            return periodName + overtimePeriod;
        }


    }
}
