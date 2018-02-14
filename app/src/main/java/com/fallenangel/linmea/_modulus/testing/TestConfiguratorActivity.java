/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/28/18 7:28 PM
 */

package com.fallenangel.linmea._modulus.testing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.custom_dictionary.data.FirebaseDictionaryWrapper;
import com.fallenangel.linmea._modulus.custom_dictionary.model.CustomDictionaryModel;
import com.fallenangel.linmea._modulus.custom_dictionary.ui.AddCustomDictionaryActivity;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.prferences.DictionaryCustomizer;
import com.fallenangel.linmea._modulus.testing.models.SimpleDictionary;
import com.fallenangel.linmea._modulus.testing.models.Word;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.fallenangel.linmea._modulus.auth.User.getCurrentUserUID;



public class TestConfiguratorActivity extends SuperAppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    //Constant
    private static final String TAG = "TestConfigurator";

    private static final int CHOOSING_ANSWER = 0;
    private static final int MANUAL_INPT = 1;

    //View
    private Toolbar mToolbar;

    private LinearLayout lvDictionary, lvTestingWords, lvInputType, lvTimeToAnswer, lvTestSide, lvTestType;
    private TextView tvDictionaryDescription, tvTestingWordsDescription, tvInputTypeDescription, tvAnswerCountCounter, tvTimeToAnswerCounter, tvStart, tvQuestionCounter, tvWordRepetitionCounter,
                        tvTestSideDescription, tvTestTypeDescription, tvTestTypeCounter, tvTestTypeSbTitle;
    private SeekBar sbNumbersOfAnswer, sbTimeToAnswer, sbQuestionCounter, sbWordRepetitionCounter, sbTestingType;




    @Inject
    Context mContext;
    @Inject DatabaseReference mDatabaseReference;
    private FirebaseDictionaryWrapper mFirebaseDictionaryWrapper = new FirebaseDictionaryWrapper();

    private TestConfig testingConfigurator = new TestConfig();
    private SimpleDictionary simpleDictionary = new SimpleDictionary();

    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;

    private int mCheckedItemOfAlertDialog;
    private int mPickedValue;
    private boolean[] checkedItems;
    private SparseBooleanArray mPickedWordsBooleanArray = new SparseBooleanArray();
    private List<String> mDictionaryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_configurator);
        getAppComponent().inject(this);
        implementIntentData();
        implementUI();
        loadSavedTestData();
        loadDictionaryWords()
                .onBackpressureBuffer()
                .doOnNext(new Action1<DataSnapshot>() {
                    @Override
                    public void call(DataSnapshot dataSnapshot) {
                        CustomDictionaryModel item = mFirebaseDictionaryWrapper.getCustomDictionaryWord(dataSnapshot);
                        simpleDictionary.addWord(item.getUID(), item.getWord(), item.getTranslation().get(0));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        upUI();
                    }
                }).subscribe();
    }

    private void implementIntentData() {
        testingConfigurator.setDictionaryName(getDictionary());
    }


    private void implementUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setTitle(testingConfigurator.getDictionaryName());



        lvDictionary = (LinearLayout) findViewById(R.id.dictionary_container);
        lvTestingWords = (LinearLayout) findViewById(R.id.words_container);
        lvInputType = (LinearLayout) findViewById(R.id.input_type_container);
        lvTestSide = (LinearLayout) findViewById(R.id.test_side_container);
        lvTestType = (LinearLayout) findViewById(R.id.test_type_container);

        tvDictionaryDescription = (TextView) findViewById(R.id.dictionary_description);
        tvTestingWordsDescription = (TextView) findViewById(R.id.words_description);
        tvInputTypeDescription = (TextView) findViewById(R.id.input_type_description);
        tvAnswerCountCounter = (TextView) findViewById(R.id.answer_count_counter);
        tvTimeToAnswerCounter = (TextView) findViewById(R.id.timer_count_counter);
        tvStart = (TextView) findViewById(R.id.start_test);
//        tvQuestionCounter = (TextView) findViewById(R.id.question_counter);
//        tvWordRepetitionCounter = (TextView) findViewById(R.id.word_repetition_counter);
        tvTestSideDescription = (TextView) findViewById(R.id.test_side_description);
        tvTestTypeDescription = (TextView) findViewById(R.id.test_type_description);
        tvTestTypeCounter = (TextView) findViewById(R.id.testing_type_counter);
        tvTestTypeSbTitle = (TextView) findViewById(R.id.test_type_sb_title);

        sbNumbersOfAnswer = (SeekBar) findViewById(R.id.answer_count_seek_bar);
        sbTimeToAnswer = (SeekBar) findViewById(R.id.timer_count_seek_bar);
//        sbQuestionCounter = (SeekBar) findViewById(R.id.question_counter_seek_bar);
//        sbWordRepetitionCounter = (SeekBar) findViewById(R.id.word_repetition_counter_seek_bar);
        sbTestingType = (SeekBar) findViewById(R.id.testing_type_seek_bar);

        tvStart.setOnClickListener(this);

        //lvDictionary.setOnClickListener(this);
        lvDictionary.setVisibility(View.GONE);
        lvTestingWords.setOnClickListener(this);
        lvInputType.setOnClickListener(this);
        lvTestSide.setOnClickListener(this);
        lvTestType.setOnClickListener(this);

        sbNumbersOfAnswer.setOnSeekBarChangeListener(this);
        sbTimeToAnswer.setOnSeekBarChangeListener(this);
//        sbQuestionCounter.setOnSeekBarChangeListener(this);
//        sbWordRepetitionCounter.setOnSeekBarChangeListener(this);
        sbTestingType.setOnSeekBarChangeListener(this);

        upUI();
    }

    private String setTestingWordsDescription() {
        return "picked " + testingConfigurator.getPickedWordsSize() + " words of " + (testingConfigurator.getDictSize()-1);
    }


    private String getDictionary() {
        return getIntent().getStringExtra("DictionaryName");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case -1:
                finish();
                break;
            case R.id.dictionary_container:
                pickDictionary();
                break;
            case R.id.words_container:
                rx.Observable.just(chooseTestingWords())
                        .observeOn(Schedulers.computation())
                        .subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                break;
            case  R.id.input_type_container:
                chooseInputType();
                break;
            case R.id.test_type_container:
                chooseTestType();
                break;
            case R.id.test_side_container:
                chooseTestSide();
                break;
            case R.id.start_test:
                startTest();
                break;
        }
    }

    private void pickDictionary(){
        mAlertDialogBuilder = new AlertDialog.Builder(this);

        for (int i = 0; i < mDictionaryList.size(); i++) {
            if (mDictionaryList.get(i).equals(testingConfigurator.getDictionaryName()))
                mCheckedItemOfAlertDialog = i;
        }

        String[] arrayOfDictionaries = mDictionaryList.toArray(new String[0]);

        if (mDictionaryList.isEmpty()){
            LayoutInflater inflater = LayoutInflater.from(this);
            final View text_view_layout = inflater.inflate(R.layout.text_view_alert_dialog_dictionary, null);
            mAlertDialogBuilder.setView(text_view_layout);
        }else {
            mAlertDialogBuilder.setTitle(R.string.dict_customizer_dict_description);
            mAlertDialogBuilder.setSingleChoiceItems(arrayOfDictionaries, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPickedValue = which;
                }
            });
        }
        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                testingConfigurator.setDictionaryName(mDictionaryList.get(mPickedValue));
                updateUI();
            }
        });
        mAlertDialogBuilder.setNeutralButton(R.string.add_new_dictionary, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent addDictionary = new Intent(getApplicationContext(), AddCustomDictionaryActivity.class);
                startActivity(addDictionary);
            }
        });
        mAlertDialogBuilder.setNegativeButton(this.getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    private List<String> loadDictionaryList(DatabaseReference mDatabaseReference, final List<String> mDictionaryList){

        final PublishSubject<String> subject = PublishSubject.create();
        String path = "custom_dictionary/" + getCurrentUserUID() + "/meta_data/";
        if (!mDictionaryList.isEmpty())
            mDictionaryList.clear();
        mDatabaseReference.child(path).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                mDictionaryList.add(dataSnapshot.getKey());
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
        return mDictionaryList;
    }

    private Boolean chooseTestingWords(){
        checkedItems = new boolean[simpleDictionary.getDictionarySize()];
        mAlertDialogBuilder = new AlertDialog.Builder(this);

        List<String> itemDictList = new ArrayList<>();

        for (int i = 0; i < simpleDictionary.getDictionarySize(); i++) {
            itemDictList.add(simpleDictionary.getDictionaryWord(i).getWord() + " - " +simpleDictionary.getDictionaryWord(i).getTranslation());
        }

        if (!testingConfigurator.pickedWordUidsIsEmpty())
        for (String uid: testingConfigurator.getPickedWordUids()) {
            for (int i = 0; i < simpleDictionary.getDictionarySize(); i++) {
                if (uid.equals(simpleDictionary.getDictionary().get(i).getUid())){
                    mPickedWordsBooleanArray.put(i, true);
                }
                if (mPickedWordsBooleanArray.get(i) == true)
                    checkedItems[i] = true;
                else
                    checkedItems[i] = false;
            }
        }

        String[] arrayOfDictionaryItems = itemDictList.toArray(new String[0]);

        if (simpleDictionary.dictionaryIsEmpty()){
            Log.i(TAG, "text_view_layout: ");
            LayoutInflater inflater = LayoutInflater.from(this);
            final View text_view_layout = inflater.inflate(R.layout.text_view_alert_dialog_dictionary, null);
            mAlertDialogBuilder.setView(text_view_layout);
        }else {
            Log.i(TAG, "chooseTestingWords: ");
            mAlertDialogBuilder.setTitle(R.string.dictionary_is_empty_description);
            mAlertDialogBuilder.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_multiple_choice, arrayOfDictionaryItems), null);
            mAlertDialogBuilder.setMultiChoiceItems(arrayOfDictionaryItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    mPickedWordsBooleanArray.put(which, isChecked);
                }
            });
            mAlertDialogBuilder.setPositiveButton(this.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    List<String> pickedWordUidList = new ArrayList<String>();
                    for (int j = 0; j < simpleDictionary.getDictionarySize(); j++) {
                        if (mPickedWordsBooleanArray.get(j) == true)
                            pickedWordUidList.add(simpleDictionary.getDictionary().get(j).getUid());
                    }
                    testingConfigurator.updPickedWordUids(pickedWordUidList);
                    upUI();
                }
            });
            if (testingConfigurator.getPickedWordsSize() == simpleDictionary.getDictionarySize()) {
                mAlertDialogBuilder.setNeutralButton(this.getString(R.string.reamove_all), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        testingConfigurator.clearPickedWordUids();
                        testingConfigurator.deletPickedWordUids();
                        upUI();
                    }
                });
            } else {
                mAlertDialogBuilder.setNeutralButton(this.getString(R.string.select_all), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<String> pickedWordUidList = new ArrayList<String>();
                        testingConfigurator.clearPickedWordUids();
                        for (Word word : simpleDictionary.getDictionary()) {
                            pickedWordUidList.add(word.getUid());
                        }
                        testingConfigurator.deletPickedWordUids();
                        testingConfigurator.updPickedWordUids(pickedWordUidList);
                        upUI();
//                    for (int j = 0; j < testingConfigurator.getDictionarySize(); j++) {
//                        testingConfigurator.
//                        mPickedWordsBooleanArray.put(j, true);
//                    }
                    }
                });
            }
        }
            mAlertDialogBuilder.setNegativeButton(this.getString(R.string.cancel), null);
            mAlertDialog = mAlertDialogBuilder.create();
            mAlertDialog.show();

        return false;
    }

    private Observable<DataSnapshot> loadDictionaryWords(){
        final PublishSubject<DataSnapshot> subject = PublishSubject.create();

        String path = "custom_dictionary/" + getCurrentUserUID() + "/dictionaries/" + testingConfigurator.getDictionaryName();
        if (!mDictionaryList.isEmpty())
            mDictionaryList.clear();
        final int[] counter = {0};

        Query query = mDatabaseReference.child(path).orderByChild("word");

        final int[] size = {-1};
        mDatabaseReference.child("/custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/" + getDictionary() + "/size/")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long theLong =  (Long) dataSnapshot.getValue();
                        size[0] = theLong != null ? theLong.intValue() : -1;
                        query.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                counter[0]++;
                                //CustomDictionaryModel item = mFirebaseDictionaryWrapper.getCustomDictionaryWord(dataSnapshot);
                                //simpleDictionary.addWord(item.getUID(), item.getWord(), item.getTranslation().get(0));
                                subject.onNext(dataSnapshot);
                                //Log.i(TAG, "onChildAdded: " + item.getUID() + " : " + item.getWord() + " : " + item.getTranslation().get(0));
                                if (counter[0] >= size[0]){
                                    subject.onCompleted();
                                    query.removeEventListener(this);
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


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        return subject;
    }



    private TestConfig loadSavedTestData(){
        String path = "custom_dictionary/" + getCurrentUserUID() + "/meta_data/" + getDictionary();
        mDatabaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                testingConfigurator = new TestWrapper().getTestData(dataSnapshot);
                testingConfigurator.setDictionaryName(getDictionary());
                upUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return testingConfigurator;
    }

    private void chooseInputType(){
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle(R.string.input_type_description);
        mCheckedItemOfAlertDialog = testingConfigurator.getInputType();
        Log.i(TAG, "chooseInputType: " + testingConfigurator.getInputType());
        mAlertDialogBuilder.setSingleChoiceItems(R.array.input_type, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPickedValue = which;
            }
        });
        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                testingConfigurator.updInputType(mPickedValue);
                upUI();
            }
        });
        mAlertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    private void chooseTestType(){
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle(R.string.test_type);
        mCheckedItemOfAlertDialog = testingConfigurator.getTestType();
        mAlertDialogBuilder.setSingleChoiceItems(R.array.test_type, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPickedValue = which;
            }
        });
        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                testingConfigurator.updTestType(mPickedValue);
                upUI();
            }
        });
        mAlertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    private void chooseTestSide(){
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle(R.string.test_side);
        mCheckedItemOfAlertDialog = testingConfigurator.getTestingSide();
        mAlertDialogBuilder.setSingleChoiceItems(R.array.test_side, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPickedValue = which;
            }
        });
        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                testingConfigurator.updTestingSide(mPickedValue);
                upUI();
            }
        });
        mAlertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    public void updateUI(){
        Observable
                .just(loadDictionaryWords())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> upUI())
                .subscribe();
    }

    private void upUI(){
        if (testingConfigurator.getInputType() == CHOOSING_ANSWER & testingConfigurator.getAnswerCount() == 0){
            testingConfigurator.updAnswerCount(2);
        }

        tvDictionaryDescription.setText(testingConfigurator.getDictionaryName());
        tvTestingWordsDescription.setText(setTestingWordsDescription());
        tvInputTypeDescription.setText(DictionaryCustomizer.switchHelper(getApplicationContext(), testingConfigurator.getInputType(), R.array.input_type));
        tvAnswerCountCounter.setText(String.valueOf(testingConfigurator.getAnswerCount()));
        tvTimeToAnswerCounter.setText(String.valueOf(testingConfigurator.getTimer()));
        //tvWordRepetitionCounter.setText(String.valueOf(testingConfigurator.getWordRepetitionCounter()));
        //tvQuestionCounter.setText(String.valueOf(testingConfigurator.getQuestionCounter()));
        tvTestSideDescription.setText(DictionaryCustomizer.switchHelper(getApplicationContext(), testingConfigurator.getTestingSide(), R.array.test_side));

        sbNumbersOfAnswer.setProgress(testingConfigurator.getAnswerCount());
        if (testingConfigurator.getInputType() == CHOOSING_ANSWER) {
            sbNumbersOfAnswer.setEnabled(true);
            //mDatabaseReference.child("custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/" + getDictionary() + "/test/input_type").setValue(CHOOSING_ANSWER);
        } else {
            sbNumbersOfAnswer.setEnabled(false);
            //mDatabaseReference.child("custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/" + getDictionary() + "/test/input_type").setValue(MANUAL_INPT);
        }
        sbTimeToAnswer.setProgress(testingConfigurator.getTimer());
//        sbQuestionCounter.setProgress(testingConfigurator.getQuestionCounter());
        //   sbWordRepetitionCounter.setProgress(testingConfigurator.getWordRepetitionCounter());


        //            <item name="0">Limit by questions</item>
//        <item name="1">Restriction on the number of repetitions of words</item>
//        <item name="2">The limit on the number of correct words</item>

        tvTestTypeDescription.setText(DictionaryCustomizer.switchHelper(getApplicationContext(), testingConfigurator.getTestType(), R.array.test_type));

        //  tvTestTypeDescription.setText(DictionaryCustomizer.switchHelper(getApplicationContext(), testingConfigurator.getTestType(), R.array.input_type));
        switch (testingConfigurator.getTestType()){
            case 0:
                tvTestTypeSbTitle.setText(getResources().getString(R.string.question_counter));
                tvTestTypeCounter.setText(String.valueOf(testingConfigurator.getQuestionCounter()));
                sbTestingType.setProgress(testingConfigurator.getQuestionCounter());
                sbTestingType.setMax(128);
                break;
            case 1:
                tvTestTypeSbTitle.setText(getResources().getString(R.string.word_repetition_counter));
                tvTestTypeCounter.setText(String.valueOf(testingConfigurator.getWordRepetitionCounter()));
                sbTestingType.setProgress(testingConfigurator.getWordRepetitionCounter());
                sbTestingType.setMax(10);
                break;
            case 2:
                tvTestTypeSbTitle.setText(getResources().getString(R.string.correct_words_counter));
                tvTestTypeCounter.setText(String.valueOf(testingConfigurator.getCorrectWordsCounter()));
                sbTestingType.setProgress(testingConfigurator.getCorrectWordsCounter());
                sbTestingType.setMax(10);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        switch (seekBar.getId()){
//            case R.id.question_counter_seek_bar:
//                tvQuestionCounter.setText(String.valueOf(progress));
//                break;
//            case R.id.word_repetition_counter_seek_bar:
//                tvWordRepetitionCounter.setText(String.valueOf(progress));
//                break;
            case R.id.answer_count_seek_bar:
                tvAnswerCountCounter.setText(String.valueOf(progress));
                break;
            case R.id.timer_count_seek_bar:
                tvTimeToAnswerCounter.setText(String.valueOf(progress));
                break;
            case R.id.testing_type_seek_bar:
                tvTestTypeCounter.setText(String.valueOf(progress));
                break;

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()){
//            case R.id.question_counter_seek_bar:
//                testingConfigurator.updQuestionCounter(seekBar.getProgress());
//                mDatabaseReference.child("custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/" + getDictionary() + "/test/question_counter").setValue(seekBar.getProgress());
//                break;
//            case R.id.word_repetition_counter_seek_bar:
//                testingConfigurator.updWordRepetitionCounter(seekBar.getProgress());
//                mDatabaseReference.child("custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/" + getDictionary() + "/test/word_repetition_counter").setValue(seekBar.getProgress());
//                break;
            case R.id.answer_count_seek_bar:
                testingConfigurator.updAnswerCount(seekBar.getProgress());
                if (seekBar.getProgress() == 0)
                    testingConfigurator.updInputType(MANUAL_INPT);
                upUI();
                break;
            case R.id.timer_count_seek_bar:
                testingConfigurator.updTimer(seekBar.getProgress());
                break;

            case R.id.testing_type_seek_bar:
                switch (testingConfigurator.getTestType()){
                    case 0:
                        if (seekBar.getProgress() < 2){
                            testingConfigurator.updQuestionCounter(2);
                            upUI();
                        } else {
                            testingConfigurator.updQuestionCounter(seekBar.getProgress());
                        }
                        break;
                    case 1:
                        if (seekBar.getProgress() == 0){
                            Toast.makeText(getApplicationContext(), "Nope", Toast.LENGTH_SHORT).show();
                            testingConfigurator.updWordRepetitionCounter(1);
                            upUI();
                        } else {
                            testingConfigurator.updWordRepetitionCounter(seekBar.getProgress());
                        }
                        break;
                    case 2:
                        if (seekBar.getProgress() == 0){
                            Toast.makeText(getApplicationContext(), "Nope", Toast.LENGTH_SHORT).show();
                            testingConfigurator.updCorrectWordsCounter(1);
                            upUI();
                        } else {
                            testingConfigurator.updCorrectWordsCounter(seekBar.getProgress());
                        }
                        break;
                }
                break;
        }
    }

    private void startTest(){

        if (testingConfigurator.getPickedWordUids().size() > 2){
            Intent intent = new Intent(this, TestingActivity.class);
            intent.putExtra("DictionaryName", getDictionary());
            intent.putExtra("DictionarySize", testingConfigurator.getDictSize()-1);
            intent.putExtra(TestConfig.class.getCanonicalName(), testingConfigurator);
            startActivity(intent);
            finish();
        } else {
            Snackbar.make(tvStart, "Select more then 2 words for test", Snackbar.LENGTH_LONG).show();
        }
//
//        for (String o:testingConfigurator.getPickedWordUids()) {
//            Log.i(TAG, "startTest: " + o);
//        }
    }
}
