package com.practice.solarcalculator.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

class PermissionUtils {

    private static boolean useRunTimePermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private static boolean hasPermission(Context context, String permission) {
        if (useRunTimePermissions()) {
            return ActivityCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    static boolean hasPermissions(Context context, String... permissions) {
        if (useRunTimePermissions() && context != null && permissions != null) {
            for (String permission : permissions) {
                if (!hasPermission(context, permission))
                    return false;
            }
        }
        return true;
    }
}

