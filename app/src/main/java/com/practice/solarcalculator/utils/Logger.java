package com.practice.solarcalculator.utils;

import android.util.Log;

public class Logger {
    private static final String TAG = "Solar-Calc";

    public static void info(String msg, Object... args) {
        Log.i(TAG, String.format(msg, args));
    }

    public static void error(String msg, Object... args) {
        Log.e(TAG, String.format(msg, args));
    }

}
