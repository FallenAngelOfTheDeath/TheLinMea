package com.fallenangel.linmea._linmea.data.firebase;

import com.fallenangel.linmea._linmea.model.Word;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NineB on 1/8/2018.
 */

public class WordWrapper {

    private String uid, wordStr, translation;


    public Word getWord(DataSnapshot dataSnapshot){

        uid = (String) dataSnapshot.getKey();
        wordStr = (String) dataSnapshot.child("word").getValue();
        List<String> tr = (ArrayList<String>) dataSnapshot.child("translation").getValue();
//        translation = (String) dataSnapshot.child("translation").getValue();

        Word newWord = new Word();
        newWord.setUid(uid);
        newWord.setWord(wordStr);
        newWord.setTranslation(tr.get(0));
        return newWord;
    }

}
