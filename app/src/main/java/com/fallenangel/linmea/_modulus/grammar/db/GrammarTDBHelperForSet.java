package com.fallenangel.linmea._modulus.grammar.db;

import com.fallenangel.linmea._modulus.grammar.model.GrammarsList;
import com.fallenangel.linmea._modulus.non.interfaces.OnSimpleChildEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by NineB on 1/17/2018.
 */

public class GrammarTDBHelperForSet<T extends GrammarsList> implements ChildEventListener {

    private static final String TAG = "GrammarTDBHelper";

    private DatabaseReference dbRef;
    private List<T> mItems;
    private Set<T> mInputSet;
    private OnSimpleChildEventListener<T> onSimpleChildEventListener;
    private ChildEventListener childEventListener = null;

    public GrammarTDBHelperForSet(Set<T> items, OnSimpleChildEventListener onSimpleChildEventListener) {
        this.dbRef = FirebaseDatabase.getInstance().getReference();
        this.mInputSet = items;
        this.onSimpleChildEventListener = onSimpleChildEventListener;
    }

    public void loadData(String path){
        childEventListener = dbRef.child(path).orderByKey().addChildEventListener(this);
    }

    public void removeChildEventListener(){
        if (childEventListener != null)
            dbRef.removeEventListener(this);
    }

    public void keepSynced(Boolean v){
        dbRef.keepSynced(true);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        T item = onSimpleChildEventListener.getItem(dataSnapshot);
        mInputSet.add(item);
        convertSetToList(mInputSet);
        onSimpleChildEventListener.onDataChange(dataSnapshot);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        T item = onSimpleChildEventListener.getItem(dataSnapshot);
        int index = getItemIndex(item, mItems);
        if (item != null)
            mItems.set(index, item);
        onSimpleChildEventListener.onChildChanged(dataSnapshot, s);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        onSimpleChildEventListener.onCancelled(databaseError);
    }

    private int getItemIndex (T model, List<T> items){
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getGrammarName().equals(model.getGrammarName())) {
                return i;
            }
        }
        return index;
    }

    private List<T> convertSetToList (Set<T> inputSet){
        List<T> list = new ArrayList<>();
        list.addAll(inputSet);
        return list;
    }

}
