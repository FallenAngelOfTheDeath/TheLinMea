/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/28/18 7:28 PM
 */

package com.fallenangel.linmea._modulus.custom_dictionary.data;

import com.fallenangel.linmea._modulus.custom_dictionary.model.CustomDictionaryModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NineB on 9/19/2017.
 */

public class FirebaseDictionaryWrapper {

    private String mTranslations, mUID;
    private ArrayList<String> mTranslationList;

    public CustomDictionaryModel getCustomDictionaryWord(DataSnapshot dataSnapshot){

      //  Long mIdLong = (Long) dataSnapshot.child("id").getValue();
//        int mId = mIdLong != null ? mIdLong.intValue() : null;

        mTranslationList = (ArrayList<String>) dataSnapshot.child("translation").getValue();

        if (mTranslationList != null && !mTranslationList.isEmpty()) {
            mUID = dataSnapshot.getKey();

            mTranslations = "";
//            for (int i = 0; i < mTranslationList.size() ; i++) {
//                mTranslations = mTranslations + ", " + mTranslationList.get(i);
//            }
            try {
                for (String transl:mTranslationList) {
                    mTranslations = mTranslations + ", " +transl;
                }
            } catch (IndexOutOfBoundsException e){
                throw e;
            }


            mTranslations = mTranslations.replaceFirst(", ", "");
        }
        String mWord = (String) dataSnapshot.child("word").getValue();
        String mDescription = (String) dataSnapshot.child("description").getValue();
        Boolean mStatus = (Boolean) dataSnapshot.child("status").getValue();
        Boolean mFavorite = (Boolean) dataSnapshot.child("favorite").getValue();
        List<String> mLinked = (ArrayList<String>) dataSnapshot.child("linked").getValue();
        List<String> mTags = (ArrayList<String>) dataSnapshot.child("tags").getValue();


        CustomDictionaryModel word = new CustomDictionaryModel();

      //  word.setId(mId);
        word.setUID(mUID);
        word.setWord(mWord);
        word.setTranslation(mTranslationList);
        word.setTranslationString(mTranslations);
        word.setDescription(mDescription);
        word.setStatus(mStatus);
        word.setFavorite(mFavorite);
        word.setLinked(mLinked);
        word.setTags(mTags);
        return word;
    }


    public MyDictionaryModel getDictionaryItem (DataSnapshot dataSnapshot){
        String mName = dataSnapshot.getKey();
        String mDescription = (String) dataSnapshot.child("description").getValue();
       // String mName = (String) dataSnapshot.child("name").getValue();

        MyDictionaryModel dictionary = new MyDictionaryModel();

        dictionary.setName(mName);
        dictionary.setDescription(mDescription);
       /// dictionary.setUID(mUID);
        return dictionary;
    }

}
