/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/28/18 9:50 PM
 */

package com.fallenangel.linmea._modulus.main_dictionary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.non.Constant;
import com.fallenangel.linmea._modulus.non.SynonymsAntonymsCallbackTask;
import com.fallenangel.linmea._modulus.prferences.DictionaryCustomizer;
import com.fallenangel.linmea._modulus.prferences.ui.MainPreferenceActivity;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ViewMainWordActivity extends SuperAppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {

    @Inject
    Context context;
    private Toolbar mToolbar;
    private ImageView ivTTS;
    private EditText etWord, etTranslation;
    private TextView tvOxford;
    private CardView cvOxford;
    private Intent intent;
    private String mWord, mTranslation;
    private TextToSpeech mTextToSpeech;
    private static int speedOfTTS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_main_word);
        getAppComponent().inject(this);
        mTextToSpeech = new TextToSpeech(context, this);
        getIntentData();

        implementView();
    }

    @Override
    protected void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void getIntentData(){
        intent = getIntent();
        mWord = intent.getStringExtra(Constant.MAIN_WORD);
        mTranslation = intent.getStringExtra(Constant.MAIN_TRANSLATION);
    }

    private void implementView(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setTitle(R.string.main_dictionary);

        ivTTS = (ImageView) findViewById(R.id.play_word);
        etWord = (EditText) findViewById(R.id.word_edit_text);
        etTranslation = (EditText) findViewById(R.id.translation_edit_text);
        tvOxford = (TextView) findViewById(R.id.oxford_tv);
        cvOxford = (CardView) findViewById(R.id.oxford_card_view);

        ivTTS.setOnClickListener(this);

        etWord.setText(mWord);
        etTranslation.setText(mTranslation);
        getOxfordData(mWord);
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int result = mTextToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(context, getResources().getString(R.string.lang_is_not_supported), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setSpeed(){
        speedOfTTS = DictionaryCustomizer.getTTSSpeed(context);
        switch (speedOfTTS){
            case 0:
                mTextToSpeech.setSpeechRate(0.1f);
                break;
            case 1:
                mTextToSpeech.setSpeechRate(0.5f);
                break;
            case 2:
                mTextToSpeech.setSpeechRate(1.0f);
                break;
            case 3:
                mTextToSpeech.setSpeechRate(1.5f);
                break;
            case 4:
                mTextToSpeech.setSpeechRate(2.0f);
                break;
            default:
                mTextToSpeech.setSpeechRate(1.0f);
                break;
        }
    }

    private void speakOut(String textToSpeech) {
        mTextToSpeech.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case -1:
                onBackPressed();
                break;
            case R.id.play_word:
                setSpeed();
                speakOut(etWord.getText().toString());
                break;
        }
    }

    private void getOxfordData(String word){
        cvOxford.setVisibility(View.VISIBLE);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbarr_main);
        progressBar.setVisibility(View.VISIBLE);
        SynonymsAntonymsCallbackTask ox = new SynonymsAntonymsCallbackTask();
        final String[] oxfordStr = {""};
        ox.getOxDictionary(word, new SynonymsAntonymsCallbackTask.OxfordListener() {
            @Override
            public void doOnCompleted(List<SynonymsAntonymsCallbackTask.OxDictionary> l) {
                rx.Observable.from(l)
                        .doOnNext(new Action1<SynonymsAntonymsCallbackTask.OxDictionary>() {
                            @Override
                            public void call(SynonymsAntonymsCallbackTask.OxDictionary oxItem) {
                                if (oxfordStr[0] == "") oxfordStr[0] = oxItem.toString();
                                else oxfordStr[0] = oxfordStr[0] + "\n\n" + oxItem.toString();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                progressBar.setVisibility(View.GONE);
                                tvOxford.setText(oxfordStr[0]);
                            }
                        })
                        .doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                progressBar.setVisibility(View.GONE);
                                cvOxford.setVisibility(View.VISIBLE);
                                tvOxford.setText("no information about " + word);
                            }
                        })
                        .subscribe();

            }

            @Override
            public void doOnError(Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                cvOxford.setVisibility(View.VISIBLE);
                tvOxford.setText("no information about " + word);
            }
        });




//        try {
//            oxfordDictionary = new OxfordDictionary(word);
//            cvOxford.setVisibility(View.VISIBLE);
//            List<OxfordDictionary.OxDictionary> oxDictionary = oxfordDictionary.getOxDictionary();
//            String oxfordStr = "";
//            for (OxfordDictionary.OxDictionary item:oxDictionary) {
//                if (oxfordStr == "") oxfordStr = item.toString();
//                else oxfordStr = oxfordStr + "\n\n" + item.toString();
//            }
//            tvOxford.setText(oxfordStr);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_main_dict_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
                doShare();
                break;
            case R.id.settings:
                Intent settings = new Intent(context, MainPreferenceActivity.class);
                startActivity(settings);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void doShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Word: " + mWord  +
                        "\nTranslation: " + mTranslation);
        startActivity(intent);
    }
}
