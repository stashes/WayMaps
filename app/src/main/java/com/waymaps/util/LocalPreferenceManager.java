package com.waymaps.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Admin on 28.01.2018.
 */

public class LocalPreferenceManager {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String SAVE_PASSWORD = "savePassword";

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final double DEFAULT_LATITUDE = 52.00d;
    private static final double DEFAULT_LONGITUDE = 38.00d;

    private static final String ZOOM = "zoom";
    private static final int DEFAULT_ZOOM = 4;

    private static final String FILTER_OBJECTS = "filterObjects";
    private static final int INVALID_INT = -1;
    private static final String INVALID_STRING = "";
    private static final String MAP_PROVIDER = "mapProvider";

    private static final String SHOW_EXCEEDS = "showExceeds";
    private static final String SHOW_OFFLINE_MARKERS = "showOfflineMarkers";
    private static final String SHOW_PARKINGS = "showParkings";

    private LocalPreferenceManager() {
    }

    public static void saveUsername(Context context, String username) {
        setStringValue(context, USERNAME, username);
    }

    public static void savePassword(Context context, String password) {
        setStringValue(context, PASSWORD, password);
    }

    public static String getUsername(Context context) {
        return getStringValue(context, USERNAME);
    }

    public static String getPassword(Context context) {
        return getStringValue(context, PASSWORD);
    }

    public static void clearPassword(Context context) {
        clearValue(context, PASSWORD);
    }

    public static void setSavePassword(Context context, boolean value) {
        setBooleanValue(context, SAVE_PASSWORD, value);
    }

    public static boolean getSavePassword(Context context) {
        return getBooleanValue(context, SAVE_PASSWORD);
    }

    public static void saveLatLonZoom(Context context, double latitude, double longitude, float zoom) {
        setDoubleValue(context, LATITUDE, latitude);
        setDoubleValue(context, LONGITUDE, longitude);
        setFloatValue(context, ZOOM, zoom);
    }

    public static double getLatitude(Context context) {
        Double latitude = getDoubleValue(context, LATITUDE);
        return latitude == null ? DEFAULT_LATITUDE : latitude.doubleValue();
    }

    public static double getLongitude(Context context) {
        Double longitude = getDoubleValue(context, LONGITUDE);
        return longitude == null ? DEFAULT_LONGITUDE : longitude.doubleValue();
    }


    public static void saveOptions(Context context, boolean showExceeds, boolean showParkings) {
        setBooleanValue(context, SHOW_EXCEEDS, showExceeds);
        setBooleanValue(context, SHOW_PARKINGS, showParkings);
    }

    public static boolean shouldShowParkings(Context context) {
        return getBooleanValue(context, SHOW_PARKINGS);
    }

    public static boolean shouldShowExceeds(Context context) {
        return getBooleanValue(context, SHOW_EXCEEDS);
    }

    public static void clearOptions(Context context) {
        clearValue(context, SHOW_EXCEEDS);
        clearValue(context, SHOW_PARKINGS);
    }

    public static void setFilterObjects(Context context, boolean filter) {
        setBooleanValue(context, FILTER_OBJECTS, filter);
    }

    public static boolean shoulFilterObjects(Context context) {
        return getBooleanValue(context, FILTER_OBJECTS);
    }

    public static void clearFilterFlag(Context context) {
        clearValue(context, FILTER_OBJECTS);
    }

    public static String getMapProvider(Context context) {
        return getStringValue(context, MAP_PROVIDER);
    }

    public static void setMapProvider(Context context, String mapProvider) {
        setStringValue(context, MAP_PROVIDER, mapProvider);
    }

    public static void setShowOfflineMarkers(Context context, boolean sholdShowOffline) {
        setBooleanValue(context, SHOW_OFFLINE_MARKERS, sholdShowOffline);
    }

    public static boolean shouldShowOfflineMarkers(Context context) {
        return getBooleanValue(context, SHOW_OFFLINE_MARKERS, true);
    }

    private static void clearValue(Context context, String key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.remove(key);
        editor.commit();
    }

    private static void setStringValue(Context context, String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getStringValue(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, INVALID_STRING);
    }

    private static void setBooleanValue(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private static boolean getBooleanValue(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    private static boolean getBooleanValue(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

    private static void setDoubleValue(Context context, String key, double value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }

    private static Double getDoubleValue(Context context, String key) {
        String doubleValue = PreferenceManager.getDefaultSharedPreferences(context).getString(key, INVALID_STRING);
        if (INVALID_STRING.equals(doubleValue)) {
            return null;
        }
        return Double.valueOf(Double.parseDouble(doubleValue));
    }

    private static void setIntValue(Context context, String key, int value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private static int getIntValue(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, -1);
    }

    private static void setFloatValue(Context context, String key, float value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putFloat(key, value);
        editor.commit();
    }

}
