package com.fallenangel.linmea._linmea.ui.society;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.adapter.ChatRoomRVAdapter;
import com.fallenangel.linmea._linmea.data.firebase.FirebaseHelper;
import com.fallenangel.linmea._linmea.data.firebase.FirebaseSocietyWrapper;
import com.fallenangel.linmea._linmea.interfaces.OnChildListener;
import com.fallenangel.linmea._linmea.model.MessageModel;
import com.fallenangel.linmea._test.BoundService;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea.linmea.user.authentication.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import static com.fallenangel.linmea._test.ServiceUtils.isServiceFriendChatRunning;
import static com.fallenangel.linmea.profile.UserMetaData.getCurrentUser;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewClickListener {

    public static final String CHAT_ID = "CHAT_ID";
    public static final String CHAT_ROOM_ID = "CHAT_ROOM_ID";
    public static final String FRIEND_NAME = "FRIEND_NAME";
    private static final String TAG = "ChatRoomA";

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FloatingActionButton mFab;
    private Toolbar mToolbar;

    private EditText mMessageET;
    private ImageButton mSendMessage;

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseSocietyWrapper mChatWrapper = new FirebaseSocietyWrapper();
    private MessageModel mMassageModel = new MessageModel();
    private ChatRoomRVAdapter mChatRoomRVAdapter;
    private FirebaseHelper<MessageModel> mFirebaseHelper;
    private Animation mFabOpen, mFabClose, mRotateForward, mRotateBackward;

    private String mChatId, mRoomID, mFriendName, mMessageID;
    private List<MessageModel> mItems = new ArrayList<>();
    private Boolean isFabOpen = false;
    String path;
    private BoundService mBoundService;
    private static ServiceConnection connectionServiceFriendChatForStart = null;
    private static ServiceConnection connectionServiceFriendChatForDestroy = null;
    BoundService.MyBinder binder;
    private void implementAnimation() {
        mFabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        mFabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        mRotateForward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        mRotateBackward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getIntentData();

        implementUI();
        implementAnimation();



//        mChatId = "001";
//        mRoomID = "room_-Kzk_0hfFMtWr2ljbp2m_musd33qW0sdE7paVwLLRvOUB7Q43";
//        mFriendName = "003";
//        mMessageID = "004";
        path = "society/friends_messaging/" + mRoomID;

    }

    private void getIntentData(){
        Intent intent = getIntent();
        mRoomID = intent.getStringExtra(CHAT_ROOM_ID);
        mFriendName = intent.getStringExtra(FRIEND_NAME);
    }

    @Override
    protected void onStart() {
        super.onStart();
        implementRVAdapter();

        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(
                        Context.NOTIFICATION_SERVICE);
//        notificationManager.cancel(Integer.parseInt(mRoomID));
        //notificationManager.notify(mRoomID,
         //       notificationBuilder.build());

//        connectionServiceFriendChatForStart = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName className,
//                                           IBinder service) {
//                binder = (BoundService.MyBinder) service;
//                binder.getService().stopNotify(mRoomID);
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName arg0) {
//            }
//        };
//


        connectToServiceForStart(this);

    }

    public void connectToServiceForStart(Context context){
        if (connectionServiceFriendChatForStart != null) {
            context.unbindService(connectionServiceFriendChatForStart);
        }
        connectionServiceFriendChatForStart = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                BoundService.MyBinder binder = (BoundService.MyBinder) service;
                binder.getService().stopNotify(mRoomID);
//                List<String> driends = binder.getService().get
//                for (int i = 0; i < ; i++) {
//
//                }
//
//                for (Friend friend : binder.getService().listFriend.getListFriend()) {
//                    binder.getService().mapMark.put(friend.idRoom, true);
//                }
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
        Intent intent = new Intent(context, BoundService.class);
        context.bindService(intent, connectionServiceFriendChatForStart, Context.BIND_NOT_FOREGROUND);
    }

    @Override
    protected void onResume() {
        super.onResume();
        implementOnRecyclerScrollListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        connectionServiceFriendChatForStart = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName className,
//                                           IBinder service) {
//                binder = (BoundService.MyBinder) service;
//                binder.getService().startNotify(mRoomID);
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName arg0) {
//            }
//        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectToServiceForDestroy(this);
    }

    public void connectToServiceForDestroy(Context context) {
        if (isServiceFriendChatRunning(context)) {
            Intent intent = new Intent(context, BoundService.class);
            if (connectionServiceFriendChatForDestroy != null) {
                context.unbindService(connectionServiceFriendChatForDestroy);
            }
            connectionServiceFriendChatForDestroy = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName className,
                                               IBinder service) {
                    BoundService.MyBinder binder = (BoundService.MyBinder) service;
                    binder.getService().startNotify(mRoomID);
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
                }
            };
            context.bindService(intent, connectionServiceFriendChatForDestroy, Context.BIND_NOT_FOREGROUND);
        }
    }

    private void implementOnRecyclerScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mFab.getVisibility() == View.VISIBLE) {
                    mFab.hide();
                    mFab.setClickable(false);
                    if (isFabOpen) {
                        mFab.startAnimation(mFabOpen);
                        mFab.setClickable(true);
                    }
                } else if (dy < 0 && mFab.getVisibility() != View.VISIBLE) {
                    mFab.show();
                    mFab.setClickable(true);

                }
            }
        });
    }

    private void implementUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mMessageET = (EditText) findViewById(R.id.chat_message_et);
        mSendMessage = (ImageButton) findViewById(R.id.chat_send_message);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.setOnClickListener(this);
        mSendMessage.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        //mToolbar.setHomeAsUpIndicator(R.drawable.ic_action_goleft);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setTitle(mFriendName);
        mToolbar.setId(0);
       // mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
       // mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //mRecyclerView.setLayoutManager(mLinearLayoutManager);

//        getSupportActionBar().setTitle(mFriendName);
    }

    private void implementRVAdapter(){
        mChatRoomRVAdapter = new ChatRoomRVAdapter(this, mItems, mFriendName, this, null ,null);
        mChatRoomRVAdapter.clear();
        mRecyclerView.setAdapter(mChatRoomRVAdapter);

        Log.i(TAG, "implementRVAdapter: " + mFriendName);
//        for (int i = 0; i < 3; i++) {
//            MessageModel message = new MessageModel();
//            if (i % 2 == 0){
//                message.setSender("musd33qW0sdE7paVwLLRvOUB7Q43");
//            } else {
//                message.setSender("musd33qW0sdE7paVwLLRvOUB7Q43" + i);
//            }
//            message.setText("message item: " + i );
//            mItems.add(i, message);
//            mChatRoomRVAdapter.addData(mItems);
//            Log.i(TAG, "onCreate: " + mItems.get(i).getText());
//        }
        if (getCurrentUser() != null) {
            implementFBData();
        }
        mChatRoomRVAdapter.notifyDataSetChanged();
    }



    private List<MessageModel> implementFBData() {
        mFirebaseHelper = new FirebaseHelper<>(this, path, mItems, mChatRoomRVAdapter);
        mItems = mFirebaseHelper.getItemsList(FirebaseHelper.ORDER_DATA, new OnChildListener<MessageModel>(){

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s, int index) {
                mRecyclerView.scrollToPosition(mChatRoomRVAdapter.getItemCount()-1);
               //Log.i(TAG, "onChildAdded: " + mChatWrapper.getChatMessage(dataSnapshot).getText());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s, int index) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot, int index) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s, int index) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public MessageModel getItem(DataSnapshot dataSnapshot) {
                return mChatWrapper.getChatMessage(dataSnapshot);
            }

            @Override
            public String getSortedString() {
                return null;
            }
        });
        return mItems;
//
//        mDatabaseReference.child(path).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if (dataSnapshot.getValue() != null) {
//
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void sendMessage () {
//        mDatabaseReference
//                .child(path + "/" + key + "/data")
//                .setValue(ServerValue.TIMESTAMP);



        if (mMessageET.getText().length() != 0){

            byte[] encodedBytes = null;
            try {
                Cipher c = Cipher.getInstance("RSA");
                c.init(Cipher.ENCRYPT_MODE, mMassageModel.getPrivateEncryptKey());
                encodedBytes = c.doFinal(mMessageET.getText().toString().getBytes());
            } catch (Exception e) {
                Log.e("Crypto", "RSA encryption error");
            }

            String key = mDatabaseReference
                    .child(path)
                    .push()
                    .getKey();

            Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("text", mMessageET.getText().toString());
            //hashMap.put("text", encodedBytes.toString());
            hashMap.put("sender", User.getCurrentUserUID());
            hashMap.put("data", DateFormat.getDateTimeInstance().format(new Date()));
            //hashMap.put("publicEncryptKey", new String(mMassageModel.getPublicEncryptKey()));
            mDatabaseReference
                    .child(path)
                    .child(key)
                    .setValue(hashMap);
            mRecyclerView.scrollToPosition(mChatRoomRVAdapter.getItemCount()-1);
            mMessageET.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chat_send_message:
                sendMessage();
                break;
            case R.id.fab:
                mRecyclerView.scrollToPosition(mChatRoomRVAdapter.getItemCount()-1);
                mFab.hide();
                mFab.setClickable(false);
                break;
            case 0:
                finish();
                break;
        }
    }

    @Override
    public void onItemClicked(View view, int position) {

    }

    @Override
    public void onOptionsClicked(View view, int position) {

    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        return false;
    }


}

