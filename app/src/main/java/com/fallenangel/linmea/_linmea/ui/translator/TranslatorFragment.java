package com.fallenangel.linmea._linmea.ui.translator;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.util.SharedPreferencesUtils;
import com.fallenangel.linmea._linmea.util.TranslatorTask;

import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class TranslatorFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TranslatorFragment";
    private TextView outputTex, sourceLang, targetLang, yandexUrl;
    private EditText inputText;
    private String mLangPair;
    private Toolbar mToolbar;

    private static final String EN_RU_PAIR = "en-ru";
    private static final String RU_EN_PAIR = "ru-en";

    public TranslatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.translator));
        sourceLang = (TextView) rootView.findViewById(R.id.source_language);
        targetLang = (TextView) rootView.findViewById(R.id.target_language);
        updateUI();
        ImageView changeLang = (ImageView) rootView.findViewById(R.id.change_language);
        changeLang.setOnClickListener(this);
        inputText = (EditText) rootView.findViewById(R.id.input_text);
        outputTex = (TextView) rootView.findViewById(R.id.output_text);
        yandexUrl = (TextView) rootView.findViewById(R.id.yandex_link);
        Button translate = (Button) rootView.findViewById(R.id.translate);
        translate.setOnClickListener(this);
        yandexUrl.setOnClickListener(this);
        yandexUrl.setClickable(false);

        return rootView;
    }

    public String getLangPair() {
        mLangPair =  SharedPreferencesUtils.getFromSharedPreferences(getActivity(), "translator", "pair");
        if (mLangPair == null) {
            setLangPair(EN_RU_PAIR);
            return EN_RU_PAIR;
        }
        return mLangPair;
    }

    public void setLangPair(String langPair) {
        SharedPreferencesUtils.putToSharedPreferences(getActivity(), "translator", "pair", langPair);
        mLangPair = langPair;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.translate:
                TranslatorTask translatorTask = new TranslatorTask(getActivity());
                String translationResult = null;
                try {
                    translationResult = translatorTask.execute(inputText.getText().toString(), getLangPair()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                outputTex.setText(translationResult);
                yandexUrl.setText("“Powered by Yandex.Translate”");
                yandexUrl.setClickable(true);
                break;
            case R.id.change_language:
                switch (getLangPair()){
                    case EN_RU_PAIR:
                        Toast.makeText(getActivity(), "EN_RU_PAIR", Toast.LENGTH_LONG).show();
                        setLangPair(RU_EN_PAIR);
                        updateUI();
                        Log.i(TAG, "onClick: " + getLangPair());
                        break;
                    case RU_EN_PAIR:
                        Toast.makeText(getActivity(), "RU_EN_PAIR", Toast.LENGTH_LONG).show();

                        setLangPair(EN_RU_PAIR);
                        updateUI();
                        Log.i(TAG, "onClick: " + getLangPair());
                        break;
                }
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
}
