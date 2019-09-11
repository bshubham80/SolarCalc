package com.practice.solarcalculator.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

public class DateUtils {

    /**
     * Convert date object into representational string format.
     *
     * @param date   object for formatting
     * @param format string format like dd-MMM-yyy, mm-dd-yyyy
     * @return the formatted time string.
     */
    public static String convertDateFormat(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static double getSunTime(boolean sunSet, Date obj, double latitude, double longitude) {
        float day = obj.getDay() + 1;
        float month = obj.getMonth() + 1;
        float year = 1900 + obj.getYear();

        double N1 = floor(275 * month / 9);
        double N2 = floor((month + 9) / 12);
        double N3 = (1 + floor((year - 4 * floor(year / 4) + 2) / 3));
        double N = N1 - (N2 * N3) + day - 30;

        double lngHour = longitude / 15;
        double t;
        if (!sunSet)
            t = N + ((6 - lngHour) / 24);
        else
            t = N + ((18 - lngHour) / 24);

        double M = (0.9856 * t) - 3.289;
        double L = M + (1.916 * sin(M)) + (0.020 * sin(2 * M)) + 282.634;

        double RA = atan(0.91764 * tan(L));

        double Lquadrant = (floor(L / 90)) * 90;
        double RAquadrant = (floor(RA / 90)) * 90;
        RA = RA + (Lquadrant - RAquadrant);

        // right ascension value needs to be converted into hours
        RA = RA / 15;

        // calculate the Sun's declination
        double sinDec = 0.39782 * sin(L);
        double cosDec = cos(asin(sinDec));

        // calculate the Sun's local hour angle
        double cosH = (cos(90+ 5.0/6.0) - (sinDec * sin(latitude))) / (cosDec * cos(latitude));

        if (cosH > 1) {
            Logger.info("the sun never rises on this location(on the specified date)");
        }

        if (cosH < -1) {
            Logger.info("the sun never sets on this location(on the specified date)");
        }

        // finish calculating H and convert into hours
        double H;
        if (!sunSet) {
            H = 360 - acos(cosH);
        } else {
            H = acos(cosH);
        }
        H = H / 15;

        //calculate local mean time of rising/setting
        double T = H + RA - (0.06571 * t) - 6.622;

        // adjust back to UTC
        double UT = T - lngHour;

        // convert UT value to local time zone of latitude/longitude
        return UT + 5.30;
    }
}
