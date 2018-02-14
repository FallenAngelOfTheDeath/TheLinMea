/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.grammar.model;

/**
 * Created by NineB on 1/25/2018.
 */

public class GramNameAndCategory {
    private String grammar, category;

    public GramNameAndCategory() {
    }

    public GramNameAndCategory(String grammar, String category) {
        this.grammar = grammar;
        this.category = category;
    }

    public String getGrammar() {
        return grammar;
    }

    public void setGrammar(String grammar) {
        this.grammar = grammar;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
