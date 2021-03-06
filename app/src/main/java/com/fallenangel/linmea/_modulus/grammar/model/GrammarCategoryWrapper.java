/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.grammar.model;

import android.util.Log;

import com.fallenangel.linmea._modulus.non.Constant;
import com.google.firebase.database.DataSnapshot;

/**
 * Created by NineB on 1/17/2018.
 */

public class GrammarCategoryWrapper {
    public GrammarCategory getCategory (DataSnapshot dataSnapshot){
        String category = dataSnapshot.getKey();
        if (Constant.DEBUG == 1) Log.i("Wrapper", "Category: " + category);
        GrammarCategory grammarCategory = new GrammarCategory();
        grammarCategory.setCategory(category);
        return grammarCategory;
    }
}
