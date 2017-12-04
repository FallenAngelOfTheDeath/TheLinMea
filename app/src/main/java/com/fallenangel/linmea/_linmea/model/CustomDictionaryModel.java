package com.fallenangel.linmea._linmea.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NineB on 9/17/2017.
 */

public class CustomDictionaryModel extends BaseModel{
    //private Long mId;mUID
    private String mWord, mTranslationString, mDescription;
    private Boolean mFavorite, mStatus;

    private ArrayList<String> mTranslation;
    private List<String> mLinked, mTags;

    public CustomDictionaryModel() {
    }

    public CustomDictionaryModel(String word,
                                 ArrayList<String> translation,
                                 String description,
                                 Boolean favorite,
                                 Boolean status) {

        this.mWord = word;
        this.mTranslation = translation;
        this.mDescription = description;
        this.mFavorite = favorite;
        this.mStatus = status;
    }

    public String getWord() {
        return mWord;
    }

    public void setWord(String word) {
        mWord = word;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Boolean getFavorite() {
        return mFavorite;
    }

    public void setFavorite(Boolean favorite) {
        mFavorite = favorite;
    }

    public Boolean getStatus() {
        return mStatus;
    }

    public void setStatus(Boolean status) {
        mStatus = status;
    }




    public ArrayList<String> getTranslation() {
        return mTranslation;
    }

    //only for add to firebase
    public void setTranslation(ArrayList<String> translation) {
        mTranslation = translation;
    }




    public String getTranslationString() {
        return mTranslationString;
    }

    public void setTranslationString(String translationString) {
        mTranslationString = translationString;
    }

    public List<String> getLinked() {
        return mLinked;
    }

    public void setLinked(List<String> linked) {
        mLinked = linked;
    }

    public List<String> getTags() {
        return mTags;
    }

    public void setTags(List<String> tags) {
        mTags = tags;
    }
}
