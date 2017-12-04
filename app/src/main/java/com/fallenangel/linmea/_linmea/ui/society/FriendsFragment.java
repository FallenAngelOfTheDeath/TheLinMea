package com.fallenangel.linmea._linmea.ui.society;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.adapter.FriendsRVAdapter;
import com.fallenangel.linmea._linmea.data.firebase.FirebaseHelper;
import com.fallenangel.linmea._linmea.data.firebase.FirebaseSocietyWrapper;
import com.fallenangel.linmea._linmea.interfaces.OnFriendRequestClickListener;
import com.fallenangel.linmea._linmea.model.UserModel;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea.linmea.user.authentication.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.fallenangel.linmea._linmea.model.UserModel.USER_NICKNAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements OnRecyclerViewClickListener, View.OnClickListener, OnFriendRequestClickListener {

    private static final String TAG = "FriendsFragment";
    private RecyclerView mRecyclerView;
    private FriendsRVAdapter mAdapter;
    private FirebaseHelper<UserModel.MainUserModel> mFirebaseHelper;
    private List<UserModel.MainUserModel> mItems = new ArrayList<>();
    private List<UserModel.FriendListModel> mItemsUids = new ArrayList<>();
    private List<UserModel.FriendListModel> mFriends = new ArrayList<>();
    private List<String> mListFriendID = new ArrayList<>();
    private FirebaseSocietyWrapper mWrapper = new FirebaseSocietyWrapper();
    private FloatingActionButton mFab;
    private String mFriendsPath = "society/users_data/" + User.getCurrentUserUID() + "/friends";
    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        mFirebaseHelper = new FirebaseHelper<>(getActivity(), mFriendsPath, mItems, mAdapter);
        implementView(rootView);
        getFriendList();
        return rootView;
    }

    private void getFriendList(){

        final UserModel.FriendListModel friendn = new UserModel.FriendListModel();
        FirebaseDatabase.getInstance().getReference().child(mFriendsPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    String[] str =  dataSnapshot.getValue().toString().replaceAll("\\{|\\}", "").split("=");
                    friendn.setUID(str[0]);
                    friendn.setFriendStatus(Boolean.parseBoolean(str[1]));
                    mFriends.add(friendn);
                    implementRVAdapter();
                } else {
                    //sorry u are have not a friends
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void implementView(View view){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);

        mFab.setOnClickListener(this);
    }
    private void implementRVAdapter(){
        mAdapter = new FriendsRVAdapter(getActivity(), mItems, mItemsUids, this, null ,null, this);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
//        for (int i = 0; i < 3; i++) {
//            UserModel.MainUserModel message = new UserModel.MainUserModel();
//                message.setNickName("musd33qW0sdE7paVwLLRvOUB7Q43" + i);
//                message.setEmail("musd33qW0sdE7paVwLLRvOUB7Q43" + i*58);
//            mItems.add(i, message);
//            mAdapter.addData(mItems);
//        }
//        if (getCurrentUser() != null) {
            implementFBData();
//        }
        mAdapter.notifyDataSetChanged();
    }

    private List<UserModel.FriendListModel> implementFBData() {
        final UserModel.FriendListModel friend = new UserModel.FriendListModel();
        DatabaseReference mDatabaseReference;

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.child(mFriendsPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    String[] str =  dataSnapshot.getValue().toString().replaceAll("\\{|\\}", "").split("=");
                    friend.setUID(str[0]);
                    friend.setFriendStatus(Boolean.parseBoolean(str[1]));
                    mItemsUids.add(friend);
                    getFriendInfo(0);
                } else {
                    //sorry u are have not a friends
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        mDatabaseReference.child("society/users/").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //if (dataSnapshot.getKey().equals(mItems.))
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });



        return mItemsUids;
    }

    private void getFriendInfo(final int index){
        if (index != mItemsUids.size()) {
            final String id = mItemsUids.get(index).getUID();
            FirebaseDatabase.getInstance().getReference().child("society/users/" + id)
                    .orderByChild(USER_NICKNAME)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        UserModel.MainUserModel user = mWrapper.getPubUserData(dataSnapshot);
                        mItems.add(user);
                        mAdapter.setItems(mItems);
                        mAdapter.notifyDataSetChanged();
//                        String useridRoom = id.compareTo(User.getCurrentUserUID()) > 0 ? (User.getCurrentUserUID() + id).hashCode() + "" : "" + (id + User.getCurrentUserUID()).hashCode();
                    }
                    getFriendInfo(index + 1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onItemClicked(View view, int position) {
        final String id = mItemsUids.get(position).getUID();
        final String name = mItems.get(position).getNickName();
        String chatRoomId = id.compareTo(User.getCurrentUserUID()) > 0 ? (User.getCurrentUserUID() + id).hashCode() + "" : "" + (id + User.getCurrentUserUID()).hashCode();
        Intent chatRoom = new Intent(getActivity(), ChatRoomActivity.class);
        chatRoom.putExtra(ChatRoomActivity.CHAT_ROOM_ID, "room_" + chatRoomId);
        chatRoom.putExtra(ChatRoomActivity.FRIEND_NAME, name);
        startActivity(chatRoom);
    }

    @Override
    public void onOptionsClicked(View view, int position) {

    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:

                final EditText editText = new EditText(getActivity());
                mAlertDialogBuilder = new AlertDialog.Builder(getActivity());
                mAlertDialogBuilder.setTitle(R.string.find_friend_by_email);
                mAlertDialogBuilder.setView(editText);
                mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("society/users")
                                //.equalTo(editText.getText().toString())
//                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        Log.i(TAG, "onDataChange: " + dataSnapshot.getValue());
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                if (editText.getText().toString().contains("@")){
                                    if (dataSnapshot.child("email").getValue().toString().toLowerCase()
                                            .equals(editText.getText().toString())){
                                        User.addFriend(dataSnapshot.getKey());
                                    }
                                }else{
//                                    if (dataSnapshot.child("nickname").getValue().toString().toLowerCase()
//                                            .equals(editText.getText().toString().toLowerCase())){
//                                        User.addFriend(dataSnapshot.getKey());
//                                    }
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
                    }
                });
                mAlertDialogBuilder.setNegativeButton(getActivity().getString(R.string.cancel), null);
                mAlertDialogBuilder.show();



                break;
        }
    }

    @Override
    public void onAccept(View view, int position) {
        User.acceptFriendRequest(mFriends.get(position).getUID());

        final UserModel.FriendListModel friend = new UserModel.FriendListModel();
        friend.setUID(mFriends.get(position).getUID());
        friend.setFriendStatus(true);
        mFriends.set(position, friend);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancel(View view, int position) {
        User.cancelFriendRequest(mFriends.get(position).getUID());
        mAdapter.removeItem(position);
        mAdapter.notifyDataSetChanged();
    }


//        mFirebaseHelper = new FirebaseHelper<>(getActivity(), mFriendsPath, mItems, mAdapter);
//
//        mFirebaseHelper.
//
//
//        mItems = mFirebaseHelper.getItemsList(FirebaseHelper.ORDER_BY_CHILD, new OnChildListener<FriendListModel>(){
//
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s, int index) {
//                //mRecyclerView.scrollToPosition(mChatRoomRVAdapter.getItemCount()-1);
//                //Log.i(TAG, "onChildAdded: " + mChatWrapper.getChatMessage(dataSnapshot).getText());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s, int index) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot, int index) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s, int index) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//            @Override
//            public FriendListModel getItem(DataSnapshot dataSnapshot) {
//                return mWrapper.getFriendsList(dataSnapshot);
//            }
//
//            @Override
//            public String getSortedString() {
//                return null;
//            }
//        });
//        return mItems;
//    }
//
//    @Override
//    public void onItemClicked(View view, int position) {
//
//    }
//
//    @Override
//    public void onOptionsClicked(View view, int position) {
//
//    }
//
//    @Override
//    public boolean onItemLongClicked(View view, int position) {
//        return false;
//    }
}
