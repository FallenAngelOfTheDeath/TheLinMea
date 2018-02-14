/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 9:01 PM
 */

package com.fallenangel.linmea._modulus.testing;

import com.fallenangel.linmea._modulus.non.data.BaseModel;
import com.fallenangel.linmea._modulus.testing.models.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by NineB on 1/16/2018.
 */

public class Test{
    private String prevWordUid = "";
    private String curWordUid = "";
    private int wordId;
    private List<Word> dictionary = new ArrayList<>();
    private List<Answer> answers = new ArrayList<>();

    private int mistakes = 0;
    private int rightAnswers = 0;

    private List<String> mistakesList = new ArrayList<>();
    private List<String> rightAnswerList = new ArrayList<>();


    private int questionCounter;
    private int stopCounter;


    public Test() {

    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void shuffleAnswers(List<Test.Answer> answers){
        Collections.shuffle(answers);
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public void putAnswer(String uid, String tr, Boolean cor){
        Answer answer = new Answer();
        answer.setAnswer(tr);
        answer.setCorrect(cor);
        answer.setUID(uid);
        this.answers.add(answer);
    }

    public void removeWord(int id) {
        dictionary.remove(id);
    }

    public int getStopCounter() {
        return stopCounter;
    }

    public void setStopCounter(int stopCounter) {
        this.stopCounter = stopCounter;
    }

    public void incrementStopCounter(){
        this.stopCounter++;
    }

    public List<String> getMistakesList() {
        return mistakesList;
    }

    public void setMistakesList(List<String> mistakesList) {
        this.mistakesList = mistakesList;
    }

    public List<String> getRightAnswerList() {
        return rightAnswerList;
    }

    public void setRightAnswerList(List<String> rightAnswerList) {
        this.rightAnswerList = rightAnswerList;
    }

    public void addToRightAnswerList(String uid){
        this.rightAnswerList.add(uid);
    }

    public void addToMistakesList(String uid){
        this.mistakesList.add(uid);
    }

    public int getMistakeCounter(){
        return mistakesList.size();
    }

    public int getRightAnswersCounter(){
        return rightAnswerList.size();
    }

    public String getPrevWordUid() {
        return prevWordUid;
    }

    public void setPrevWordUid(String prevWordUid) {
        this.prevWordUid = prevWordUid;
    }

    public String getCurWordUid() {
        return curWordUid;// = mDictionary.get(wordId).getUid();
    }

    public void updateCurWordUid(){
        this.curWordUid = dictionary.get(wordId).getUid();
    }

//        public void setCurWordUid(String curWordUid) {
//            this.curWordUid = curWordUid;
//        }

    public String getCurWord(int testSide){
        switch (testSide){
            case TestConfig.WORD_TRANSLATION:
                return dictionary.get(getWordId()).getWord();
            case TestConfig.TRANSLATION_WORD:
                return dictionary.get(getWordId()).getTranslation();
        }
        return null;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public void genRandomWordId(int min, int max){
        this.wordId = getRandomId(min, max);
    }

    public List<Word> getDictionary() {
        return dictionary;
    }

    public void setDictionary(List<Word> dictionary) {
        this.dictionary = dictionary;
    }

    public void addWordToDictionary(Word word){
        dictionary.add(word);
    }

    public Boolean dictionaryIsEmpty(){
        return dictionary.isEmpty();
    }

    public void clearDictionary(){
        dictionary.clear();
    }

    private int getRandomId(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public int getDictionarySize(){
        return dictionary.size();
    }

    public Boolean prevEqualsCurWord(){
        return prevWordUid.equals(curWordUid);
    }

    public void setCurToPrev(){
        this.prevWordUid = curWordUid;
    }

    public int getQuestionCounter() {
        return questionCounter;
    }

    public void setQuestionCounter(int questionCounter) {
        this.questionCounter = questionCounter;
    }
    public void incrementQuestionCounter(){
        this.questionCounter++;
    }

    public String getPrevWord(int testSide){
        switch (testSide){
            case TestConfig.WORD_TRANSLATION:
                return dictionary.get(getWordId()).getWord();
            case TestConfig.TRANSLATION_WORD:
                return dictionary.get(getWordId()).getTranslation();
        }
        return null;
    }


    public static class Answer extends BaseModel {
        String answer;
        Boolean correct;

        public Answer() {

        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public Boolean getCorrect() {
            return correct;
        }

        public void setCorrect(Boolean correct) {
            this.correct = correct;
        }
    }
}