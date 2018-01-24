package com.fallenangel.linmea._linmea.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fallenangel.linmea._modulus.auth.User;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NineB on 1/8/2018.
 */

public class TestConfig implements Parcelable {

    //Constants
    public static final int QUESTIONS_LIM = 0;
    public static final int WORD_REP_LIM = 1;
    public static final int CORRECT_WORD_LIM = 2;

    public static final int CHOOSING_ANSWER = 0;
    public static final int MANUAL_INPUT = 1;

    public static final int WORD_TRANSLATION = 0;
    public static final int TRANSLATION_WORD = 1;


    private String dictionaryName;
    private int answerCount, inputType, timer, testingSide, dictSize, testType,
            questionCounter, wordRepetitionCounter, correctWordsCounter;
    private Boolean allWords;
    private List<String> pickedWordUids = new ArrayList<>();
    // private List<String> wordsUids = new ArrayList<>();

    public TestConfig() {
    }

    protected TestConfig(Parcel in) {
        dictionaryName = in.readString();
        answerCount = in.readInt();
        inputType = in.readInt();
        timer = in.readInt();
        testingSide = in.readInt();
        dictSize = in.readInt();
        testType = in.readInt();
        questionCounter = in.readInt();
        wordRepetitionCounter = in.readInt();
        correctWordsCounter = in.readInt();
        byte tmpAllWords = in.readByte();
        allWords = tmpAllWords == 0 ? null : tmpAllWords == 1;
        pickedWordUids = in.createStringArrayList();
    }

    public static final Creator<TestConfig> CREATOR = new Creator<TestConfig>() {
        @Override
        public TestConfig createFromParcel(Parcel in) {
            return new TestConfig(in);
        }

        @Override
        public TestConfig[] newArray(int size) {
            return new TestConfig[size];
        }
    };

    public String getDictionaryName() {
        return dictionaryName;
    }

    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void updAnswerCount(int answerCount) {
        this.answerCount = answerCount;
        FirebaseDatabase.getInstance().getReference().child("custom_dictionary/" + User.getCurrentUserUID() +
                "/meta_data/" + getDictionaryName() + "/test/answer_count")
                .setValue(answerCount);
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public int getInputType() {
        return inputType;
    }

    public void updInputType(int inputType) {
        this.inputType = inputType;
        FirebaseDatabase.getInstance().getReference().child("custom_dictionary/" + User.getCurrentUserUID() +
                "/meta_data/" + getDictionaryName() + "/test/input_type").setValue(inputType);
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public int getTimer() {
        return timer;
    }

    public void updTimer(int timer) {
        this.timer = timer;
        FirebaseDatabase.getInstance().getReference().child("custom_dictionary/" + User.getCurrentUserUID() +
                "/meta_data/" + getDictionaryName() + "/test/timer").setValue(timer);
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getTestingSide() {
        return testingSide;
    }

    public void updTestingSide(int testingSide) {
        this.testingSide = testingSide;
        FirebaseDatabase.getInstance().getReference().child("custom_dictionary/" + User.getCurrentUserUID() +
                "/meta_data/" + getDictionaryName() + "/test/test_side")
                .setValue(testingSide);
    }

    public void setTestingSide(int testingSide) {
        this.testingSide = testingSide;
    }

    public Boolean getAllWords() {
        return allWords;
    }

    public void setAllWords(Boolean allWords) {
        this.allWords = allWords;
    }

    public List<String> getPickedWordUids() {
        return pickedWordUids;
    }

    public void updPickedWordUids(List<String> pickedWordUids) {
        this.pickedWordUids = pickedWordUids;
        FirebaseDatabase.getInstance().getReference().child("custom_dictionary/" + User.getCurrentUserUID() +
                "/meta_data/" + getDictionaryName() + "/test/picked_word_uids")
                .setValue(pickedWordUids);
    }
    public void deletPickedWordUids() {
        this.pickedWordUids = pickedWordUids;
        FirebaseDatabase.getInstance().getReference().child("custom_dictionary/" + User.getCurrentUserUID() +
                "/meta_data/" + getDictionaryName() + "/test/picked_word_uids")
                .removeValue();
    }

    public void setPickedWordUids(List<String> pickedWordUids) {
        this.pickedWordUids = pickedWordUids;
    }

    public void clearPickedWordUids() {
        this.pickedWordUids.clear();
    }

    public int getQuestionCounter() {
        return questionCounter;
    }

    public void updQuestionCounter(int questionCounter) {
        this.questionCounter = questionCounter;
        FirebaseDatabase.getInstance().getReference().child("custom_dictionary/" + User.getCurrentUserUID() +
                "/meta_data/" + getDictionaryName() + "/test/question_counter")
                .setValue(questionCounter);

    }

    public void setQuestionCounter(int questionCounter) {
        this.questionCounter = questionCounter;
    }

    public int getWordRepetitionCounter() {
        return wordRepetitionCounter;
    }

    public void updWordRepetitionCounter(int wordRepetitionCounter) {
        this.wordRepetitionCounter = wordRepetitionCounter;
        FirebaseDatabase.getInstance().getReference().child("custom_dictionary/" + User.getCurrentUserUID() +
                "/meta_data/" + getDictionaryName() + "/test/word_repetition_counter")
                .setValue(wordRepetitionCounter);
    }

    public void setWordRepetitionCounter(int wordRepetitionCounter) {
        this.wordRepetitionCounter = wordRepetitionCounter;
    }

    public int getDictSize() {
        return dictSize;
    }

    public void setDictSize(int dictSize) {
        this.dictSize = dictSize;
    }

    //        public List<String> getWordsUids() {
//            return wordsUids;
//        }
//
//        public void setWordsUids(List<String> wordsUids) {
//            this.wordsUids = wordsUids;
//        }
//
//        public void addWordUid(String uid){
//            wordsUids.add(uid);
//        }

    public Boolean pickedWordUidsIsEmpty(){
        return pickedWordUids.isEmpty();
    }

    public int getPickedWordsSize(){
        return pickedWordUids.size();
    }

    public int getTestType() {
        return testType;
    }

    public void setTestType(int testType) {
        this.testType = testType;
    }

    public void updTestType(int testType) {
        this.testType = testType;
        FirebaseDatabase.getInstance().getReference().child("custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/"
                + getDictionaryName() + "/test/test_type")
                .setValue(testType);
    }

    public int getCorrectWordsCounter() {
        return correctWordsCounter;
    }

    public void setCorrectWordsCounter(int correctWordsCounter) {
        this.correctWordsCounter = correctWordsCounter;
        FirebaseDatabase.getInstance().getReference().child("custom_dictionary/" +
                User.getCurrentUserUID() + "/meta_data/" + getDictionaryName() + "/test/correct_words_counter")
                .setValue(correctWordsCounter);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(dictionaryName);
        parcel.writeInt(answerCount);
        parcel.writeInt(inputType);
        parcel.writeInt(timer);
        parcel.writeInt(testingSide);
        parcel.writeInt(dictSize);
        parcel.writeInt(testType);
        parcel.writeInt(questionCounter);
        parcel.writeInt(wordRepetitionCounter);
        parcel.writeInt(correctWordsCounter);
        parcel.writeByte((byte) (allWords == null ? 0 : allWords ? 1 : 2));
        parcel.writeStringList(pickedWordUids);
    }

    public String logConfig(){
       return  "\nTest config: \n" +
               "dictionaryName: " + getDictionaryName() + "\n" +
                "answerCount: " + getAnswerCount() + "\n" +
                "inputType: " + getInputType() + "\n" +
                "timer: " + getTimer() + "\n" +
                "testingSide: " + getTestingSide() + "\n" +
                "dictSize: " + getDictSize() + "\n" +
                "testType: " + getTestType() + "\n" +
                "questionCounter: " + getQuestionCounter() + "\n" +
                "wordRepetitionCounter: " + getWordRepetitionCounter() + "\n" +
                "correctWordsCounter: " + getCorrectWordsCounter() + "\n" +
                "pickedWordUids: " + getPickedWordsSize();
    }
}
