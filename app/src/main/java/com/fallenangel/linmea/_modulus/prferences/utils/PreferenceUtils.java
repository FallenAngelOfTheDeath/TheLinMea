package com.fallenangel.linmea._modulus.prferences.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey;
import com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode;

import static com.fallenangel.linmea._linmea.util.SharedPreferencesUtils.APP_PREFERENCES;

/**
 * Created by NineB on 1/19/2018.
 */

public class PreferenceUtils {

    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    public static String getStringPreference(Context context, PreferenceMode mode, PreferenceKey key) {
        sp = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return sp.getString(mode + ":" + key, null);
    }

    public static Boolean getBooleanPreference(Context context, PreferenceMode mode, PreferenceKey key) {
        sp = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return sp.getBoolean(mode + ":" + key, false);
    }

    public static int getIntPreference(Context context, PreferenceMode mode, PreferenceKey key) {
        sp = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return sp.getInt(mode + ":" + key, -1);
    }

    public static Long getLongPreference(Context context, PreferenceMode mode, PreferenceKey key) {
        sp = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return sp.getLong(mode + ":" + key, -1);
    }

    public static Float getFloatPreference(Context context, PreferenceMode mode, PreferenceKey key) {
        sp = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return sp.getFloat(mode + ":" + key, -1);
    }

    public static void putPreference(Context context, PreferenceMode mode, PreferenceKey key, Boolean value){
        sp = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putBoolean(mode + ":" + key, value);
        editor.apply();
    }

    public static void putPreference(Context context, PreferenceMode mode, PreferenceKey key, int value){
        sp = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putInt(mode + ":" + key, value);
        editor.apply();
    }

    public static void putPreference(Context context, PreferenceMode mode, PreferenceKey key, String value){
        sp = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString(mode + ":" + key, value);
        editor.apply();
    }

    public static void putPreference(Context context, PreferenceMode mode, PreferenceKey key, Long value){
        sp = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putLong(mode + ":" + key, value);
        editor.apply();
    }

    public static void putPreference(Context context, PreferenceMode mode, PreferenceKey key, Float value){
        sp = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putFloat(mode + ":" + key, value);
        editor.apply();
    }

}
