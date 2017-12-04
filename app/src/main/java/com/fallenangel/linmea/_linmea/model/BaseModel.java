package com.fallenangel.linmea._linmea.model;

/**
 * Created by NineB on 10/26/2017.
 */

public class BaseModel {
    private int mId;
    private String mUID;

    public BaseModel() {
    }

    public BaseModel(int id, String uid) {
        mId = id;
        mUID = uid;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getUID() {
        return mUID;
    }

    public void setUID(String uid) {
        mUID = uid;
    }
}
