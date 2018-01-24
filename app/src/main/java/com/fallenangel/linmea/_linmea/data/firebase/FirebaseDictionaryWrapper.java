package com.fallenangel.linmea._linmea.data.firebase;

import com.fallenangel.linmea._linmea.model.UserModel;
import com.fallenangel.linmea._linmea.model.CustomDictionaryModel;
import com.fallenangel.linmea._linmea.model.MyDictionaryModel;
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
            for (int i = 0; i < mTranslationList.size() ; i++) {
                mTranslations = mTranslations + ", " + mTranslationList.get(i);
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
