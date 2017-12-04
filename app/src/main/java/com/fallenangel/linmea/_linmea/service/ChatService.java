package com.fallenangel.linmea._linmea.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Random;

/**
 * Created by NineB on 11/25/2017.
 */

public class ChatService extends Service{
    private final static String TAG = "ChatService";
    public final IBinder mBinder = new LocalBinder();

    private final Random mGenerator = new Random();

    public class LocalBinder extends Binder {
            public ChatService getChatService(){
                Log.i(TAG, "getChatService: " + getRandomInt());
                return  ChatService.this;
            }
    }

    public ChatService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public int getRandomInt(){
        return mGenerator.nextInt(10000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: " + getRandomInt());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: " + getRandomInt());
    }


    //
//    public ChatService() {
//    }
//
//    public class LocalBinder extends Binder {
//        public ChatService getService() {
//            // Return this instance of LocalService so clients can call public methods
//            return ChatService.this;
//        }
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(TAG, "OnStartService");
//        return START_STICKY;
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        Log.d(TAG, "OnBindService");
//        return mBinder;
//    }
    //    public List<String> mFriendsList;
//    public List<String> mGroupList;
//    public CountDownTimer mUpdateOnline;
//    public Map<String, Query> mapQuery;
//    public Map<String, ChildEventListener> mapChildEventListenerMap;
//
//    private String mFriendsPath = "society/users_data/" + User.getCurrentUserUID() + "/friends";
//
//    private String mOnlineStatusPath = "society/users/" + User.getCurrentUserUID() + "/status/";
//
//    public static long TIME_TO_REFRESH = 10 * 1000;
//    public static long TIME_TO_OFFLINE = 2 * 60 * 1000;

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.i(TAG, "onCreate: ");
////        mFriendsList = new ArrayList<>();
////        mGroupList = new ArrayList<>();
////        mapQuery = new HashMap<>();
////        mapChildEventListenerMap = new HashMap<>();
////        getFriendsUIds();
////        updateOnline();
//    }
//    private void updateOnline(){
//        mUpdateOnline = new CountDownTimer(System.currentTimeMillis(), TIME_TO_REFRESH) {
//            @Override
//            public void onTick(long l) {
//                updateUserStatus(getApplicationContext());
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        };
//        mUpdateOnline.start();
//    }
//
//    public void updateUserStatus(Context context){
//        if(isNetworkConnected(context)) {
//           // if (!uid.equals("")) {
//                FirebaseDatabase.getInstance().getReference().child(mOnlineStatusPath + "isOnline").setValue(true);
//                FirebaseDatabase.getInstance().getReference().child(mOnlineStatusPath + "timestamp").setValue(System.currentTimeMillis());
//            //}
//        }
//    }
//
//    public static boolean isNetworkConnected(Context context) {
//        try{
//            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            return cm.getActiveNetworkInfo() != null;
//        }catch (Exception e){
//            return true;
//        }
//    }
//
//    private List<String> getFriendsUIds() {
//        final UserModel.FriendListModel friend = new UserModel.FriendListModel();
//        DatabaseReference mDatabaseReference;
//
//        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
//
//        mDatabaseReference.child(mFriendsPath).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String[] str =  dataSnapshot.getValue().toString().replaceAll("\\{|\\}", "").split("=");
//                friend.setUID(str[0]);
//                mFriendsList.add(str[0]);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        return mFriendsList;
//    }
//    private void checkNewMssg(){
//        if (mFriendsList.size() > 0) {
//
//            for (int i = 0; i < mFriendsList.size(); i++) {
//                final String id = mFriendsList.get(i);
//                String chatRoomId = id.compareTo(User.getCurrentUserUID()) > 0 ? (User.getCurrentUserUID() + id).hashCode() + "" : "" + (id + User.getCurrentUserUID()).hashCode();
//                mapQuery.put(chatRoomId, FirebaseDatabase.getInstance().getReference().child("society/friends_messaging/" + chatRoomId).limitToLast(1));
//
//                mapChildEventListenerMap.put(chatRoomId, new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//                mapQuery.get(chatRoomId).addChildEventListener(mapChildEventListenerMap.get(chatRoomId));
//
//            }
//
////            //Dang ky lang nghe cac room tai day
////            for (final Friend friend : listFriend.getListFriend()) {
////                if (!listKey.contains(friend.idRoom)) {
////                    mapQuery.put(friend.idRoom, FirebaseDatabase.getInstance().getReference().child("message/" + friend.idRoom).limitToLast(1));
////                    mapChildEventListenerMap.put(friend.idRoom, new ChildEventListener() {
////                        @Override
////                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
////                            if (mapMark.get(friend.idRoom) != null && mapMark.get(friend.idRoom)) {
//////                                Toast.makeText(FriendChatService.this, friend.name + ": " + ((HashMap)dataSnapshot.getValue()).get("text"), Toast.LENGTH_SHORT).show();
//////                                if (mapBitmap.get(friend.idRoom) == null) {
//////                                    if (!friend.avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
//////                                        byte[] decodedString = Base64.decode(friend.avata, Base64.DEFAULT);
//////                                        mapBitmap.put(friend.idRoom, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
//////                                    } else {
//////                                        mapBitmap.put(friend.idRoom, BitmapFactory.decodeResource(getResources(), R.drawable.default_avata));
//////                                    }
//////                                }
////                                createNotify(friend.name, (String) ((HashMap) dataSnapshot.getValue()).get("text"), friend.idRoom.hashCode(), mapBitmap.get(friend.idRoom), false);
////
////                            } else {
////                                mapMark.put(friend.idRoom, true);
////                            }
////                        }
////
////                        @Override
////                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
////
////                        }
////
////                        @Override
////                        public void onChildRemoved(DataSnapshot dataSnapshot) {
////
////                        }
////
////                        @Override
////                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
////
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////
////                        }
////                    });
////                    listKey.add(friend.idRoom);
////                }
////                mapQuery.get(friend.idRoom).addChildEventListener(mapChildEventListenerMap.get(friend.idRoom));
////            }
//        } else {
//            stopSelf();
//        }
//    }
//
//
//
//    public void createNotify(String name, String content, int id, Bitmap icon, boolean isGroup) {
//        Intent activityIntent = new Intent(this,  MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_ONE_SHOT);
//        NotificationCompat.Builder notificationBuilder = new
//                NotificationCompat.Builder(this)
//                .setLargeIcon(icon)
//                .setContentTitle(name)
//                .setContentText(content)
//                .setContentIntent(pendingIntent)
//                .setVibrate(new long[] { 1000, 1000})
//                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
//                .setAutoCancel(true);
////        if (isGroup) {
////            notificationBuilder.setSmallIcon(R.drawable.ic_tab_group);
////        } else {
////            notificationBuilder.setSmallIcon(R.drawable.ic_tab_person);
////        }
//        NotificationManager notificationManager =
//                (NotificationManager) this.getSystemService(
//                        Context.NOTIFICATION_SERVICE);
//        notificationManager.cancel(id);
//        notificationManager.notify(id,
//                notificationBuilder.build());
//    }
}
