package com.fallenangel.linmea.linmea.user.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by NineB on 9/8/2017.
 */

public class SharedPreferencesUtils {
    public static final String APP_PREFERENCES = "SharedPreferencesUtils";
    public static String getFromSharedPreferences (Context context, String key, String userName){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

//        SharedPreferences mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
//        String value = mSharedPreferences.getString(key + ":" + userName, null);
        return  sharedPreferences.getString(key + ":" + userName, null);
    }

    public static void putToSharedPreferences(Context context, String key, String userName, String value){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


       // SharedPreferences mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key + ":" + userName, value);
        edit.commit();
    }
}
