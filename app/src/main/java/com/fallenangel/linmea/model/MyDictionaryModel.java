package com.fallenangel.linmea.model;

import java.util.ArrayList;

/**
 * Created by NineB on 9/22/2017.
 */

public class MyDictionaryModel extends BaseModel {

    private String mName, mOwner, mDescription;
    private int mSize;

    private ArrayList<String> mSharedDictionaries;

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ArrayList<String> getSharedDictionaries() {
        return mSharedDictionaries;
    }

    public void setSharedDictionaries(ArrayList<String> sharedDictionaries) {
        mSharedDictionaries = sharedDictionaries;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }
}
