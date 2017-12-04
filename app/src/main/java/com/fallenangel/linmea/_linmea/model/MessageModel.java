package com.fallenangel.linmea._linmea.model;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by NineB on 11/23/2017.
 */

public class MessageModel extends BaseModel {
    private int mStatus, mHidden;
    private String mText, mData, mSender, mImageUrl;
    private ArrayList<String> mReceiver;

    private Key mPublicEncryptKey, mPrivateEncryptKey;
    private KeyPairGenerator mKpg;
    private KeyPair mKp;

    public MessageModel() {
        try {
            mKpg = KeyPairGenerator.getInstance("RSA");
            mKpg.initialize(1024);
            mKp = mKpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        mData = data;
    }

    public int getHidden() {
        return mHidden;
    }

    public void setHidden(int hidden) {
        this.mHidden = hidden;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String sender) {
        mSender = sender;
    }

    public ArrayList<String> getReceiver() {
        return mReceiver;
    }

    public void setReceiver(ArrayList<String> receiver) {
        mReceiver = receiver;
    }


    public Key getPublicEncryptKey() {
        mPublicEncryptKey = mKp.getPublic();
        return mPublicEncryptKey;
    }

    public void setPublicEncryptKey(Key publicEncryptKey) {
        mPublicEncryptKey = publicEncryptKey;
    }

    public Key getPrivateEncryptKey() {
        mPrivateEncryptKey = mKp.getPrivate();
        return mPrivateEncryptKey;
    }

    public void setPrivateEncryptKey(Key privateEncryptKey) {
        mPrivateEncryptKey = privateEncryptKey;
    }
}
