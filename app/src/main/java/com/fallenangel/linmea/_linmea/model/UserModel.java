package com.fallenangel.linmea._linmea.model;

import android.net.Uri;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NineB on 10/10/2017.
 */

public class UserModel {

    public static final String USER_UID = "uid";
    public static final String USER_EMAIL = "email";
    public static final String USER_NICKNAME = "nickname";
    public static final String USER_PRIV_STATUS = "private_status";
    public static final String USER_AVATAR_URL = "avatar_url";

    public static final String USER_FIRST_NAME = "first_name";
    public static final String USER_MIDDLE_NAME = "last_name";
    public static final String USER_LAST_NAME = "nick_name";
    public static final String USER_GENDER = "gender";
    public static final String USER_FRIENDS = "friends";
    public static final String USER_DATE_OF_CREATION = "date_of_creation";
    public static final String USER_COUNTRY = "country";
    public static final String USER_CITY = "city";
    public static final String USER_BIRTHDAY = "birthday";
    public static final String USER_BIOGRAPHY= "biography";

    public static class MainUserModel extends BaseModel {
        private String mNickName, mEmail, mUID;
        private Boolean mPrivStatus;
        private String avatar_url;

        public MainUserModel() {
        }



        public String getAvatar_url() {
            return avatar_url;
        }

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }

        public String getNickName() {
            return mNickName;
        }

        public void setNickName(String nickName) {
            mNickName = nickName;
        }

        public String getEmail() {

            return mEmail;
        }

        public void setEmail(String email) {
            mEmail = email;
        }

        public String getUID() {
            return mUID;
        }

        public void setUID(String UID) {
            mUID = UID;
        }

        public Boolean getPrivStatus() {
            return mPrivStatus;
        }

        public void setPrivStatus(Boolean privStatus) {
            mPrivStatus = privStatus;
        }
    }

    public static class PrivUserModel extends BaseModel{

        private String mFirstName, mLastName, mMiddleName, mGender, mBiography, mCity, mCountry;
        private Date mDateOfCreation, mBirthday;
        private Map<String, Boolean> mFriends, mPubliceDictionaries;

        public PrivUserModel() {
            mFriends = new HashMap<>();
            mPubliceDictionaries = new HashMap<>();
        }

        public String getFirstName() {
            return mFirstName;
        }

        public void setFirstName(String firstName) {
            mFirstName = firstName;
        }

        public String getLastName() {
            return mLastName;
        }

        public void setLastName(String lastName) {
            mLastName = lastName;
        }

        public String getMiddleName() {
            return mMiddleName;
        }

        public void setMiddleName(String middleName) {
            mMiddleName = middleName;
        }

        public String getGender() {
            return mGender;
        }

        public void setGender(String gender) {
            mGender = gender;
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

        public String getCountry() {
            return mCountry;
        }

        public void setCountry(String country) {
            mCountry = country;
        }

        public Date getDateOfCreation() {
            return mDateOfCreation;
        }

        public void setDateOfCreation(Date dateOfCreation) {
            mDateOfCreation = dateOfCreation;
        }

        public Date getBirthday() {
            return mBirthday;
        }

        public void setBirthday(Date birthday) {
            mBirthday = birthday;
        }

        public Map<String, Boolean> getFriends() {
            return mFriends;
        }

        public void setFriends(Map<String, Boolean> friends) {
            mFriends = friends;
        }

        public Map<String, Boolean> getPubliceDictionaries() {
            return mPubliceDictionaries;
        }

        public void setPubliceDictionaries(Map<String, Boolean> publiceDictionaries) {
            mPubliceDictionaries = publiceDictionaries;
        }
    }

    public static class FriendListModel extends BaseModel {

        private Boolean mFriendStatus;

        public FriendListModel() {
        }

        public Boolean getFriendStatus() {
            return mFriendStatus;
        }

        public void setFriendStatus(Boolean friendStatus) {
            mFriendStatus = friendStatus;
        }
    }












    //Main Data
    private String mNickName, mEmail, mUID;
    private Boolean mPrivStatus, mFriendStatus;

    private String mFirstName, mLastName, mMiddleName;
    private Date mDateOfCreation;


    //Private Data
    private String mBiography, mCity, mGender;
    private Date mBirthday;
    private Uri mAvatarUri;

    public UserModel() {
    }

    public String getUID() {
        return mUID;
    }

    public void setUID(String UID) {
        mUID = UID;
    }

    public Uri getAvatarUri() {
        return mAvatarUri;
    }

    public void setAvatarUri(Uri avatarUri) {
        mAvatarUri = avatarUri;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirestName(String firstName) {
        mFirstName = firstName;
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

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public Boolean getPrivStatus() {
        return mPrivStatus;
    }

    public void setPrivStatus(Boolean privStatus) {
        mPrivStatus = privStatus;
    }

    public Boolean getFriendStatus() {
        return mFriendStatus;
    }

    public void setFriendStatus(Boolean friendStatus) {
        mFriendStatus = friendStatus;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getMiddleName() {
        return mMiddleName;
    }

    public void setMiddleName(String middleName) {
        mMiddleName = middleName;
    }
}
