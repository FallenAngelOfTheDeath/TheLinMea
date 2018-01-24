package com.fallenangel.linmea._modulus.grammar.model;

import android.util.Log;

import com.fallenangel.linmea._modulus.non.Constant;
import com.google.firebase.database.DataSnapshot;

/**
 * Created by NineB on 1/17/2018.
 */

public class UserDataGrammarWrapper {
    public UserDataGrammar getUserDataGrammar (DataSnapshot dataSnapshot){
        String name = dataSnapshot.getKey();
        Boolean learn = (Boolean) dataSnapshot.child("learned").getValue();
        Boolean fav = (Boolean) dataSnapshot.child("favorite").getValue();

        if (Constant.DEBUG == 1) {
            String l, f;
            if (learn == null)
                l = "false";
            else
                 l = learn.toString();

             if (fav == null)
                 f = "false";
             else
                 f = fav.toString();
            Log.i("Wrapper", "Name: " + name + ", Leaned: " + l  + ", Favorite: " + f);
        }

        UserDataGrammar userData = new UserDataGrammar();
        userData.setGrammarName(name);
        userData.setFavorite(fav);
        userData.setLearned(learn);
        return userData;
    }
}
