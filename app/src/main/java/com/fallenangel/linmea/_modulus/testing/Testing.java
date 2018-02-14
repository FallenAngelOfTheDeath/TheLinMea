/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 9:01 PM
 */

package com.fallenangel.linmea._modulus.testing;

import android.os.Parcel;
import android.os.Parcelable;

import com.fallenangel.linmea._modulus.testing.models.TestItem;
import com.fallenangel.linmea._modulus.testing.models.TestStats;

/**
 * Created by NineB on 1/21/2018.
 */

public class Testing  implements Parcelable {

    private TestStats stats = new TestStats();

    public Testing() {
    }

    protected Testing(Parcel in) {
        stats = in.readParcelable(TestStats.class.getClassLoader());
    }

    public static final Creator<Testing> CREATOR = new Creator<Testing>() {
        @Override
        public Testing createFromParcel(Parcel in) {
            return new Testing(in);
        }

        @Override
        public Testing[] newArray(int size) {
            return new Testing[size];
        }
    };

    public TestStats getTestStats (){
        return stats;
    }

    public TestStats putAnswerStat(TestItem question, String answerUid){
        if (checkAnswer(question, answerUid))
            stats.incrementAndGetTrueCounter();
        else
            stats.incrementAndGetFalseCounter();
        return stats;
    }

    private Boolean checkAnswer(TestItem question, String answerUid){
        return question.getUid().equals(answerUid);
    }

    public int getCountComplitedQuestion(){
        return stats.getQCounter();
    }

//    public Map<TestItem, String> getDetailStatInfo(){
//        return savedQInfo;
//    }


    public TestStats getStats() {
        return stats;
    }

//    public TestStats putAnswerStatWithSavingDetailInformation (TestItem question, String answerUid){
//        if (checkAnswer(question, answerUid)) {
//            stats.incrementAndGetTrueCounter();
//            stats.setWordStat(question, answerUid);
//        } else {
//            stats.incrementAndGetFalseCounter();
//            stats.setWordStat(question, answerUid);
//        }
//        return stats;
//    }

//    public TestStats skipQuestion(TestItem question, String answerUid){
//        stats.incrementAndGetFalseCounter();
//        stats.setWordStat(question, answerUid);
//        return stats;
//    }

    public TestStats skipQuestionWithoutAnswer(TestItem question){
        stats.incrementAndGetFalseCounter();
        stats.incrementAndGetQCounter();
        stats.setWordStat(question, "No Answer");
        return stats;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.stats, i);
    }

}
