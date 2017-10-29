package com.fallenangel.linmea.linmea.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fallenangel.linmea.interfaces.OnResultListener;
import com.fallenangel.linmea.linmea.user.authentication.user;
import com.fallenangel.linmea.linmea.user.utils.SharedPreferencesUtils;
import com.fallenangel.linmea.linmea.utils.image.ImageUplDwnlUtils;
import com.fallenangel.linmea.utils.CheckPermissionNetwork;
import com.fallenangel.linmea.utils.CheckPermissionReadExternalStorage;
import com.fallenangel.linmea.utils.CheckPermissionWriteExternalStorage;

import java.io.File;

public class UpdateUserData extends Service {

    CheckPermissionReadExternalStorage checkPermissionReadExternalStorage = new CheckPermissionReadExternalStorage();
    CheckPermissionWriteExternalStorage checkPermissionWriteExternalStorage = new CheckPermissionWriteExternalStorage();
    CheckPermissionNetwork checkPermissionNetwork = new CheckPermissionNetwork();

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
            if (user.getCurrentUser() != null){
            if (user.getCurrentUser().getPhotoUrl() != null) {
                final String savedMD5 = SharedPreferencesUtils.getFromSharedPreferences(context, "FireMD5", user.getCurrentUserUID());
                final String[] fireMD5 = {null};
                ImageUplDwnlUtils.getMD5ImagesFromFireBase(user.getCurrentUserUID(), new OnResultListener<String>() {
                    @Override
                    public String onComplete(String result) {
                        fireMD5[0] = result;
                        if (savedMD5 != null){
                            if (!savedMD5.equals(fireMD5[0])) {
                                imgUri[0] = ImageUplDwnlUtils.downloadImage(context, user.getCurrentUserUID(), null);
                            } else {
                                File localFile = new File(context.getCacheDir().getAbsolutePath(), "user_avatar_"  + user.getCurrentUserUID()+".jpg");
                                if (!localFile.exists()){
                                    imgUri[0] = ImageUplDwnlUtils.downloadImage(context, user.getCurrentUserUID(), null);
                                }
                            }
                        } else {
                            imgUri[0] = ImageUplDwnlUtils.downloadImage(context, user.getCurrentUserUID(), null);
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
//
//    private Handler mHandler;
//
//    final static String TAG = "GetUserDataAsyncTask";
//
//    public UpdateUserData() {
//        super("SampleService");
//    }
//
//    public void onCreate() {
//        super.onCreate();
//        Log.d(TAG, "onCreate");
//    }
//
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        mHandler = new Handler();
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//        if (intent != null) {
//            synchronized (this){
//                try {
//                    wait(15000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        while (true){
//                            getUserDataFromFire(UpdateUserData.this);
//                        }
//                        //Toast.makeText(getApplicationContext(), "Service Completed" , Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }
//    }
//
////    public void onDestroy() {
////        super.onDestroy();
////        Log.d(LOG_TAG, "onDestroy");
////    }
////
////    @Override
////    public IBinder onBind(Intent intent) {
////        Log.d(LOG_TAG, "onBind");
////        throw new UnsupportedOperationException("Not yet implemented");
////    }
//
//

//
//
//    private void updateThread() {
//        new Thread(new Runnable() {
//            public void run() {
//                for (int i = 1; i < 20; i ++) {
//                    try {
//                        TimeUnit.SECONDS.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    Log.d(TAG, "hello from service: " + i);
//                }
//            }
//        }).start();
//    }
//}
