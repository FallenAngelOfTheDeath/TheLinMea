package com.fallenangel.linmea._modulus.grammar.model;

import android.util.Log;

import com.fallenangel.linmea._modulus.non.Constant;
import com.google.firebase.database.DataSnapshot;

/**
 * Created by NineB on 1/17/2018.
 */

public class GrammarListWrapper {
    public GrammarsList getGrammar (DataSnapshot dataSnapshot, String category){
        String name = dataSnapshot.getKey();

        if (Constant.DEBUG == 1) Log.i("Wrapper", "Category: " + category + ", Name: " + name);

        GrammarsList grammar = new GrammarsList();
        grammar.setGrammarCategory(category);
        grammar.setGrammarName(name);
        return grammar;
    }
}
