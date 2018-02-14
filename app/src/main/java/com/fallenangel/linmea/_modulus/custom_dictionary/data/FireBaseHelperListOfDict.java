/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/27/18 11:01 PM
 */

package com.fallenangel.linmea._modulus.custom_dictionary.data;

import com.fallenangel.linmea._modulus.main_dictionary.OnNewChildEventListener;
import com.fallenangel.linmea._modulus.non.adapter.AbstractRecyclerViewAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by NineB on 1/27/2018.
 */

public class FireBaseHelperListOfDict {

    private AbstractRecyclerViewAdapter mAdapter;
    private List<MyDictionaryModel> mItems;
    private String mPath;
    DatabaseReference mDatabaseReference;
    private FirebaseDictionaryWrapper mFirebaseDictionaryWrapper;
    private String sortedString = "";

    public FireBaseHelperListOfDict (DatabaseReference dbr, String path, List<MyDictionaryModel> items, AbstractRecyclerViewAdapter adapter) {
        this.mPath = path;
        this.mItems = items;
        this.mAdapter = adapter;
        this.mDatabaseReference = dbr;
        mFirebaseDictionaryWrapper = new FirebaseDictionaryWrapper();
    }


    public void getList(final OnNewChildEventListener onChildListener){
        mDatabaseReference.child(mPath).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyDictionaryModel item = mFirebaseDictionaryWrapper.getDictionaryItem(dataSnapshot);
                if (item != null)
                    mItems.add(item);
                sortedString = onChildListener.getSortedString(); ///<- remove from here
                if (sortedString == null) sortedString = "";
                mAdapter.setItems(mItems, sortedString);
                onChildListener.onChildAdded(dataSnapshot, s);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                MyDictionaryModel item = mFirebaseDictionaryWrapper.getDictionaryItem(dataSnapshot);
                int index = getItemIndex(item, mItems);
                if (item != null)
                    mItems.set(index, item);
                mAdapter.setItems(mItems, "");
                mAdapter.notifyItemRangeChanged(0, mItems.size());
                mAdapter.notifyDataSetChanged();
                onChildListener.onChildChanged(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                MyDictionaryModel item = mFirebaseDictionaryWrapper.getDictionaryItem(dataSnapshot);
                for (int i = 0; i < mItems.size(); i++) {
                    if (mItems.get(i).getName().equals(item.getName())){
                        mItems.remove(i);
                        mAdapter.notifyItemRangeChanged(i, mItems.size());
                        mAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
                onChildListener.onChildRemoved(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getItemIndex (MyDictionaryModel model, List<MyDictionaryModel> items){
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(model.getName())) {
                return i;
            }
        }
        return index;
    }

}
