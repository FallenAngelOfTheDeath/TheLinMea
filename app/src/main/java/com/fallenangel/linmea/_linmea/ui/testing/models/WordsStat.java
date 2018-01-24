package com.fallenangel.linmea._linmea.ui.testing.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NineB on 1/21/2018.
 */

public class WordsStat implements Parcelable {
    TestItem item;
    String urAnsw;

    public WordsStat(TestItem item, String urAnsw) {
        this.item = item;
        this.urAnsw = urAnsw;

    }

    public TestItem getItem() {
        return item;
    }

    public String getUrAnsw() {
        return urAnsw;
    }

    protected WordsStat(Parcel in) {
        item = in.readParcelable(TestItem.class.getClassLoader());
        urAnsw = in.readString();
    }

    public static final Creator<WordsStat> CREATOR = new Creator<WordsStat>() {
        @Override
        public WordsStat createFromParcel(Parcel in) {
            return new WordsStat(in);
        }

        @Override
        public WordsStat[] newArray(int size) {
            return new WordsStat[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(item, i);
        parcel.writeString(urAnsw);
    }
}
