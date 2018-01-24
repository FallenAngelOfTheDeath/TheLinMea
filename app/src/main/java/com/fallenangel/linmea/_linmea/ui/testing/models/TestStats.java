package com.fallenangel.linmea._linmea.ui.testing.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NineB on 1/21/2018.
 */

public class TestStats implements Parcelable {

    private int trueCounter, falseCounter, qCounter;
    //private HashMap<TestItem, String> wordsStat = new HashMap<>();
    private List<WordsStat> wordsStat = new ArrayList<>();

    public TestStats() {
    }


    protected TestStats(Parcel in) {
        trueCounter = in.readInt();
        falseCounter = in.readInt();
        qCounter = in.readInt();
        wordsStat = in.createTypedArrayList(WordsStat.CREATOR);
    }

    public static final Creator<TestStats> CREATOR = new Creator<TestStats>() {
        @Override
        public TestStats createFromParcel(Parcel in) {
            return new TestStats(in);
        }

        @Override
        public TestStats[] newArray(int size) {
            return new TestStats[size];
        }
    };

    public int getTrueCounter() {
        return trueCounter;
    }

//    public void setTrueCounter(int trueCounter) {
//        this.trueCounter = trueCounter;
//    }

    public int getFalseCounter() {
        return falseCounter;
    }

//    public void setFalseCounter(int falseCounter) {
//        this.falseCounter = falseCounter;
//    }

    public int incrementAndGetTrueCounter(){
        //incrementAndGetQCounter();
       return this.trueCounter++;
    }

    public int incrementAndGetFalseCounter(){
        //incrementAndGetQCounter();
        return this.falseCounter++;
    }

    public int getQCounter() {
        return qCounter;
    }

    public int incrementAndGetQCounter(){
        return this.qCounter++;
    }

    public List<WordsStat> getWordsStat() {
        return wordsStat;
    }

    public void setWordStat(TestItem item, String answ){
        WordsStat ws = new WordsStat(item, answ);
        wordsStat.add(ws);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(trueCounter);
        parcel.writeInt(falseCounter);
        parcel.writeInt(qCounter);
        parcel.writeTypedList(wordsStat);
    }
}
