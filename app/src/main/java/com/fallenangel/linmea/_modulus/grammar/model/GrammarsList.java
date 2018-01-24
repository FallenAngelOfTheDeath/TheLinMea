package com.fallenangel.linmea._modulus.grammar.model;

import com.fallenangel.linmea._linmea.model.BaseModel;

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