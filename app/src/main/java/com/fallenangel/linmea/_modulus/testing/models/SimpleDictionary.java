/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 9:01 PM
 */

package com.fallenangel.linmea._modulus.testing.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NineB on 1/11/2018.
 */

public class SimpleDictionary {

    private List<Word> dictionary = new ArrayList<>();

    public SimpleDictionary() {
    }

    public int getDictionarySize(){
        return dictionary.size();
    }

    public Word getDictionaryWord(int position){
        return dictionary.get(position);
    }

    public Boolean dictionaryIsEmpty(){
        return dictionary.isEmpty();
    }

    public List<Word> getDictionary() {
        return dictionary;
    }

    public void addWord(String uid, String word, String translation){
        Word newWord = new Word();
        newWord.setUid(uid);
        newWord.setWord(word);
        newWord.setTranslation(translation);
        dictionary.add(newWord);

    }
}
