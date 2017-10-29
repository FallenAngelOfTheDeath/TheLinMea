package com.fallenangel.linmea.model;

import android.content.Context;

import com.fallenangel.linmea.profile.UserMetaData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NineB on 9/17/2017.
 */

public class Singleton {

    private static Singleton sSingleton;
    private Context mContext;
    private FirebaseDatabase mFireDatabase;
   private DatabaseReference mDatabaseReference;

    public static Singleton getInstance(Context context) {
        if (sSingleton == null){
            sSingleton = new Singleton(context);
        }
        return sSingleton;
    }

    private Singleton(Context context) {
        mContext = context.getApplicationContext();
        mFireDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFireDatabase.getReference();
    }
    private List<CustomDictionaryModel> getWordListForCustomDictionary (){
        final List<CustomDictionaryModel> wordList = new ArrayList<>();
        mDatabaseReference.child("custom_dictionary").child(UserMetaData.getUserUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // wordList.add();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    return wordList;
    }

//    private CustomDictionaryModel getWord (DataSnapshot dataSnapshot){
//        String mUserUID = UserMetaData.getUserUID();
//        String mWord = dataSnapshot.child(mUserUID).child()
//        String mTranslation = dataSnapshot.child(mUserUID).child()
//        String mDescription = dataSnapshot.child(mUserUID).child()
//        private Boolean mStatus, mFavorite;
//
//
//    }


}
