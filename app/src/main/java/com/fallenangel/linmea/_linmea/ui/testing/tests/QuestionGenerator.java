package com.fallenangel.linmea._linmea.ui.testing.tests;

import android.os.Parcelable;
import android.util.Log;

import com.fallenangel.linmea._linmea.ui.testing.models.TestItem;
import com.fallenangel.linmea._modulus.non.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by NineB on 1/21/2018.
 */

public class QuestionGenerator extends Testing implements Parcelable {

    private List<TestItem> answers = new ArrayList<>();
    private TestItem question = new TestItem();



   // private List<TestQuestion> test = new ArrayList<>();
    private String preQues;

    private int id;

    public QuestionGenerator() {
    }

    public List<TestItem> getAllAnswers(){
        return answers;
    }

    public String getCurrectAnswer(){
        String str = "";
        for (TestItem item:answers) {
            if (Constant.DEBUG == 1) Log.d("QG", "getCurrectAnswer: " + question.getUid() + " : " + item.getUid());
            if (question.getUid()
                    .equals(item.getUid()))
                str = item.getAnswer();
        }
        return str;
    }

    public Boolean answersIsEmpty(){
        return answers.isEmpty();
    }



    public void buildTest(TestItem question, List<TestItem> answers){
        setQuestion(question);
        setAnswers(answers);
        getStats().getQCounter();
    }



    public void clearAnswers(){
        answers.clear();
    }


//    public TestQuestion getRandomQuestion(){
//        genQId();
//        getStats().incrementAndGetQCounter();
//        return test.get(id);
//    }


//    private void genQId(){
//        id = getRandomNum(0, test.size()-1);
//    }


    public int getCorrectQuestion(){
        return id;
    }


    public void skipQuestionWithoutAnswer(){
        skipQuestionWithoutAnswer(question);
    }

    public void trueAnswer(){
        getStats().incrementAndGetQCounter();
        getStats().incrementAndGetTrueCounter();
        getStats().setWordStat(question, getCurrectAnswer());
    }

    public void falseAnswer(String answ){
        getStats().incrementAndGetQCounter();
        getStats().incrementAndGetFalseCounter();
        getStats().setWordStat(question, answ);
    }

//    private int getRandomNum(int min, int max) {
//        Random random = new Random();
//        return random.nextInt((max - min) + 1) + min;
//    }




//    private int getSize(){
//        return test.size();
//    }
//
//    public TestQuestion getTestingQuestion(){
//        int num = getRandomNum(0, getSize()-1);
//        if (!preQues.equals(test.get(num).getQuestionTrueUid()))
//            getTestingQuestion();
//        else
//            return test.get(num);
//        return null;
//    }


//    public String getTrueAnswerUidById(int i){
//        return test.get(i).getQuestionTrueUid();
//    }






    public String getQuestion(){
        return question.getAnswer();
    }





    ///--------
    public void setAnswers(List<TestItem> answers) {
        Collections.shuffle(answers);
        this.answers = answers;
    }

    public void setQuestion(TestItem question) {
        this.question = question;
    }
}
