package com.fallenangel.linmea._linmea.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by NineB on 1/17/2018.
 */

public class Translation extends BaseModel implements Parcelable {
    private String sourceText, translatedText;

    public Translation() {
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }


    public HashMap<String, String> toMap(){
        HashMap<String, String> translationMap = new HashMap<>();
        translationMap.put("source_text", getSourceText());
        translationMap.put("translated_text", getTranslatedText());
        return translationMap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sourceText);
        parcel.writeString(translatedText);
    }

    public static final Parcelable.Creator<Translation> CREATOR = new Parcelable.Creator<Translation>() {

        @Override
        public Translation createFromParcel(Parcel parcel) {
            return new Translation(parcel);
        }

        @Override
        public Translation[] newArray(int i) {
            return new Translation[i];
        }
    };

    public Translation(Parcel in) {
        this.sourceText = in.readString();
        this.translatedText = in.readString();
    }
}
