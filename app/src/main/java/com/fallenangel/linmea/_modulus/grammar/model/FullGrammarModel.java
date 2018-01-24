package com.fallenangel.linmea._modulus.grammar.model;

import com.fallenangel.linmea._linmea.model.BaseModel;

/**
 * Created by NineB on 1/18/2018.
 */

public class FullGrammarModel extends BaseModel{
    private String category, grammar;
    private Boolean favorite, learned;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGrammar() {
        return grammar;
    }

    public void setGrammar(String grammar) {
        this.grammar = grammar;
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

    public String toString(){
        return "Grammar category: " + getCategory() + ", Grammar name: " + getGrammar()
                //+ ", Fav: " + getFavorite().toString() + ", Learned: " + getLearned().toString()
                ;
    }
}
