package com.fallenangel.linmea._linmea.model;

/**
 * Created by NineB on 12/8/2017.
 */

public class MainDictionaryModel extends BaseModel {
    private String mWord, mTranslation;

    public MainDictionaryModel(String word, String translation) {
        mWord = word;
        mTranslation = translation;
    }

    public String getWord() {
        return mWord;
    }

    public void setWord(String word) {
        mWord = word;
    }

    public String getTranslation() {
        return mTranslation;
    }

    public void setTranslation(String translation) {
        mTranslation = translation;
    }
}
