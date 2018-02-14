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
 * Created by NineB on 10/8/2017.
 */

public interface OnValueEventListener<T> {
    public void onDataChange(DataSnapshot dataSnapshot, T mItem);
    public void onCancelled(DatabaseError databaseError, T mItem);
    public T getItem(DataSnapshot dataSnapshot);
}
