package it.unive.quadcore.smartmeal.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;

public class PermissionHandler {

    private static String[] getNearbyRequiredPermissions() {
        return new String[] {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
    }

    private static String[] getNotificationsRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
        } else {
            return new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }
    }

    private static String[] getSensorsRequiredPermissions() {
        return new String[] {
                // TODO capire che permessi servono
        };
    }

    private static String[] getAllRequiredPermissions() {
        ArrayList<String> requiredPermissions = new ArrayList<>();
        requiredPermissions.addAll(Arrays.asList(getNearbyRequiredPermissions()));
        requiredPermissions.addAll(Arrays.asList(getNotificationsRequiredPermissions()));
        requiredPermissions.addAll(Arrays.asList(getSensorsRequiredPermissions()));

        return requiredPermissions.toArray(new String[0]);
    }

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    public static int getRequestCodeRequiredPermissions() {
        return REQUEST_CODE_REQUIRED_PERMISSIONS;
    }

    public static boolean hasNearbyPermissions(Context context) {
        return hasPermission(
                context,
                getNearbyRequiredPermissions()
        );
    }

    public static void requestNearbyPermissions(Activity activity) {
        requestPermissions(
                activity,
                getNearbyRequiredPermissions()
        );
    }

    public static boolean hasNotificationsPermissions(Context context) {
        return hasPermission(
                context,
                getNotificationsRequiredPermissions()
        );
    }

    public static void requestNotificationsPermissions(Activity activity) {
        requestPermissions(
                activity,
                getNotificationsRequiredPermissions()
        );
    }

    public static boolean hasSensorsPermissions(Context context) {
        return hasPermission(
                context,
                getSensorsRequiredPermissions()
        );
    }

    public static void requestSensorsPermissions(Activity activity) {
        requestPermissions(
                activity,
                getSensorsRequiredPermissions()
        );
    }

    public static void requestAllPermissions(Activity activity) {
        requestPermissions(
                activity,
                getAllRequiredPermissions()
        );
    }

    private static boolean hasPermission(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static void requestPermissions(Activity activity, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(activity, permissions)) {
                activity.requestPermissions(permissions, REQUEST_CODE_REQUIRED_PERMISSIONS);
            }
        }
    }

}
