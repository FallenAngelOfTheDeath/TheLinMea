package com.fallenangel.linmea._linmea.interfaces;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by NineB on 10/8/2017.
 */

public interface OnValueEventListener<T> {
    public void onDataChange(DataSnapshot dataSnapshot, T mItem);
    public void onCancelled(DatabaseError databaseError, T mItem);
    public T getItem(DataSnapshot dataSnapshot);
}
