package com.fallenangel.linmea._modulus.grammar.model;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by NineB on 1/18/2018.
 */

public class FavoriteGrammarWrapper {

    public GrammarsList getGrammar (DataSnapshot dataSnapshot){

        String category = dataSnapshot.getKey();
        String name = (String) dataSnapshot.child(category).getValue();

        GrammarsList grammar = new GrammarsList();
        grammar.setGrammarName(category);
        grammar.setGrammarCategory(name);
        return grammar;
    }

}
