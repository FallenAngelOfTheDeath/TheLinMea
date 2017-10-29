package com.fallenangel.linmea.linmea.model;

import android.net.Uri;

import java.util.Date;

/**
 * Created by NineB on 10/10/2017.
 */

public class UserModel {

    private String mFirestName, mLastName, mBiography, mCity;
    private Date mBirthday, mDateOfCreation;
    private Uri mAvatarUri;

    public Uri getAvatarUri() {
        return mAvatarUri;
    }

    public void setAvatarUri(Uri avatarUri) {
        mAvatarUri = avatarUri;
    }

    public String getFirestName() {
        return mFirestName;
    }

    public void setFirestName(String firestName) {
        mFirestName = firestName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getBiography() {
        return mBiography;
    }

    public void setBiography(String biography) {
        mBiography = biography;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public Date getBirthday() {
        return mBirthday;
    }

    public void setBirthday(Date birthday) {
        mBirthday = birthday;
    }

    public Date getDateOfCreation() {
        return mDateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        mDateOfCreation = dateOfCreation;
    }
}
