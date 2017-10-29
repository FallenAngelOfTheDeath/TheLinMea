package com.fallenangel.linmea.linmea.startup;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.fallenangel.linmea.linmea.ui.MainActivity;
import com.fallenangel.linmea.linmea.user.authentication.user;
import com.fallenangel.linmea.linmea.user.utils.SharedPreferencesUtils;
import com.fallenangel.linmea.linmea.utils.image.ImageUplDwnlUtils;

import static com.fallenangel.linmea.linmea.utils.image.ImageUplDwnlUtils.saveCurrentFireMD5OfImageFromServer;
import static com.fallenangel.linmea.linmea.utils.info.NetworkConnection.hasConnection;

/**
 * Created by NineB on 9/29/2017.
 */

public class StartUpAsyncTask extends AsyncTask<Object, Object, Object> {

    String TAG = "StartUpAsyncTask";
    Context mContext;
    String mFirstStart;

    public StartUpAsyncTask(Context context, String firstStart) {
        mContext = context;
        mFirstStart = firstStart;
    }

    @Override
    protected Object doInBackground(Object... params) {
       Context context = (Context) params[0];
       String firstStart = (String) params[1];
        startUp(context, firstStart);
       return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(TAG, "onPreExecute: started");
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        finishAsyncTask(mContext, mFirstStart);
        Log.i(TAG, "onPreExecute: completed");
    }




    private Object startUp (Context context, String mFirstStart) {
        if(user.getCurrentUser() != null){
            saveCurrentFireMD5OfImageFromServer(context, user.getCurrentUserUID(), "FireMD5");
        }


        ImageUplDwnlUtils.downloadImage(context,user.getCurrentUserUID(), null);



       //context.startService(new Intent(context, UpdateUserData.class));



        return null;
    }


    private void finishAsyncTask (Context context, String mFirstStart) {
        SharedPreferencesUtils.getFromSharedPreferences(context, "FIRST_START", "");
        if (mFirstStart == null){
            DefaultSettings.setDefaultSettings();
            SharedPreferencesUtils.putToSharedPreferences(context, "FIRST_START", "", "false");
            Intent firstStart = new Intent(context, FirstStartActivity.class);
            context.startActivity(firstStart);
        } else {

            if (hasConnection(context) == true){
                Intent mainActivity = new Intent(context, MainActivity.class);
                context.startActivity(mainActivity);

            } else {
                Intent mainActivity = new Intent(context, MainActivity.class);
                context.startActivity(mainActivity);
            }

        }
    }

}
