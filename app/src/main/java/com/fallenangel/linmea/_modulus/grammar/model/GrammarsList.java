/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.grammar.model;

import com.fallenangel.linmea._modulus.non.data.BaseModel;

/**
 * Created by NineB on 1/17/2018.
 */

public class GrammarsList extends BaseModel{

    private String grammarName, grammarCategory;

    public GrammarsList() {
    }

    public String getGrammarCategory() {
        return grammarCategory;
    }

    public void setGrammarCategory(String grammarCategory) {
        this.grammarCategory = grammarCategory;
    }

    public String getGrammarName() {
        return grammarName;
    }

    public void setGrammarName(String grammarName) {
        this.grammarName = grammarName;
    }

}
