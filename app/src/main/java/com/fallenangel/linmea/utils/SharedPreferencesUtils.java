package com.fallenangel.linmea.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by NineB on 9/8/2017.
 */

public class SharedPreferencesUtils {

    SharedPreferences mSharedPreferences;

    public String getFromSharedPreferences (Activity activity, String key, String userName){
        mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        String value = mSharedPreferences.getString(key + ":" + userName, "");
        return value;
    }

    public void putToSharedPreferences(Activity activity, String key, String userName, String value){
        mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(key + ":" + userName, value);
        edit.commit();
    }
}
