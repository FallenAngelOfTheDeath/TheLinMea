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
