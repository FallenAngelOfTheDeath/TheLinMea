package com.fallenangel.linmea.firebase;

import com.fallenangel.linmea.database.FirebaseWrapper;
import com.fallenangel.linmea.interfaces.OnChildListener;
import com.fallenangel.linmea.model.MyDictionaryModel;
import com.fallenangel.linmea.profile.UserMetaData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by NineB on 9/18/2017.
 */

public class FBhelper {

    DatabaseReference mDatabaseReference;
    ArrayList<MyDictionaryModel> mItems;
    FirebaseWrapper firebaseWrapper = new FirebaseWrapper();

    public FBhelper (DatabaseReference databaseReference, ArrayList<MyDictionaryModel> items) {
        this.mDatabaseReference = databaseReference;
        this.mItems = items;
    }

    private  void fetchData (DataSnapshot dataSnapshot) {
        MyDictionaryModel item = firebaseWrapper.getDictionaryItem(dataSnapshot);
        mItems.add(item);
    }

    public ArrayList<MyDictionaryModel> getFirebaseData (final OnChildListener onChildListener) {
        mDatabaseReference.child("custom_dictionary").child(UserMetaData.getUserUID()).child("meta_data").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
                onChildListener.onChildAdded(dataSnapshot, s, -1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
                onChildListener.onChildChanged(dataSnapshot, s, -1);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
                onChildListener.onChildRemoved(dataSnapshot, -1);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                onChildListener.onChildMoved(dataSnapshot, s,  -1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onChildListener.onCancelled(databaseError);
            }
        });
        return mItems;
    }

}
