package com.fallenangel.linmea.database.firebase;

import com.fallenangel.linmea.database.FirebaseWrapper;
import com.fallenangel.linmea.interfaces.OnChildListener;
import com.fallenangel.linmea.model.CustomDictionaryModel;
import com.fallenangel.linmea.profile.UserMetaData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by NineB on 9/18/2017.
 */

public class FireBaseHelper {

    DatabaseReference mDatabaseReference;
    ArrayList<CustomDictionaryModel> mItems;
    FirebaseWrapper firebaseWrapper = new FirebaseWrapper();
    String mDictionary;

    public FireBaseHelper (DatabaseReference databaseReference, ArrayList<CustomDictionaryModel> items, String dictionary) {
        this.mDatabaseReference = databaseReference;
        this.mItems = items;
        this.mDictionary = dictionary;
    }

    private  void fetchData (DataSnapshot dataSnapshot) {
        CustomDictionaryModel item = firebaseWrapper.getCustomDictionaryWord(dataSnapshot);
        mItems.add(item);
    }

    public ArrayList<CustomDictionaryModel> getFirebaseData (final OnChildListener onChildListener) {

        mDatabaseReference.child("custom_dictionary").child(UserMetaData.getUserUID()).child("dictionaries").child(mDictionary).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
                onChildListener.onChildAdded(dataSnapshot, s, -1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                CustomDictionaryModel model = firebaseWrapper.getCustomDictionaryWord(dataSnapshot);

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
                onChildListener.onChildMoved(dataSnapshot, s, -1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onChildListener.onCancelled(databaseError);
            }
        });
        return mItems;
    }

    private int getItemIndex (CustomDictionaryModel model, ArrayList<CustomDictionaryModel> items){
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getWord().equals(model.getWord())){
                index = i;
                break;
            }
        }
        return index;
    }

}
