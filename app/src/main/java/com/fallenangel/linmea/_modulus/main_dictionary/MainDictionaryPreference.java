/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/27/18 5:28 PM
 */

package com.fallenangel.linmea._modulus.main_dictionary;

import android.content.Context;

import com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey;
import com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode;
import com.fallenangel.linmea._modulus.prferences.utils.PreferenceUtils;

/**
 * Created by NineB on 1/27/2018.
 */

public class MainDictionaryPreference {
    private static int mRearrangement;

    public MainDictionaryPreference() {

    }

    public static int getRearrangement(Context context) {
        return PreferenceUtils.getIntPreference(context, PreferenceMode.MAIN_DICTIONARY_PAGE_1, PreferenceKey.REARRANGEMENT);
    }

    public static void setRearrangement(Context context, int rearrangement) {
        PreferenceUtils.putPreference(context, PreferenceMode.MAIN_DICTIONARY_PAGE_1, PreferenceKey.REARRANGEMENT, rearrangement);
    }
}
