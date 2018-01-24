package com.fallenangel.linmea._linmea.data.firebase;

import com.fallenangel.linmea._linmea.model.MessageModel;
import com.fallenangel.linmea._linmea.model.UserModel;
import com.fallenangel.linmea._linmea.model.UserModel.FriendListModel;
import com.fallenangel.linmea._modulus.auth.User;
import com.google.firebase.database.DataSnapshot;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

import static com.fallenangel.linmea._linmea.model.UserModel.USER_AVATAR_URL;
import static com.fallenangel.linmea._linmea.model.UserModel.USER_EMAIL;
import static com.fallenangel.linmea._linmea.model.UserModel.USER_NICKNAME;
import static com.fallenangel.linmea._linmea.model.UserModel.USER_PRIV_STATUS;

/**
 * Created by NineB on 11/24/2017.
 */

public class FirebaseSocietyWrapper {

    private int mStatus;
    private Boolean mHidden;
    private String mEncodedText, mText, mData, mSender, mImageUrl;
    private Long mTime;
    private ArrayList<String> mReceiver;
    private Key mPublicEncryptKey;

    public MessageModel getChatMessage(DataSnapshot dataSnapshot) {

        //mEncodedText = (String) dataSnapshot.child("text").getValue();
        mText = (String) dataSnapshot.child("text").getValue();
        mSender = (String) dataSnapshot.child("sender").getValue();
        mData = (String) dataSnapshot.child("data").getValue();
        if (dataSnapshot.child("hidden").child(User.getCurrentUserUID()).getValue() != null)
        mHidden = (Boolean) dataSnapshot.child("hidden").child(User.getCurrentUserUID()).getValue();
        mTime = (Long) dataSnapshot.child("time").getValue();
        //mPublicEncryptKey = (Key) dataSnapshot.child("publicEncryptKey").getValue();
//        byte[] decodedBytes = null;
//        try {
//            Cipher c = Cipher.getInstance("RSA");
//            c.init(Cipher.DECRYPT_MODE, mPublicEncryptKey);
//            decodedBytes = c.doFinal(mEncodedText.getBytes());
//        } catch (Exception e) {
//            Log.e("Crypto", "RSA decryption error");
//        }


        MessageModel message = new MessageModel();
        message.setSender(mSender);
        message.setText(mText);
        message.setData(mData);
        return message;
    }

    public FriendListModel getFriendsList(DataSnapshot dataSnapshot){

        String friendsUID = (String) dataSnapshot.getKey();
        Boolean friendStatus = (Boolean) dataSnapshot.child(friendsUID).getValue();

        FriendListModel friends = new FriendListModel();

        friends.setUID(friendsUID);
        friends.setFriendStatus(friendStatus);

        return friends;
    }



    public UserModel.MainUserModel getPubUserData(DataSnapshot dataSnapshot){

        //String userUID = (String) dataSnapshot.getKey();
        String userEmail = (String) dataSnapshot.child(USER_EMAIL).getValue();
        String userNickName = (String) dataSnapshot.child(USER_NICKNAME).getValue();
        Boolean userPrivStatus = (Boolean) dataSnapshot.child(USER_PRIV_STATUS).getValue();
        String userAvatarUrl = (String) dataSnapshot.child(USER_AVATAR_URL).getValue();

        UserModel.MainUserModel user = new UserModel.MainUserModel();
        user.setEmail(userEmail);
        user.setNickName(userNickName);
        user.setPrivStatus(userPrivStatus);
        user.setAvatar_url(userAvatarUrl);
        return user;
    }

    public UserModel.PrivUserModel getPrivUserData(DataSnapshot dataSnapshot){

        String userUid = (String) dataSnapshot.getKey();
        String userFirstName = (String) dataSnapshot.child(UserModel.USER_FIRST_NAME).getValue();
        String userMiddleName = (String) dataSnapshot.child(UserModel.USER_MIDDLE_NAME).getValue();
        String userLastName = (String) dataSnapshot.child(UserModel.USER_LAST_NAME).getValue();
        String userGender = (String) dataSnapshot.child(UserModel.USER_GENDER).getValue();
        String userCountry = (String) dataSnapshot.child(UserModel.USER_COUNTRY).getValue();
        String userCity = (String) dataSnapshot.child(UserModel.USER_CITY).getValue();
        String userBiography = (String) dataSnapshot.child(UserModel.USER_BIOGRAPHY).getValue();
        Date userBirthday = (Date) dataSnapshot.child(UserModel.USER_BIRTHDAY).getValue();
        Date userDateOfCreation = (Date) dataSnapshot.child(UserModel.USER_DATE_OF_CREATION).getValue();


        UserModel.PrivUserModel user = new UserModel.PrivUserModel();
        user.setUID(userUid);
        user.setFirstName(userFirstName);
        user.setMiddleName(userMiddleName);
        user.setLastName(userLastName);
        user.setGender(userGender);
        user.setCountry(userCountry);
        user.setCity(userCity);
        user.setBiography(userBiography);
        user.setBirthday(userBirthday);
        user.setDateOfCreation(userDateOfCreation);
        return user;
    }


}
