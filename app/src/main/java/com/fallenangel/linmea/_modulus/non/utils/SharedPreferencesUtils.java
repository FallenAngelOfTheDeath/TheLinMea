/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 9:01 PM
 */

package com.fallenangel.linmea._modulus.non.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by NineB on 9/8/2017.
 */

public class SharedPreferencesUtils {
    public static final String APP_PREFERENCES = "SharedPreferencesUtils";
    public static String getFromSharedPreferences (Context context, String key, String id) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

//        SharedPreferences mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
//        String value = mSharedPreferences.getString(key + ":" + userName, null);
        return sharedPreferences.getString(key + ":" + id, null);
    }
    public static int getIntFromSharedPreferences (Context context, String key, String id) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

//        SharedPreferences mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
//        String value = mSharedPreferences.getString(key + ":" + userName, null);
        return sharedPreferences.getInt(key + ":" + id, -1);
    }
    public static Boolean getBollFromSharedPreferences (Context context, String key, String id){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

//        SharedPreferences mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
//        String value = mSharedPreferences.getString(key + ":" + userName, null);
        return  sharedPreferences.getBoolean(key + ":" + id, false);
    }

    public static void putToSharedPreferences(Context context, String key, String id, String value){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


       // SharedPreferences mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key + ":" + id, value);
        edit.commit();
    }

    public static void putToSharedPreferences(Context context, String key, String id, int value){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


       // SharedPreferences mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key + ":" + id, value);
        edit.commit();
    }

    public static void putToSharedPreferences(Context context, String key, String id, Boolean value){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


       // SharedPreferences mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key + ":" + id, value);
        edit.commit();
    }


}
