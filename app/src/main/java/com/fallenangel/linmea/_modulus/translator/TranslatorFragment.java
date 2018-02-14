/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/28/18 7:24 PM
 */

package com.fallenangel.linmea._modulus.translator;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.auth.View.RxEditText;
import com.fallenangel.linmea._modulus.main.supclasses.SuperFragment;
import com.fallenangel.linmea._modulus.non.Constant;
import com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey;
import com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode;
import com.fallenangel.linmea._modulus.prferences.utils.PreferenceUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import rx.Observable;

/**
 * A simple {@link Fragment} subclass.
 */
public class TranslatorFragment extends SuperFragment implements View.OnClickListener, TranslatorTask.AsyncResponse {

    @Inject
    Context mContext;

    private static final String TAG = "TranslatorFragment";
    private TextView outputTex, sourceLang, targetLang, yandexUrl;
    private EditText inputText;
    private String mLangPair;
    private Toolbar mToolbar;
    private Button translate;
    private ImageView changeLang;
    private String translationResult;
    private TranslatorTask translatorTask;
    private InputMethodManager imm;
    private static final String EN_RU_PAIR = "en-ru";
    private static final String RU_EN_PAIR = "ru-en";

    private Observable<String> inputTextObservable;

    public TranslatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        sourceLang = (TextView) rootView.findViewById(R.id.source_language);
        targetLang = (TextView) rootView.findViewById(R.id.target_language);
        yandexUrl = (TextView) rootView.findViewById(R.id.yandex_link);
        outputTex = (TextView) rootView.findViewById(R.id.output_text);

        inputText = (EditText) rootView.findViewById(R.id.input_text);

        changeLang = (ImageView) rootView.findViewById(R.id.change_language);
        translate = (Button) rootView.findViewById(R.id.translate);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.translator));
        updateUI();
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        changeLang.setOnClickListener(this);
        translate.setOnClickListener(this);
        yandexUrl.setOnClickListener(this);

        translate.setEnabled(false);
        yandexUrl.setClickable(false);
        translate.setClickable(false);


        inputTextObservable = RxEditText.getTextWatcherOnAfterTextChanged(inputText);

        inputTextObservable.exists(s -> {
            if (s.isEmpty())
                return false;
            else
                return true;
        }).doOnNext(aBoolean -> {
            translate.setEnabled(aBoolean);
            translate.setClickable(aBoolean);
        }).subscribe();

        return rootView;
    }

    public String getLangPair() {
        mLangPair = PreferenceUtils.getStringPreference(mContext, PreferenceMode.TRANSLATOR, PreferenceKey.TRANSLATION_PAIR);
        //mLangPair =  SharedPreferencesUtils.getFromSharedPreferences(getActivity(), "translator", "pair");
        if (mLangPair == null) {
            setLangPair(EN_RU_PAIR);
            return EN_RU_PAIR;
        }
        return mLangPair;
    }

    public void setLangPair(String langPair) {
        PreferenceUtils.putPreference(mContext, PreferenceMode.TRANSLATOR, PreferenceKey.TRANSLATION_PAIR, langPair);
        //SharedPreferencesUtils.putToSharedPreferences(getActivity(), "translator", "pair", langPair);
        mLangPair = langPair;
    }

    @Override
    public void onClick(View v) {
        if (Constant.DEBUG == 1) Log.d(TAG, "onClick: " + v.getId());
        switch (v.getId()){
            case R.id.translate:
                //translateText(inputText.getText().toString(), getLangPair());


                translatorTask = new TranslatorTask(this);
                try {
                    translationResult = translatorTask.execute(inputText.getText().toString(), getLangPair()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);


                break;
            case R.id.change_language:
                switch (getLangPair()){
                    case EN_RU_PAIR:
                        setLangPair(RU_EN_PAIR);
                        updateUI();
                        break;
                    case RU_EN_PAIR:
                        setLangPair(EN_RU_PAIR);
                        updateUI();
                        break;
                }
                break;
            case R.id.yandex_link:
                String url = "http://translate.yandex.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
    }


    private void updateUI(){
        switch (getLangPair()){
            case EN_RU_PAIR:
                sourceLang.setText("English");
                targetLang.setText("Russian");
                break;
            case RU_EN_PAIR:
                sourceLang.setText("Russian");
                targetLang.setText("English");
                break;
        }
    }

    private void saveToHistory(String sourceText, String translatedText){
        String basePath = "translator/history/" + User.getCurrentUserUID();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String key = databaseReference
                .child(basePath)
                .push().getKey();
        Translation translation = new Translation();
        translation.setUID(key);
        translation.setSourceText(sourceText);
        translation.setTranslatedText(translatedText);

        databaseReference.child(basePath + "/" + key).setValue(translation.toMap());
    }

    @Override
    public void onPostExecute(String s) {
        outputTex.setText(s);
        yandexUrl.setText("“Powered by Yandex.Translate”");
        yandexUrl.setClickable(true);
        saveToHistory(inputText.getText().toString(), s);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("TRANSLATED_TEXT", inputText.getText().toString());
        outState.putString("TRANSLATED_TEXT", translationResult);
    }
}
