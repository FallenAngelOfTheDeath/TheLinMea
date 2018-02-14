/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.grammar.model;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * Created by NineB on 1/25/2018.
 */

public class GrammarItemWrapper {

    private String mName, mDescription;
    private ArrayList<String> mExamples;

    public GrammarItem getGrammar(DataSnapshot dataSnapshot){
        mName = (String) dataSnapshot.getKey();
        mDescription = (String) dataSnapshot.child("description").getValue();
        mExamples = (ArrayList<String>) dataSnapshot.child("examples").getValue();
        GrammarItem grammarItem = new GrammarItem();
        grammarItem.setmGrammar(mName);
        grammarItem.setmDescription(mDescription);
        grammarItem.setmExamples(mExamples);
        return grammarItem;
    }

}
