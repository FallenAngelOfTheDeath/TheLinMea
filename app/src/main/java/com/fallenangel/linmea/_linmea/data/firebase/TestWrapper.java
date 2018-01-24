package com.fallenangel.linmea._linmea.data.firebase;

import com.fallenangel.linmea._linmea.model.TestConfig;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

/**
 * Created by NineB on 1/8/2018.
 */

public class TestWrapper{

    public TestConfig getTestData(DataSnapshot dataSnapshot){

        Long questionCounter = (Long) dataSnapshot.child("test/question_counter").getValue();
        Long wordRepetitionCounter = (Long) dataSnapshot.child("test/word_repetition_counter").getValue();
        Long answerCount = (Long) dataSnapshot.child("test/answer_count").getValue();
        Long correctWordsCounter = (Long) dataSnapshot.child("/test/correct_words_counter").getValue();
        // Log.i(TAG, "getTestData: " + answerCount);
        Long testType = (Long) dataSnapshot.child("test/test_type").getValue();
        Long inputType = (Long) dataSnapshot.child("test/input_type").getValue();
        Long timer = (Long) dataSnapshot.child("test/timer").getValue();
        Long testingSide = (Long) dataSnapshot.child("test/test_side").getValue();
        List<String> pickedWordUids = (List<String>) dataSnapshot.child("test/picked_word_uids").getValue();
        Long dictSize = (Long) dataSnapshot.child("size").getValue();

        TestConfig testConfig = new TestConfig();
        if (questionCounter != null)
            testConfig.setQuestionCounter(questionCounter != null ? questionCounter.intValue() : null);
        if (wordRepetitionCounter != null)
            testConfig.setWordRepetitionCounter(wordRepetitionCounter != null ? wordRepetitionCounter.intValue() : null);
        if (correctWordsCounter != null)
            testConfig.setCorrectWordsCounter(correctWordsCounter != null ? correctWordsCounter.intValue() : null);
        if (dictSize != null)
            testConfig.setDictSize((dictSize != null ? dictSize.intValue() : null)+1);
        if (testType != null)
            testConfig.setTestType(testType != null ? testType.intValue() : null);
        if (answerCount != null)
            testConfig.setAnswerCount(answerCount != null ? answerCount.intValue() : null);
        if (inputType != null)
            testConfig.setInputType(inputType != null ? inputType.intValue() : null);
        if (timer != null)
            testConfig.setTimer(timer != null ? timer.intValue() : null);
      //  testConfig.updTimer((int) (long) timer);
        if (testingSide != null)
            testConfig.setTestingSide(testingSide != null ? testingSide.intValue() : null);
        if (pickedWordUids != null)
            testConfig.setPickedWordUids(pickedWordUids);
        return testConfig;
    }
}