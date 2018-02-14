/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/27/18 11:21 PM
 */

package com.fallenangel.linmea._modulus.main_dictionary;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by NineB on 1/27/2018.
 */

public interface OnNewChildEventListener {
    void onChildAdded(DataSnapshot dataSnapshot, String s);
    void onChildChanged(DataSnapshot dataSnapshot, String s);
    void onChildRemoved(DataSnapshot dataSnapshot);
    String getSortedString();
}