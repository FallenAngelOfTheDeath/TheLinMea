package com.fallenangel.linmea._linmea.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by NineB on 1/8/2018.
 */

public class Word extends BaseModel implements Parcelable, Serializable{
    private String uid, word, translation;
    private int wordRepetitionCounter = 0;
    private int mistakCounter = 0, curreectCounter = 0;
    //private List<String> translList = new ArrayList<>();

    public Word() {
    }

    protected Word(Parcel in) {
        uid = in.readString();
        word = in.readString();
        translation = in.readString();
        wordRepetitionCounter = in.readInt();
        mistakCounter = in.readInt();
        curreectCounter = in.readInt();
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public int getWordRepetitionCounter() {
        return wordRepetitionCounter;
    }

    public void setWordRepetitionCounter(int wordRepetitionCounter) {
        this.wordRepetitionCounter = wordRepetitionCounter;
    }

    public void incrementRepetitionCounter(){
        wordRepetitionCounter++;
    }

    public int getMistakCounter() {
        return mistakCounter;
    }

    public void setMistakCounter(int mistakCounter) {
        this.mistakCounter = mistakCounter;
    }

    public int getCurreectCounter() {
        return curreectCounter;
    }

    public void setCurreectCounter(int curreectCounter) {
        this.curreectCounter = curreectCounter;
    }

    public void incrementMistakCounter(){
        this.mistakCounter++;
    }

    public void incrementCurrectCouinter(){
        this.curreectCounter++;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(word);
        parcel.writeString(translation);
        parcel.writeInt(wordRepetitionCounter);
        parcel.writeInt(mistakCounter);
        parcel.writeInt(curreectCounter);
    }
}
