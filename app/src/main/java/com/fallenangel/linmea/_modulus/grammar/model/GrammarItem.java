/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.grammar.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NineB on 1/25/2018.
 */

public class GrammarItem {
    String mGrammar, mDescription;
    List<String> mExamples = new ArrayList<>();

    public GrammarItem() {
    }

    public String getmGrammar() {
        return mGrammar;
    }

    public void setmGrammar(String mGrammar) {
        this.mGrammar = mGrammar;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public List<String> getmExamples() {
        return mExamples;
    }

    public void setmExamples(List<String> mExamples) {
        this.mExamples = mExamples;
    }
}

