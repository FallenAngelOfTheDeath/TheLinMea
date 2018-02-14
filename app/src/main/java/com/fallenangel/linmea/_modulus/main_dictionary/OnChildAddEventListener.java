/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/27/18 7:46 PM
 */

package com.fallenangel.linmea._modulus.main_dictionary;

/**
 * Created by NineB on 1/27/2018.
 */

public interface OnChildAddEventListener<T> {
    void doOnNext(T item);
    void doOnCompleted();
}