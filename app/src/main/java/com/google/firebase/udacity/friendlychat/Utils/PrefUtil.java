package com.google.firebase.udacity.friendlychat.Utils;



import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by DMI on 20-06-2017.
 */

public class PrefUtil {

    // shared preferences key names
    public static final String LOGIN_STATUS = "LOGIN_STATUS";
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_STATUS = "USER_STATUS";

    /*write data to all shared preference using single method*/
    public static void saveLoginDetails(Context context, String userId, String email, String username) {
        setLoginStatus(context, true);
        setUsername(context, username);
        setUserId(context, userId);
        setEmail(context, email);
    }

    public static String getStatus(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String restoredText = prefs.getString(USER_STATUS, "Hey There I'm Using Firechat");
        return restoredText;
    }

    public static void setStatus(Context context, String data) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putString(USER_STATUS, data).commit();
    }

    /*get the above fields directly*/
    /*LOGIN STATUS*/
    public static boolean getLoginStatus(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean restoredText = prefs.getBoolean(LOGIN_STATUS, false);
        return restoredText;
    }

    public static void setLoginStatus(Context context, boolean data) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putBoolean(LOGIN_STATUS, data).commit();
    }

    /*USER ID*/
    public static void setUserId(Context context, String data) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putString(USER_ID, data).commit();
    }

    public static String getUserId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String restoredText = prefs.getString(USER_ID, null);
        return restoredText;
    }

    /*USERNAME*/
    public static void setUsername(Context context, String data) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putString(USER_NAME, data).commit();
    }

    public static String getUsername(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String restoredText = prefs.getString(USER_NAME, null);
        return restoredText;
    }

    /*USER EMAIL*/
    public static void setEmail(Context context, String data) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putString(USER_EMAIL, data).commit();
    }

    public static String getEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String restoredText = prefs.getString(USER_EMAIL, null);
        return restoredText;
    }


    // default shared preferences
    public static void saveStringPref(Context context, String keyName, String data) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putString(keyName, data).commit();
    }

    //reading shared preference data
    public static String readStringPref(Context context, String keyName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String restoredText = prefs.getString(keyName, null);
        return restoredText;
    }

    /*integer preferences*/
    // default shared preferences
    public static void saveIntPref(Context context, String keyName, int data) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putInt(keyName, data).commit();
    }

    //reading shared preference data
    public static int readIntPref(Context context, String keyName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int restoredText = prefs.getInt(keyName, 0);
        return restoredText;
    }

    /*string shared preferences*/
    // default shared preferences
    public static void saveBoolPref(Context context, String keyName, boolean data) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putBoolean(keyName, data).commit();
    }

    //reading shared preference data
    public static boolean readBoolPref(Context context, String keyName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean restoredText = prefs.getBoolean(keyName, false);
        return restoredText;
    }

    public static void clearSharedPreferences(Context context) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.clear().commit();
    }
}

