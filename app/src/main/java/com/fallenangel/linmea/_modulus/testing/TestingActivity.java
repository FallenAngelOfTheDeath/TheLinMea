/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/28/18 7:24 PM
 */

package com.fallenangel.linmea._modulus.testing;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.custom_dictionary.data.FirebaseDictionaryWrapper;
import com.fallenangel.linmea._modulus.custom_dictionary.model.CustomDictionaryModel;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.non.Constant;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.non.view.GridAutofitLayoutManager;
import com.fallenangel.linmea._modulus.testing.models.TestItem;
import com.fallenangel.linmea._modulus.testing.models.Word;
import com.fallenangel.linmea._modulus.testing.models.WordWrapper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.fallenangel.linmea._modulus.auth.User.getCurrentUserUID;
import static com.fallenangel.linmea._modulus.testing.TestConfig.CHOOSING_ANSWER;
import static com.fallenangel.linmea._modulus.testing.TestConfig.CORRECT_WORD_LIM;
import static com.fallenangel.linmea._modulus.testing.TestConfig.MANUAL_INPUT;
import static com.fallenangel.linmea._modulus.testing.TestConfig.QUESTIONS_LIM;
import static com.fallenangel.linmea._modulus.testing.TestConfig.WORD_REP_LIM;

public class TestingActivity extends SuperAppCompatActivity implements View.OnClickListener, OnRecyclerViewClickListener {

    @Inject DatabaseReference mDatabaseReference;


    //Const
    private static final String TAG = "TestingActivity";
    private static final int DEBUG = 1;


    //
    private Test test = new Test();
    private TestConfig testConfig;


    //Variables
    List<CustomDictionaryModel> mOriginalDictionary = new ArrayList<>();
    List<Word> mTestingWords = new ArrayList<>();
    List<TestItem> rvItems = new ArrayList<>();

    private FirebaseDictionaryWrapper mFirebaseDictionaryWrapper = new FirebaseDictionaryWrapper();
    private WordWrapper wordWrapper = new WordWrapper();

    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;

    private RecyclerViewTestingAnswers rvAnswersAdapter;


    //View
    private TextView
            tvWord,
            tvComplete, tvDontKnow,
            tvQCounter, tvMistakesCounter, tvRightAnswerCounter, tvTimer;
    private FrameLayout flInputAnswerContainer;
    private RelativeLayout rlMain;

    //View - manual input
    private ImageView ivConfirmAnswer;
    private EditText edTypeAnswer;

    //View - not manual input
    private RecyclerView rvAnswers;


    private QuestionGenerator questionGenerator = new QuestionGenerator();
    private Subscription subscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
        setContentView(R.layout.activity_testing);
        onLoadConfigAndData();
    }

    private void onLoadConfigAndData() {
        Observable
                .just(getTestConfig())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> loadDictionaries())
                .subscribe();
    }

    //Load data
    private TestConfig getTestConfig() {
        testConfig = (TestConfig) getIntent().getParcelableExtra(
                TestConfig.class.getCanonicalName());
        if (DEBUG == 1)
            Log.i(TAG, "onCreate: " + testConfig.logConfig());
        return testConfig;
    }

    private Observable<CustomDictionaryModel> cresteObservable(){


        String path = "custom_dictionary/" + getCurrentUserUID() + "/dictionaries/" + testConfig.getDictionaryName();
        final PublishSubject<CustomDictionaryModel> subject = PublishSubject.create();
        Query query = mDatabaseReference.child(path);
        int dictSize = getIntent().getIntExtra("DictionarySize", 0);
        final int[] counter = {0};

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                subject.onNext(new FirebaseDictionaryWrapper().getCustomDictionaryWord(dataSnapshot));
                counter[0]++;
                if (counter[0] >= dictSize){
                    query.removeEventListener(this);
                    subject.onCompleted();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return subject;
    }

    private Word coverCustomDictToWord(CustomDictionaryModel item){
        Word word = new Word(item.getUID(), item.getWord(), item.getTranslation().get(0));
        return word;
    }

    private void loadDictionaries() {
        if (!test.dictionaryIsEmpty())
            test.clearDictionary();

        cresteObservable()
                .onBackpressureBuffer()
                .doOnNext(new Action1<CustomDictionaryModel>() {
                    @Override
                    public void call(CustomDictionaryModel item) {
                        mOriginalDictionary.add(item);
                        if (DEBUG == 1) Log.i(TAG, "added item to dict: " + item.getWord());
                        for (String uid : testConfig.getPickedWordUids()) {
                            if (uid.equals(item.getUID())) {
                                mTestingWords.add(coverCustomDictToWord(item));
                                // test.addWordToDictionary(wordWrapper.getWord(dataSnapshot));
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> onCreateTestView())
        .subscribe();
    }

    private void onCreateTestView() {
        implementUINew();
        switch (testConfig.getInputType()) {
            case MANUAL_INPUT:
                implementManualInputUI();
                break;
            case CHOOSING_ANSWER:
                implementChoosingAnswerUI();
                break;
        }
        //buildTestQuestion();
        startTest();
    }

    private void implementManualInputUI() {
        View manualInputLayout =
                getLayoutInflater().inflate(R.layout.manual_input, flInputAnswerContainer, true);

        ivConfirmAnswer = (ImageView) manualInputLayout.findViewById(R.id.confirm_button);
        edTypeAnswer = (EditText) manualInputLayout.findViewById(R.id.type_answer);
        ivConfirmAnswer.setOnClickListener(this);
    }

    private void implementChoosingAnswerUI() {
        View rvLayout = getLayoutInflater().inflate(R.layout.recycler_view, flInputAnswerContainer, true);
        rvAnswers = (RecyclerView) rvLayout.findViewById(R.id.recycler_view_ans);
        rvAnswers.setHasFixedSize(true);
        rvAnswers.setLayoutManager(new GridAutofitLayoutManager(this, 400, true));

        rvAnswersAdapter = new RecyclerViewTestingAnswers(rvItems, this);
        rvAnswersAdapter.clear();
        rvAnswers.setAdapter(rvAnswersAdapter);
    }

    private void implementUINew() {
        flInputAnswerContainer = (FrameLayout) findViewById(R.id.answer_container);
        rlMain = (RelativeLayout) findViewById(R.id.main_layout_testing);

        tvWord = (TextView) findViewById(R.id.testing_word);

        tvComplete = (TextView) findViewById(R.id.close);
        tvDontKnow = (TextView) findViewById(R.id.dont_know);

        tvQCounter = (TextView) findViewById(R.id.stats);
        tvMistakesCounter = (TextView) findViewById(R.id.mistakes);
        tvRightAnswerCounter = (TextView) findViewById(R.id.right_answers);
        tvTimer = (TextView) findViewById(R.id.timer);

        tvComplete.setOnClickListener(this);
        tvDontKnow.setOnClickListener(this);
    }


    private List<TestItem> buildByInputType(int i) {
        List<TestItem> list = new ArrayList<>();
        switch (testConfig.getInputType()) {
            case MANUAL_INPUT:
                list.add(getTrueAnswer(i));
                return list;
            case CHOOSING_ANSWER:
                list.addAll(getAnswers(i));
                return list;
        }
        return list;
    }

    private String prevWord = "";
    private int randomId = -1;

    private void buildTestQuestion() {
        TestItem q = null;
        switch (testConfig.getTestType()) {
            case QUESTIONS_LIM:
                randomId = getRandomNum(0, mTestingWords.size() - 1);
                if (!questionGenerator.answersIsEmpty()) questionGenerator.clearAnswers();
                q = genQuestionFromDict(randomId);
                if (prevWord.equals(q.getUid())) {
                    buildTestQuestion();
                } else {
                    prevWord = q.getUid();
                    questionGenerator.buildTest(q, buildByInputType(randomId));
                }
                break;
            case WORD_REP_LIM:
                randomId = getRandomNum(0, mTestingWords.size() - 1);
                if (!questionGenerator.answersIsEmpty()) questionGenerator.clearAnswers();
                q = genQuestionFromDict(randomId);
                if (prevWord.equals(q.getUid())) {
                    buildTestQuestion();
                } else {
                    if (mTestingWords.get(randomId).getWordRepetitionCounter() <= testConfig.getWordRepetitionCounter()) {
                        prevWord = q.getUid();
                        questionGenerator.buildTest(q, buildByInputType(randomId));
                    } else {
                        buildTestQuestion();
                    }
                }
                break;
            case CORRECT_WORD_LIM:
                randomId = getRandomNum(0, mTestingWords.size() - 1);
                if (!questionGenerator.answersIsEmpty()) questionGenerator.clearAnswers();
                q = genQuestionFromDict(randomId);
                if (prevWord.equals(q.getUid())) {
                    buildTestQuestion();
                } else {
                    if (mTestingWords.get(randomId).getCurreectCounter() <= testConfig.getCorrectWordsCounter()) {
                        prevWord = q.getUid();
                        questionGenerator.buildTest(q, buildByInputType(randomId));
                    } else {
                        buildTestQuestion();
                    }
                }
                break;
        }
    }

    private TestItem genQuestionFromDict(int i) {
        TestItem question = new TestItem();
        switch (testConfig.getTestingSide()) {
            case TestConfig.WORD_TRANSLATION:
                question.setAnswer(mTestingWords.get(i).getWord());
                question.setUid(mTestingWords.get(i).getUid());
                question.setcA(mTestingWords.get(i).getTranslation());
                break;
            case TestConfig.TRANSLATION_WORD:
                question.setAnswer(mTestingWords.get(i).getTranslation());
                question.setUid(mTestingWords.get(i).getUid());
                question.setcA(mTestingWords.get(i).getWord());
                break;
        }
        return question;
    }


    private void startTest() {
        buildTestQuestion();
        rxTimer();

        switch (testConfig.getInputType()) {
            case CHOOSING_ANSWER:
                fillRecyclerView();
                break;
            case MANUAL_INPUT:
                break;
        }
        updateUI();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dont_know:
                checkFinishTest(view, -1);
                break;
            case R.id.close:
                finishTestAlert();
                break;
            case R.id.confirm_button:
                checkFinishTest(view, -1);
                break;
        }
        updateUI();
    }


    private void rxTimer() {
        if (subscription != null)
            subscription.unsubscribe();
        if(testConfig.getTimer() != 0) {
            subscription = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .takeUntil(new Func1<Long, Boolean>() {
                        @Override
                        public Boolean call(Long aLong) {
                            return aLong == testConfig.getTimer();
                        }
                    })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(aLong -> tvTimer.setText("Left " + (testConfig.getTimer() - aLong) + " seconds")).doOnCompleted(() -> {
                        Snackbar.make(rlMain, "Time to answer is over", Snackbar.LENGTH_LONG).show();
                        questionGenerator.skipQuestionWithoutAnswer();
                        mTestingWords.get(randomId).incrementMistakCounter();
                        mTestingWords.get(randomId).incrementRepetitionCounter();
                        switch (testConfig.getTestType()) {
                            case QUESTIONS_LIM:
                                if (questionGenerator.getTestStats().getQCounter() >= testConfig.getQuestionCounter()) {
                                    finishTest();
                                } else {
                                    startTest();
                                }
                                break;
                            case WORD_REP_LIM:
                                if (questionGenerator.getTestStats().getQCounter() >= (mTestingWords.size() * testConfig.getWordRepetitionCounter())) {
                                    finishTest();
                                }else {
                                    startTest();
                                }
                                break;
                            case CORRECT_WORD_LIM:
                                if (stopCounter >= (mTestingWords.size())) {
                                    finishTest();
                                } else {
                                    if(mTestingWords.get(randomId).getCurreectCounter()==testConfig.getCorrectWordsCounter()){
                                        stopCounter++;
                                        if(Constant.DEBUG == 1) Log.d(TAG, "increment stop counter: " + stopCounter);
                                    }
                                    startTest();
                                }
                                break;
                        }
                        if(testConfig.getInputType() == MANUAL_INPUT) edTypeAnswer.setText("");
                    })
                .subscribe();

        } else {
            tvTimer.setText("No timer");
        }
    }

    @Override
    public void onItemClicked(View view, int position) {
        checkFinishTest(view, position);
    }
    private int stopCounter;
    private void checkFinishTest(View v, int position){
        switch (testConfig.getTestType()) {
            case QUESTIONS_LIM:
                    if (v.getId() != R.id.dont_know){
                        if (getAnswerFromUi(position).toLowerCase().trim().equals(questionGenerator.getCurrectAnswer().toLowerCase().trim())){
                            questionGenerator.trueAnswer();
                            mTestingWords.get(randomId).incrementCurrectCouinter();
                            mTestingWords.get(randomId).incrementRepetitionCounter();
                        } else {
                            questionGenerator.falseAnswer(getAnswerFromUi(position));
                            mTestingWords.get(randomId).incrementMistakCounter();
                            mTestingWords.get(randomId).incrementRepetitionCounter();
                        }
                        //if (Constant.DEBUG == 1) Log.d(TAG, "checkFinishTest: " + getAnswerFromUi(position).toLowerCase().trim());
                       // if (Constant.DEBUG == 1) Log.d(TAG, "checkFinishTest: " + questionGenerator.getCurrectAnswer().toLowerCase().trim());
                    } else {
                        questionGenerator.skipQuestionWithoutAnswer();
                        mTestingWords.get(randomId).incrementMistakCounter();
                        mTestingWords.get(randomId).incrementRepetitionCounter();
                    }
                    if (questionGenerator.getTestStats().getQCounter() >= testConfig.getQuestionCounter()) {
                        finishTest();
                    } else {
                    startTest();
                    }
                break;
            case WORD_REP_LIM:
                if (v.getId() != R.id.dont_know){
                    if (getAnswerFromUi(position).toLowerCase().trim().equals(questionGenerator.getCurrectAnswer().toLowerCase().trim())){
                        questionGenerator.trueAnswer();
                        mTestingWords.get(randomId).incrementCurrectCouinter();
                        mTestingWords.get(randomId).incrementRepetitionCounter();
                    } else {
                        questionGenerator.falseAnswer(getAnswerFromUi(position));
                        mTestingWords.get(randomId).incrementMistakCounter();
                        mTestingWords.get(randomId).incrementRepetitionCounter();
                    }
                    //if (Constant.DEBUG == 1) Log.d(TAG, "checkFinishTest: " + getAnswerFromUi(position).toLowerCase().trim());
                   // if (Constant.DEBUG == 1) Log.d(TAG, "checkFinishTest: " + questionGenerator.getCurrectAnswer().toLowerCase().trim());
                } else {
                    questionGenerator.skipQuestionWithoutAnswer();
                    mTestingWords.get(randomId).incrementMistakCounter();
                    mTestingWords.get(randomId).incrementRepetitionCounter();
                }
                if (questionGenerator.getTestStats().getQCounter() >= (mTestingWords.size() * testConfig.getWordRepetitionCounter())) {
                    finishTest();
                }else {
                    startTest();
                }
                break;
            case CORRECT_WORD_LIM:
                if (v.getId() != R.id.dont_know){
                    if (getAnswerFromUi(position).toLowerCase().trim().equals(questionGenerator.getCurrectAnswer().toLowerCase().trim())){
                        questionGenerator.trueAnswer();
                        mTestingWords.get(randomId).incrementCurrectCouinter();
                        mTestingWords.get(randomId).incrementRepetitionCounter();
                    } else {
                        questionGenerator.falseAnswer(getAnswerFromUi(position));
                        mTestingWords.get(randomId).incrementMistakCounter();
                        mTestingWords.get(randomId).incrementRepetitionCounter();
                    }
                   // if (Constant.DEBUG == 1) Log.d(TAG, "checkFinishTest: " + getAnswerFromUi(position).toLowerCase().trim());
                   // if (Constant.DEBUG == 1) Log.d(TAG, "checkFinishTest: " + questionGenerator.getCurrectAnswer().toLowerCase().trim());
                } else {
                    questionGenerator.skipQuestionWithoutAnswer();
                    mTestingWords.get(randomId).incrementMistakCounter();
                    mTestingWords.get(randomId).incrementRepetitionCounter();
                }
                if (stopCounter >= (mTestingWords.size())) {
                    finishTest();
                } else {
                    if(mTestingWords.get(randomId).getCurreectCounter()==testConfig.getCorrectWordsCounter()){
                        stopCounter++;
                            if(Constant.DEBUG == 1) Log.d(TAG, "increment stop counter: " + stopCounter);
                    }
                    startTest();
                }
                break;

        }
        if(testConfig.getInputType() == MANUAL_INPUT) edTypeAnswer.setText("");
    }

    private void updateUI(){
        tvWord.setText(questionGenerator.getQuestion());
        switch (testConfig.getTestType()) {
            case QUESTIONS_LIM:
                tvQCounter.setText("Question: " + (questionGenerator.getTestStats().getQCounter() + 1) + " of " + testConfig.getQuestionCounter());
                break;
            case WORD_REP_LIM:
                tvQCounter.setText("Question: " + (questionGenerator.getTestStats().getQCounter() + 1) + " of " + (mTestingWords.size() * testConfig.getWordRepetitionCounter()));
                break;
            case CORRECT_WORD_LIM:
                tvQCounter.setText("Question: " + (questionGenerator.getTestStats().getQCounter() + 1));
                break;
        }
        tvRightAnswerCounter.setText("Correct answers: " + questionGenerator.getTestStats().getTrueCounter());
        tvMistakesCounter.setText("Mistakes: " + questionGenerator.getTestStats().getFalseCounter());
    }

    private List<TestItem> getAnswers(int i){
        List<TestItem > answers = new ArrayList<>();
        answers.add(getTrueAnswer(i));
        answers.addAll(getFalseAnswers());
        return answers;
    }

    private TestItem getTrueAnswer(int i){
        TestItem trueAnswer = new TestItem();
        switch (testConfig.getTestingSide()){
            case TestConfig.WORD_TRANSLATION:
                trueAnswer.setAnswer(mTestingWords.get(i).getTranslation());
                trueAnswer.setUid(mTestingWords.get(i).getUid());
                break;
            case TestConfig.TRANSLATION_WORD:
                trueAnswer.setAnswer(mTestingWords.get(i).getWord());
                trueAnswer.setUid(mTestingWords.get(i).getUid());
                break;
        }
        return trueAnswer;
    }

    private List<TestItem> getFalseAnswers(){
        List<TestItem> list = new ArrayList<>();
        for (int i = 0; i < testConfig.getAnswerCount()-1; i++) {
            int num = getRandomNum(0, mOriginalDictionary.size()-1);
            TestItem item = null;
            switch (testConfig.getTestingSide()){
                case TestConfig.WORD_TRANSLATION:
                    item = new TestItem(mOriginalDictionary.get(num).getTranslation().get(0), mOriginalDictionary.get(num).getUID());
                    break;
                case TestConfig.TRANSLATION_WORD:
                    item = new TestItem(mOriginalDictionary.get(num).getWord(), mOriginalDictionary.get(num).getUID());
                    break;
            }
            list.add(item);
        }
        return list;
    }


    //    private TestItem genOneFalseAnswer() {
//        int num = getRandomNum(0, mOriginalDictionary.size() - 1);
//        TestItem testItem = new TestItem();
//        if (!mOriginalDictionary.get(num).getUID().equals(questionGenerator.getTrueAnswerUid())) {
//            switch (testConfig.getTestingSide()) {
//                case TestConfig.WORD_TRANSLATION:
//                    testItem.setAnswer(mOriginalDictionary.get(num).getWord());
//                    testItem.setUid(mOriginalDictionary.get(num).getUID());
//                    break;
//                case TestConfig.TRANSLATION_WORD:
//                    testItem.setAnswer(mOriginalDictionary.get(num).getTranslation().get(0));
//                    testItem.setUid(mOriginalDictionary.get(num).getUID());
//                    break;
//            }
//        } else {
//        genOneFalseAnswer();
//        }
//    return testItem;
//    }
    private void fillRecyclerView(){
        rvAnswersAdapter.clear();
        rvItems.addAll(questionGenerator.getAllAnswers());
        rvAnswersAdapter.notifyDataSetChanged();

    }

    private int getRandomNum(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }


    private String getAnswerFromUi(int position){
        switch (testConfig.getInputType()){
            case CHOOSING_ANSWER:
                fillRecyclerView();
                return questionGenerator.getAllAnswers().get(position).getAnswer();
            case MANUAL_INPUT:
                return edTypeAnswer.getText().toString().toLowerCase().trim();
        }
        return null;
    }















    @Override
    public void onOptionsClicked(View view, int position) {

    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        return false;
    }

    private void finishTestAlert(){
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle(R.string.complete);
        mAlertDialogBuilder.setMessage(R.string.finish_test);

        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishTest();
            }
        });
        mAlertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    @Override
    public void onBackPressed() {
        finishTestAlert();
    }

    private void finishTest(){
        if (subscription != null)
            subscription.unsubscribe();

        Bundle extra = new Bundle();
        extra.putSerializable("TESTING_WORDS", (Serializable) mTestingWords);

        Intent intent = new Intent(this, TestResultActivity.class);
        intent.putExtra(QuestionGenerator.class.getCanonicalName(), questionGenerator);
        intent.putExtra("TESTING_WORDS_EXTRA", extra);
        startActivity(intent);
        finish();
    }

}
