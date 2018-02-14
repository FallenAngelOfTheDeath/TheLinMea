/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.grammar.model;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by NineB on 1/17/2018.
 */

public class UserDataGrammar extends GrammarsList {


    private String category;
    private Boolean favorite, learned;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Boolean getLearned() {
        return learned;
    }

    public void setLearned(Boolean learned) {
        this.learned = learned;
    }

    public static void changeLearned (String to, Boolean v){
        FirebaseDatabase.getInstance().getReference()
                .child("grammar/user_data/"  + to + "/learned/")
                .setValue(v);
    }

    public static void changeFavorite (String to, Boolean v){
        FirebaseDatabase.getInstance().getReference()
                .child("grammar/user_data/" + to + "/favorite/")
                .setValue(v);
    }
}
