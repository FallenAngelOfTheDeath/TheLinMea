package com.fallenangel.linmea._test;

import android.app.ActivityManager;
import android.content.Context;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;

import com.fallenangel.linmea._modulus.auth.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ServiceUtils {

    private static ServiceConnection connectionServiceFriendChatForStart = null;
    private static ServiceConnection connectionServiceFriendChatForDestroy = null;

    public static boolean isServiceFriendChatRunning(Context context) {
        Class<?> serviceClass = BoundService.class;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

//    public static void connectToServiceForStart(Context context){
//        if (connectionServiceFriendChatForStart != null) {
//            context.unbindService(connectionServiceFriendChatForStart);
//        }
//        connectionServiceFriendChatForStart = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName className,
//                                           IBinder service) {
//                BoundService.MyBinder binder = (BoundService.MyBinder) service;
////                List<String> driends = binder.getService().get
////                for (int i = 0; i < ; i++) {
////
////                }
////
////                for (Friend friend : binder.getService().listFriend.getListFriend()) {
////                    binder.getService().mapMark.put(friend.idRoom, true);
////                }
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName arg0) {
//            }
//        };
//        Intent intent = new Intent(context, BoundService.class);
//        context.bindService(intent, connectionServiceFriendChatForStart, Context.BIND_NOT_FOREGROUND);
//    }

    public static void updateUserStatus(Context context){
        if(isNetworkConnected(context)) {
            String uid = User.getCurrentUserUID();
            if (!uid.equals("")) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("society/users/" + uid + "/status/isOnline");
                dbRef.setValue(true);

               // FirebaseDatabase.getInstance().getReference().child("society/users/" + uid + "/status/isOnline").setValue(true);
               FirebaseDatabase.getInstance().getReference().child("society/users/" + uid + "/status/timestamp").setValue(System.currentTimeMillis());
                dbRef.onDisconnect().setValue(false);
            }
        }
    }

    public static boolean isNetworkConnected(Context context) {
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        }catch (Exception e){
            return true;
        }
    }
}
