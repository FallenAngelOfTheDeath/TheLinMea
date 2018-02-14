/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.main_dictionary;

import com.fallenangel.linmea._modulus.non.data.BaseModel;

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
