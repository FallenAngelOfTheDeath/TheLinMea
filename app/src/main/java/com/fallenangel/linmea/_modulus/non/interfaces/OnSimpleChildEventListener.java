package com.fallenangel.linmea._modulus.non.interfaces;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by NineB on 1/17/2018.
 */

public interface OnSimpleChildEventListener<T> {
    void onDataChange(DataSnapshot dataSnapshot);
    void onChildChanged(DataSnapshot dataSnapshot, String s);
    void onCancelled(DatabaseError databaseError);
    T getItem(DataSnapshot dataSnapshot);

}
