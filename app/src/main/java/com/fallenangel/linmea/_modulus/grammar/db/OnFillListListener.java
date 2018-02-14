/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 2/6/18 9:20 PM
 */

package com.fallenangel.linmea._modulus.grammar.db;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by NineB on 2/6/2018.
 */

public interface OnFillListListener<T>{
    void doOnNext(T item);
    void doOnCompleted();
    T itemWrapper(DataSnapshot dataSnapshot);
}