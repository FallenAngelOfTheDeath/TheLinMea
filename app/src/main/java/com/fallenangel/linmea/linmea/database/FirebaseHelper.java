package com.fallenangel.linmea.linmea.database;

import android.content.Context;

import com.fallenangel.linmea.database.FirebaseWrapper;
import com.fallenangel.linmea.interfaces.OnChildListener;
import com.fallenangel.linmea.interfaces.OnValueEventListener;
import com.fallenangel.linmea.model.CustomDictionaryModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by NineB on 9/30/2017.
 */

public class FirebaseHelper {

    private List<CustomDictionaryModel> mItems;
    private FirebaseWrapper mFirebaseWrapper = new FirebaseWrapper();
    private String mPath;
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private Context mContext;

    public FirebaseHelper (Context context, List<CustomDictionaryModel> items, String path) {
        this.mContext = context;
        this.mItems = items;
        this.mPath = path;
    }

    public FirebaseHelper (){

    }

    public List<CustomDictionaryModel> getItemsList(final OnChildListener onChildListener){

        mDatabaseReference
                .child(mPath)
                .orderByChild("id")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        CustomDictionaryModel item = mFirebaseWrapper.getCustomDictionaryWord(dataSnapshot);
                        mItems.add(item);
                        //+sorting & filters
                        onChildListener.onChildAdded(dataSnapshot, s, -1);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        CustomDictionaryModel item = mFirebaseWrapper.getCustomDictionaryWord(dataSnapshot);
                        int index = getItemIndex(item, mItems);
                        mItems.set(index, item);
                        //+sorting & filters
                        onChildListener.onChildChanged(dataSnapshot, s, index);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        CustomDictionaryModel item = mFirebaseWrapper.getCustomDictionaryWord(dataSnapshot);
                        for (int i = 0; i < mItems.size(); i++) {
                            if (mItems.get(i).getUID().equals(item.getUID())){
                                // mItems.remove(i);
                                break;
                            }
                        }


                        onChildListener.onChildRemoved(dataSnapshot,  -1);
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

    public CustomDictionaryModel getItem (String wordUID, final OnValueEventListener onValueEventListener){
        final CustomDictionaryModel[] item = {null};
        mDatabaseReference
                .child(mPath)
                .child(wordUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        item[0] = mFirebaseWrapper.getCustomDictionaryWord(dataSnapshot);
                        onValueEventListener.onDataChange(dataSnapshot, item[0]);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        onValueEventListener.onCancelled(databaseError, null);
                    }
        });
        return item[0];
    }

    private int getItemIndex (CustomDictionaryModel model, List<CustomDictionaryModel> items){
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getUID().equals(model.getUID())) {
                return i;
            }
        }
        return index;
    }

}
