package com.fallenangel.linmea._linmea;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fallenangel.linmea._linmea.service.ChatService;
import com.fallenangel.linmea._linmea.service.UpdateUserData;
import com.fallenangel.linmea._linmea.ui.MainActivity;
import com.fallenangel.linmea._linmea.ui.first_start.FirstStartActivity;
import com.fallenangel.linmea._linmea.util.SharedPreferencesUtils;
import com.fallenangel.linmea._test.BoundService;
import com.fallenangel.linmea.linmea.user.authentication.User;

import static com.fallenangel.linmea._test.ServiceUtils.isServiceFriendChatRunning;
import static com.fallenangel.linmea.linmea.utils.image.ImageUplDwnlUtils.saveCurrentFireMD5OfImageFromServer;

/**
 * Created by NineB on 11/10/2017.
 */

public class SplashScreen extends AppCompatActivity{

    private static final String TAG = "SplashScreen";
    private String mFirstStart;
    private Thread mThread;
    private ChatService mChatService;
    private boolean isBound = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                startUp(SplashScreen.this);
//            }
//        });
//        mThread.setDaemon(true);
//        mThread.start();

        startUp();

        mFirstStart = SharedPreferencesUtils.getFromSharedPreferences(this, "FIRST", "START");
        if (mFirstStart == null){
            SharedPreferencesUtils.putToSharedPreferences(this, "FIRST", "START", "false");
            Intent firstStart = new Intent(this, FirstStartActivity.class);
            startActivity(firstStart);
            finish();
        } else {
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
            finish();
        }

        Log.i(TAG, "onCreate: " );

//        if (!isServiceFriendChatRunning(this)) {
            Intent intent = new Intent(this, BoundService.class);
            startService(intent);
            ///bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
//        } else {
//            if (mServiceConnection != null) {
//                this.unbindService(mServiceConnection);
//            }
//            mServiceConnection;
//        }

    }
                                                //    BoundService mBoundService;
                                                //    boolean mServiceBound = false;
                                                //    private ServiceConnection mServiceConnection = new ServiceConnection() {
                                                //
                                                //        @Override
                                                //        public void onServiceDisconnected(ComponentName name) {
                                                //            mServiceBound = false;
                                                //        }
                                                //
                                                //        @Override
                                                //        public void onServiceConnected(ComponentName name, IBinder service) {
                                                //            BoundService.MyBinder myBinder = (BoundService.MyBinder) service;
                                                //            mBoundService = myBinder.getService();
                                                //            mServiceBound = true;
                                                //        }
                                                //    };

    @Override
    protected void onStart() {
        super.onStart();

        // Intent bindChatIntent = new Intent(this, ChatService.class);
       // startService(bindChatIntent);
       // bindService(bindChatIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void startUp () {
        if(User.getCurrentUser() != null){
            saveCurrentFireMD5OfImageFromServer(this, User.getCurrentUserUID(), "FireMD5");
        }
        UpdateUserData.getUserDataFromFire(this);
        startService(new Intent(this, UpdateUserData.class));
    }
//
//    private ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.i(TAG, "onServiceConnected: ");
//            ChatService.LocalBinder binder = (ChatService.LocalBinder) service;
//            mChatService = binder.getChatService();
//            isBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            Log.i(TAG, "onServiceDisconnected: ");
//            isBound = false;
//        }
//    };

    public static void startServiceFriendChat(Context context) {
        if (!isServiceFriendChatRunning(context)) {
            Intent myIntent = new Intent(context, ChatService.class);
            context.startService(myIntent);
        }
// else {
//            if (connectionServiceFriendChatForStart != null) {
//                context.unbindService(connectionServiceFriendChatForStart);
//            }
//            connectionServiceFriendChatForStart = new ServiceConnection() {
//                @Override
//                public void onServiceConnected(ComponentName className,
//                                               IBinder service) {
//                    FriendChatService.LocalBinder binder = (FriendChatService.LocalBinder) service;
//                    for (Friend friend : binder.getService().listFriend.getListFriend()) {
//                        binder.getService().mapMark.put(friend.idRoom, true);
//                    }
//                }
//
//                @Override
//                public void onServiceDisconnected(ComponentName arg0) {
//                }
//            };
//            Intent intent = new Intent(context, FriendChatService.class);
//            context.bindService(intent, connectionServiceFriendChatForStart, Context.BIND_NOT_FOREGROUND);
//        }
    }


}
