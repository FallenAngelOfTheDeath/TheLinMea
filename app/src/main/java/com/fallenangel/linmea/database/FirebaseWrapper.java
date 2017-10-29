package com.fallenangel.linmea.database;

import com.fallenangel.linmea.linmea.model.UserModel;
import com.fallenangel.linmea.model.CustomDictionaryModel;
import com.fallenangel.linmea.model.MyDictionaryModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * Created by NineB on 9/19/2017.
 */

public class FirebaseWrapper {

    private String mTranslations, mUID;
    private ArrayList<String> mTranslationList;

    public CustomDictionaryModel getCustomDictionaryWord(DataSnapshot dataSnapshot){

        Long mIdLong = (Long) dataSnapshot.child("id").getValue();
        int mId = mIdLong != null ? mIdLong.intValue() : null;

        mTranslationList = (ArrayList<String>) dataSnapshot.child("translation").getValue();

        if (mTranslationList != null && !mTranslationList.isEmpty()) {
            mUID = dataSnapshot.getKey();

            mTranslations = "";
            for (int i = 0; i < mTranslationList.size() ; i++) {
                mTranslations = mTranslations + ", " + mTranslationList.get(i);
            }
            mTranslations = mTranslations.replaceFirst(", ", "");
        }
        String mWord = (String) dataSnapshot.child("word").getValue();
        String mDescription = (String) dataSnapshot.child("description").getValue();
        Boolean mStatus = (Boolean) dataSnapshot.child("status").getValue();
        Boolean mFavorite = (Boolean) dataSnapshot.child("favorite").getValue();

        CustomDictionaryModel word = new CustomDictionaryModel();

        word.setId(mId);
        word.setUID(mUID);
        word.setWord(mWord);
        word.setTranslationString(mTranslations);
        word.setDescription(mDescription);
        word.setStatus(mStatus);
        word.setFavorite(mFavorite);
        return word;
    }


    public MyDictionaryModel getDictionaryItem (DataSnapshot dataSnapshot){
        String mName = dataSnapshot.getKey();
        String mDescription = (String) dataSnapshot.child("Description").getValue();

        MyDictionaryModel dictionary = new MyDictionaryModel();

        dictionary.setName(mName);
        dictionary.setDescription(mDescription);

        return dictionary;
    }

    public UserModel getUserMainMetadata (DataSnapshot dataSnapshot){
        String mFirstName = (String) dataSnapshot.child("first_name").getValue();
        String mLastName = (String) dataSnapshot.child("last_name").getValue();
        String mPrivetStatus = (String) dataSnapshot.child("privet_status").getValue();

        UserModel user = new UserModel();

        user.setFirestName(mFirstName);
        user.setLastName(mLastName);
        return user;
    }
}
