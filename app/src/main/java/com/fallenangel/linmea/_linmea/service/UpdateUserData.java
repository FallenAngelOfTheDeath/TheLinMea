package com.fallenangel.linmea._linmea.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fallenangel.linmea.interfaces.OnResultListener;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._linmea.util.SharedPreferencesUtils;
import com.fallenangel.linmea.linmea.utils.image.ImageUplDwnlUtils;

import java.io.File;

public class UpdateUserData extends Service {

    public Thread mThread;
    private final static String TAG = "GetUserDataAsyncTask";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG, "onStart: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateLoop(UpdateUserData.this);
        Log.i(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    private void updateLoop(final Context context){
        Log.i(TAG, "updateLoop: ");
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(180000);
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                        getUserDataFromFire(UpdateUserData.this);
                }
            }
        });

        mThread.setDaemon(true);
        mThread.start();
    }


        public static void getUserDataFromFire (final Context context) {
            final Uri[] imgUri = {null};
            if (User.getCurrentUser() != null){
            if (User.getCurrentUser().getPhotoUrl() != null) {
                final String savedMD5 = SharedPreferencesUtils.getFromSharedPreferences(context, "FireMD5", User.getCurrentUserUID());
                final String[] fireMD5 = {null};
                ImageUplDwnlUtils.getMD5ImagesFromFireBase(User.getCurrentUserUID(), new OnResultListener<String>() {
                    @Override
                    public String onComplete(String result) {
                        fireMD5[0] = result;
                        if (savedMD5 != null){
                            if (!savedMD5.equals(fireMD5[0])) {
                                imgUri[0] = ImageUplDwnlUtils.downloadImage(context, User.getCurrentUserUID(), null);
                            } else {
                                File localFile = new File(context.getCacheDir().getAbsolutePath(), "user_avatar_"  + User.getCurrentUserUID()+".jpg");
                                if (!localFile.exists()){
                                    imgUri[0] = ImageUplDwnlUtils.downloadImage(context, User.getCurrentUserUID(), null);
                                }
                            }
                        } else {
                            imgUri[0] = ImageUplDwnlUtils.downloadImage(context, User.getCurrentUserUID(), null);
                        }
                        return result;
                    }

                    @Override
                    public String onSuccess(String result) {
                        fireMD5[0] = result;
                        return result;
                    }

                    @Override
                    public void onFailure(Throwable error) {

                    }
                });

            }

        }

    }

}