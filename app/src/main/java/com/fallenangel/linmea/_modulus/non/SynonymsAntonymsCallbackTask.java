/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 9:01 PM
 */

package com.fallenangel.linmea._modulus.non;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by NineB on 12/6/2017.
 */

public class SynonymsAntonymsCallbackTask
       // extends AsyncTask<String, Integer, String>
{

    private Object jsonString;

    private List<String> antonyms;
    private List<String> synonyms;
    private List<String> examples;
    private String lexicalCategory;

    private OxDictionary oD;

    private List<OxDictionary> oxDictionary;

    static HttpsURLConnection urlConnection;

    public SynonymsAntonymsCallbackTask() {
        oxDictionary = new ArrayList<>();
        antonyms = new ArrayList<>();
        synonyms = new ArrayList<>();
        examples = new ArrayList<>();
    }


//    @Override
//    protected String doInBackground(String... params) {
//        return oxfordNetworkCall(params[0]);
//    }

    public static String oxfordNetworkCall(String urlll){
        final String app_id = "982e4f2d";
        final String app_key = "2dea6529ee36cea250dae685f60996e6";
        try {
            URL url = new URL(urlll);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setRequestProperty("app_id",app_id);
            urlConnection.setRequestProperty("app_key",app_key);

            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        } finally {
//            urlConnection.disconnect();
        }
    }



    public rx.Observable<String> getOxfordResult(String id){
        return rx.Observable.create(new rx.Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String response = oxfordNetworkCall(dictionaryEntries(id));
                    subscriber.onNext(response);
                    subscriber.onCompleted();
                    subscriber.onCompleted();
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public void getOxDictionary(String id, OxfordListener oxfordListener){
        getOxfordResult(id)
                .map(s -> {
                    try {
                        return parseResult(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
//                .filter(oxDictionaries -> {
//                    if (oxDictionaries != null) return true;
//                    else return false;
//                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(oxDictionaries -> {
                    try {
                        oxfordListener.doOnCompleted(oxDictionaries);
                    } catch (NullPointerException e){
                        System.out.println(e);
                        oxfordListener.doOnError(e);
                    }
                })
                .doOnCompleted(() -> {
                    try {
                        urlConnection.disconnect();
                    } catch (Exception e){
                        System.out.println(e);
                    }
                })
                .doOnError(throwable -> {
                    try {
                        urlConnection.disconnect();
                    } catch (Exception e){
                        System.out.println(e);
                    }
                    oxfordListener.doOnError(throwable);
                })
                .subscribe();


    }

    public interface OxfordListener{
        void doOnCompleted(List<OxDictionary> list);
        void doOnError(Throwable throwable);
    }

    private String dictionaryEntries(String id) {
        final String language = "en";
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + id.toLowerCase() + "/synonyms;antonyms";
    }

    private void setNull(){
        oD = new OxDictionary();
        antonyms = new ArrayList<>();
        synonyms = new ArrayList<>();
        examples = new ArrayList<>();
        lexicalCategory = null;
    }

    public List<OxDictionary> parseResult(Object json) throws JSONException {
        JSONObject obj = new JSONObject(json.toString());
        JSONObject results = obj.getJSONArray("results").getJSONObject(0);

        JSONArray lexicalEntries = results.getJSONArray("lexicalEntries");
        for (int i = 0; i < lexicalEntries.length(); i++) {
            setNull();


            JSONArray entriesAntonyms = null;
            try {
                entriesAntonyms = lexicalEntries.getJSONObject(i).getJSONArray("entries")
                        .getJSONObject(0)
                        .getJSONArray("senses").getJSONObject(0).getJSONArray("antonyms");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(entriesAntonyms != null) {
                for (int j = 0; j < entriesAntonyms.length(); j++) {
                    antonyms.add(entriesAntonyms.getJSONObject(j).getString("text"));
                }
            }

            JSONArray entriesSynonyms = lexicalEntries.getJSONObject(i).getJSONArray("entries")
                    .getJSONObject(0)
                    .getJSONArray("senses").getJSONObject(0).getJSONArray("synonyms");

            for (int j = 0; j < entriesSynonyms.length(); j++) {
                synonyms.add(entriesSynonyms.getJSONObject(j).getString("text"));
            }


            JSONArray entriesExamples = lexicalEntries.getJSONObject(i).getJSONArray("entries").getJSONObject(0)
                    .getJSONArray("senses").getJSONObject(0).getJSONArray("examples");

            for (int j = 0; j < entriesExamples.length(); j++) {
                examples.add(entriesExamples.getJSONObject(j).getString("text"));
            }

            oD.setLexicalCategory(lexicalEntries.getJSONObject(i).getString("lexicalCategory"));
            oD.setAntonyms(antonyms);
            oD.setExamples(examples);
            oD.setSynonyms(synonyms);

            oxDictionary.add(oD);

        }
        return oxDictionary;
    }

    public class OxDictionary{
        private List<String> antonyms;
        private List<String> synonyms;
        private List<String> examples;
        private String lexicalCategory;

        public OxDictionary() {
        }

        public List<String> getAntonyms() {
            return antonyms;
        }

        public List<String> getSynonyms() {
            return synonyms;
        }

        public List<String> getExamples() {
            return examples;
        }

        public String getLexicalCategory() {
            return lexicalCategory;
        }

        public void setAntonyms(List<String> antonyms) {
            this.antonyms = antonyms;
        }

        public void setSynonyms(List<String> synonyms) {
            this.synonyms = synonyms;
        }

        public void setExamples(List<String> examples) {
            this.examples = examples;
        }

        public void setLexicalCategory(String lexicalCategory) {
            this.lexicalCategory = lexicalCategory;
        }

        public String synonymsToString(){
            String synonymsString = "";
            for (int i = 0; i < getSynonyms().size(); i++) {
                synonymsString = synonymsString + ", " + getSynonyms().get(i);
            }
            return synonymsString.replaceFirst(", ", "").trim();
        }

        public String antonymsToString(){
            String antonymsToString = "";
            for (int i = 0; i < getAntonyms().size(); i++) {
                antonymsToString = antonymsToString + ", " + getAntonyms().get(i);
            }
            return antonymsToString.replaceFirst(", ", "").trim();
        }

        public String examplesToString() {
            String examplesString = "";
            for (int i = 0; i < getExamples().size(); i++) {
                examplesString = examplesString + ", " + getExamples().get(i);
            }
            return examplesString.replaceFirst(", ", "").trim();
        }

        public String toString (){
            String oxDictionaryString = "";

            oxDictionaryString = "Lexical Category: " + getLexicalCategory() + "\n" + "\n";
            if (synonymsIsEmpty()){
                oxDictionaryString = oxDictionaryString
                        + "Synonyms: " + synonymsToString() + "\n" + "\n";
            }
            if (antonymsIsEmpty()){
                oxDictionaryString = oxDictionaryString
                        + "Antonyms: " + antonymsToString() + "\n" + "\n";
            }
            if (examplesIsEmpty()){
                oxDictionaryString = oxDictionaryString
                        + "Examples: " + examplesToString();
            }
            return oxDictionaryString;
        }


        public Boolean antonymsIsEmpty(){
            if (!antonyms.isEmpty()) return true;
            return false;
        }
        public Boolean synonymsIsEmpty(){
            if (!synonyms.isEmpty()) return true;
            return false;
        }
        public Boolean examplesIsEmpty(){
            if (!examples.isEmpty()) return true;
            return false;
        }
    }
}


