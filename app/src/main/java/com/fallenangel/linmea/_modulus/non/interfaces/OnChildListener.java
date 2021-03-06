/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 9:01 PM
 */

package com.fallenangel.linmea._modulus.non.interfaces;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by NineB on 9/19/2017.
 */

public interface OnChildListener<T> {
    public void onChildAdded(DataSnapshot dataSnapshot, String s, int index);
    public void onChildChanged(DataSnapshot dataSnapshot, String s, int index);
    public void onChildRemoved(DataSnapshot dataSnapshot, int index);
    public void onChildMoved(DataSnapshot dataSnapshot, String s, int index);
    public void onCancelled(DatabaseError databaseError);
    public T getItem(DataSnapshot dataSnapshot);
    public String getSortedString();
}
