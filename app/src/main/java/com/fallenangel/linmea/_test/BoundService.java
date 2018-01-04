/*
 * Copyright (c) 2016. Truiton (http://www.truiton.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Mohit Gupt (https://github.com/mohitgupt)
 *
 */

package com.fallenangel.linmea._test;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Chronometer;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.model.UserModel;
import com.fallenangel.linmea._linmea.ui.society.ChatRoomActivity;
import com.fallenangel.linmea.linmea.user.authentication.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class BoundService extends Service {
    private static String LOG_TAG = "BoundService";
    private static String TAG = "BoundService";
    private IBinder mBinder = new MyBinder();
    private Chronometer mChronometer;
    private final Random mGenerator = new Random();

    public static long TIME_TO_REFRESH = 10 * 1000;
    public static long TIME_TO_OFFLINE = 2 * 60 * 1000;
    public CountDownTimer updateOnline;


    public List<String> mFriendsList;
    public Map<String, String> mFriendsMap = new HashMap<>();
    public List<String> mGroupList;
    public CountDownTimer mUpdateOnline;
    public Map<String, Query> mapQuery = new HashMap<>();
    public Map<String, ChildEventListener> mapChildEventListenerMap = new HashMap<>();









    public Map<String, Boolean> mapMark = new HashMap<>();
    public Map<String, Query> mMapQuery = new HashMap<>();
    private final UserModel.FriendListModel mFriend = new UserModel.FriendListModel();
    private List<String> mFriendUidList = new ArrayList<>();
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private Map<String, Boolean> mNotifyMark = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        //loadFriendUidList();









        if (User.getCurrentUser() != null)
        loadFriendUidList();









      //   FirebaseDatabase.getInstance().getReference().child("message/" + generateRoomId("musd33qW0sdE7paVwLLRvOUB7Q43")).limitToLast(1));










    }

    private void getFriendList(){
        String friendsPath = "society/users_data/" + User.getCurrentUserUID() + "/friends";


        mDatabaseReference.child(friendsPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] str = dataSnapshot.getValue().toString().replaceAll("\\{|\\}", "").split("=");
                mFriend.setUID(str[0]);
                mFriendUidList.add(str[0]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadFriendUidList(){
        String friendsPath = "society/users_data/" + User.getCurrentUserUID() + "/friends";


        mDatabaseReference.child(friendsPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String[] str =  dataSnapshot.getValue().toString().replaceAll("\\{|\\}", "").split("=");
                mFriend.setUID(str[0]);
                mFriendUidList.add(str[0]);

                final String[] nickName = {""};
                FirebaseDatabase.getInstance().getReference().child("society/users/" + str[0] + "/nickname").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        nickName[0] = dataSnapshot.getValue().toString();
                        mFriendsMap.put(str[0],nickName[0]);
                        Log.i(TAG, "onDataChange: " + mFriendsMap);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mapQuery(mFriendUidList);
                mapMark.put(str[0], true);


//
//                final String roomId = generateRoomId("musd33qW0sdE7paVwLLRvOUB7Q43");
//
//                createNotify("ujkhjk",
//                        (String) ((HashMap) dataSnapshot.getValue()).get("text"),
//                        roomId.hashCode(),
//                        null,
//                        false);









                                                                                                                        //                if (mFriendUidList.size() > 0 && mFriendUidList != null) {
                                                                                                                        //                    Log.i(TAG, "onCreate: jkohbk;jk;klj;lk;0");
                                                                                                                        //                    //for (final Friend friend : listFriend.getListFriend()) {
                                                                                                                        //                    for (int i = 0; i < mFriendUidList.size(); i++) {
                                                                                                                        //                        Log.i(TAG, "onDataChange: 1");
                                                                                                                        //                        final String roomId = "room_" + generateRoomId(mFriendUidList.get(i));
                                                                                                                        //                       // final String roomId = generateRoomId("musd33qW0sdE7paVwLLRvOUB7Q43");
                                                                                                                        //                        //   if (!listKey.contains(friend.idRoom)) {
                                                                                                                        //                        mapQuery.put(roomId, FirebaseDatabase.getInstance().getReference().child("society/friends_messaging/" + roomId).limitToLast(1));
                                                                                                                        //                        Log.i(TAG, "onDataChange: " + roomId);
                                                                                                                        //                        mapChildEventListenerMap.put(roomId, new ChildEventListener() {
                                                                                                                        //
                                                                                                                        //                            @Override
                                                                                                                        //                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                                                                                        //                                Log.i(TAG, "onChildAdded: 2");
                                                                                                                        //                                createNotify(roomId, (String) ((HashMap) dataSnapshot.getValue()).get("text"), roomId.hashCode(), null, false);
                                                                                                                        //
                                                                                                                        //                                Log.i(TAG, "onChildAdded: ");
                                                                                                                        //                            }
                                                                                                                        //
                                                                                                                        //                            @Override
                                                                                                                        //                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                                                                                                        //
                                                                                                                        //                            }
                                                                                                                        //
                                                                                                                        //                            @Override
                                                                                                                        //                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                                                                                                        //
                                                                                                                        //                            }
                                                                                                                        //
                                                                                                                        //                            @Override
                                                                                                                        //                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                                                                                                        //
                                                                                                                        //                            }
                                                                                                                        //
                                                                                                                        //                            @Override
                                                                                                                        //                            public void onCancelled(DatabaseError databaseError) {
                                                                                                                        //
                                                                                                                        //                            }
                                                                                                                        //                        });
                                                                                                                        //                        //listKey.add(friend.idRoom);
                                                                                                                        //                        //  }
                                                                                                                        //                        mapQuery.get(roomId).addChildEventListener(mapChildEventListenerMap.get(roomId));
                                                                                                                        //                    }
                                                                                                                        //                } else {
                                                                                                                        //                    stopSelf();
                                                                                                                        //                }
                                                                                                                        //










                //if (mFriendUidList != null) mapQuery(mFriendUidList);
                //if (mFriendUidList != null) Log.i(TAG, "onDataChange: " + mFriendUidList.get(0));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String generateRoomId(String friendUid){
        return friendUid.compareTo(User.getCurrentUserUID()) > 0 ? (User.getCurrentUserUID() + friendUid).hashCode() + "" : "" + (friendUid + User.getCurrentUserUID()).hashCode();
    }

    private Map<String, Query> mapQuery(List<String> friendList){
        if (friendList.size() > 0) {
            for (int i = 0; i < friendList.size(); i++) {
                String friendChatRoomId = "room_" + generateRoomId(friendList.get(i));
                mMapQuery.put(friendChatRoomId, FirebaseDatabase.getInstance().getReference().child("society/friends_messaging/" + friendChatRoomId).limitToLast(1));
                addChildEventListenerMap(friendChatRoomId, friendList.get(i));
            }
        } else {
        stopSelf();
    }
        return mMapQuery;
    }

    private void addChildEventListenerMap(final String chatRoomId, final String id){
        mapChildEventListenerMap.
                put(chatRoomId,
                new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (mapMark.get(chatRoomId) != null) {
                    if (mapMark.get(chatRoomId) != false)
                    createNotify(mFriendsMap.get(id), (String) ((HashMap) dataSnapshot.getValue()).get("text"), chatRoomId.hashCode());
                } else {
                    mapMark.put(chatRoomId, true);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mMapQuery.get(chatRoomId).addChildEventListener(mapChildEventListenerMap.get(chatRoomId));
    }


    public void stopNotify(String id) {
        mapMark.put(id, false);
    }
    public void startNotify(String id) {
        mapMark.put(id, true);
    }

    public void createNotify(String name, String content, int id) {
        //String nickName = User.getUserNameById(name);
        Intent activityIntent = new Intent(this, ChatRoomActivity.class);
        activityIntent.putExtra(ChatRoomActivity.CHAT_ROOM_ID, id);
        activityIntent.putExtra(ChatRoomActivity.FRIEND_NAME, name);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                //.setLargeIcon(icon)
                .setContentTitle(name)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] { 1000, 1000})
             //   .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true);
       // if (isGroup) {
            notificationBuilder.setSmallIcon(R.drawable.ic_message_black);
       // } else {
            //notificationBuilder.setSmallIcon(R.drawable.ic_tab_person);
      //  }
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(
                       Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
        notificationManager.notify(id,
                notificationBuilder.build());
    }


    //    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.v(LOG_TAG, "in onCreate");
//        mChronometer = new Chronometer(this);
//        mChronometer.setBase(SystemClock.elapsedRealtime());
//        mChronometer.start();
//
//
//
//        updateOnline = new CountDownTimer(System.currentTimeMillis(), TIME_TO_REFRESH) {
//            @Override
//            public void onTick(long l) {
//                ServiceUtils.updateUserStatus(getApplicationContext());
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        };
//        updateOnline.start();
//
//
//        mFriendsList = new ArrayList<>();
//        mGroupList = new ArrayList<>();
//        mapQuery = new HashMap<>();
//        mapChildEventListenerMap = new HashMap<>();
//        getFriendsUIds();
//
//    }
    private void getFriendsUIds() {
        final UserModel.FriendListModel friend = new UserModel.FriendListModel();
        DatabaseReference mDatabaseReference;

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Log.i("gfhgfhgfhfg", "path:     " + "society/users_data/" + User.getCurrentUserUID() + "/friends");

        mDatabaseReference.child("society/users_data/" + User.getCurrentUserUID() + "/friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: " + dataSnapshot.getValue().toString());
                String[] str =  dataSnapshot.getValue().toString().replaceAll("\\{|\\}", "").split("=");
                friend.setUID(str[0]);
                mFriendsList.add(str[0]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.i("gfhgfhgfhfg", "getFriendsUIds: " + mFriendsList + "     :          " + "society/users_data/" + User.getCurrentUserUID() + "/friends");
        //return mFriendsList;
    }
    private void checkNewMssg(){
        if (mFriendsList.size() > 0) {

            for (int i = 0; i < mFriendsList.size(); i++) {
                final String id = mFriendsList.get(i);
                String chatRoomId = id.compareTo(User.getCurrentUserUID()) > 0 ? (User.getCurrentUserUID() + id).hashCode() + "" : "" + (id + User.getCurrentUserUID()).hashCode();
                mapQuery.put(chatRoomId, FirebaseDatabase.getInstance().getReference().child("society/friends_messaging/" + chatRoomId).limitToLast(1));

                mapChildEventListenerMap.put(chatRoomId, new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.i(TAG, "onChildAdded: " + dataSnapshot.getValue());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mapQuery.get(chatRoomId).addChildEventListener(mapChildEventListenerMap.get(chatRoomId));

            }
        } else {
            stopSelf();
        }
    }
    public int getRandomInt(){
        return mGenerator.nextInt(10000);
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
//        mChronometer.stop();
   //     updateOnline.cancel();
//
    }

    public String getTimestamp() {
        long elapsedMillis = SystemClock.elapsedRealtime()
                - mChronometer.getBase();
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
        int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
        int millis = (int) (elapsedMillis - hours * 3600000 - minutes * 60000 - seconds * 1000);
        return hours + ":" + minutes + ":" + seconds + ":" + millis;
    }

    public class MyBinder extends Binder {
        public BoundService getService() {
            return BoundService.this;
        }
    }
}
