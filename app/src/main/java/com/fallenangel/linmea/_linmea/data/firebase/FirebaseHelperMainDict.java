package com.fallenangel.linmea._linmea.data.firebase;

import android.content.Context;
import android.util.Log;

import com.fallenangel.linmea._linmea.interfaces.OnChildListener;
import com.fallenangel.linmea._linmea.model.MainDictionaryModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by NineB on 12/8/2017.
 */

public class FirebaseHelperMainDict {

    private DatabaseReference mDatabaseReference;
    private Context mContext;
    private List<MainDictionaryModel> mItems;
    private String mPath;
    private MainDictWrapper mMainDictWrapper;

    public FirebaseHelperMainDict (Context context, List<MainDictionaryModel> items, String path) {
        this.mContext = context;
        this.mItems = items;
        this.mPath = path;
        this.mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        this.mMainDictWrapper = new MainDictWrapper();
    }

    public List<MainDictionaryModel> getItemsList(final OnChildListener<MainDictionaryModel> onChildListener){
        mDatabaseReference.child(mPath).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MainDictionaryModel item = mMainDictWrapper.getItem(dataSnapshot);
                mItems.add(item);
                Log.i("k;kl;kl", "onChildAdded: " + item.getWord());
                onChildListener.onChildAdded(dataSnapshot, s, -1);
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
        return null;
    }

    public class MainDictWrapper{
        public MainDictionaryModel getItem(DataSnapshot dataSnapshot){
            String word = (String) dataSnapshot.child("word").getValue();
            String translation = (String) dataSnapshot.child("translation").getValue();
            return new MainDictionaryModel(word, translation);
        }
    }

}
