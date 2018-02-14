/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.testing.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NineB on 1/21/2018.
 */

public class TestItem implements Parcelable {
    private String answer, uid, cA;


    public TestItem() {
    }

    public TestItem(String answer, String uid) {
        this.answer = answer;
        this.uid = uid;
    }

    protected TestItem(Parcel in) {
        answer = in.readString();
        uid = in.readString();
        cA = in.readString();
    }

    public static final Creator<TestItem> CREATOR = new Creator<TestItem>() {
        @Override
        public TestItem createFromParcel(Parcel in) {
            return new TestItem(in);
        }

        @Override
        public TestItem[] newArray(int size) {
            return new TestItem[size];
        }
    };

    public String getcA() {
        return cA;
    }

    public void setcA(String cA) {
        this.cA = cA;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(answer);
        parcel.writeString(uid);
        parcel.writeString(cA);
    }
}
