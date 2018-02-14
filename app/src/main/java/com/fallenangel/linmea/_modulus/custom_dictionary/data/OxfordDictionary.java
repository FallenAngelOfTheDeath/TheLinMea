///*
// * Created by Кондрашов Дмитрий Эдуардович
// * Copyright (C) 2018. All rights reserved.
// * email: kondrashovde@gmail.com
// *
// * Last modified 1/26/18 5:59 PM
// */
//
//package com.fallenangel.linmea._modulus.custom_dictionary.data;
//
//import com.fallenangel.linmea._modulus.non.SynonymsAntonymsCallbackTask;
//
//import org.json.JSONException;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//
//import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Action0;
//import rx.functions.Action1;
//import rx.functions.Func1;
//import rx.schedulers.Schedulers;
//
///**
// * Created by NineB on 1/6/2018.
// */
//
//public class OxfordDictionary {
//
//    private Object jsonString;
//
//    private List<String> antonyms;
//    private List<String> synonyms;
//    private List<String> examples;
//    private String lexicalCategory;
//
//    private OxDictionary oD;
//
//    private List<OxDictionary> oxDictionary;
//
//    public OxfordDictionary(String id) throws ExecutionException, InterruptedException, JSONException {
//        oxDictionary = new ArrayList<>();
//        antonyms = new ArrayList<>();
//        synonyms = new ArrayList<>();
//        examples = new ArrayList<>();
//        //jsonString = getWordDataById(id);
//        getResult(jsonString);
//    }
//    public OxfordDictionary() throws ExecutionException, InterruptedException, JSONException {
//        oxDictionary = new ArrayList<>();
//        antonyms = new ArrayList<>();
//        synonyms = new ArrayList<>();
//        examples = new ArrayList<>();
//    }
//
//    private void setNull(){
//        oD = new SynonymsAntonymsCallbackTask.OxDictionary();
//        antonyms = new ArrayList<>();
//        synonyms = new ArrayList<>();
//        examples = new ArrayList<>();
//        lexicalCategory = null;
//    }
//
//   // public Object getWordDataById (String id) throws ExecutionException, InterruptedException {
//       // AsyncTask str = new SynonymsAntonymsCallbackTask().execute(dictionaryEntries(id));
//      //  return str.get();
//  //  }
//
//    public List<SynonymsAntonymsCallbackTask.OxDictionary> getWordById (String id) throws ExecutionException, InterruptedException, JSONException {
//        final List<SynonymsAntonymsCallbackTask.OxDictionary>[] tmpList = {new ArrayList<>()};
//        SynonymsAntonymsCallbackTask.getOxfordResult(dictionaryEntries(id))
//                .map(new Func1<String, List<OxDictionary>>() {
//                    @Override
//                    public List<OxDictionary> call(String s) {
//                        try {
//                            return getResult(s);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            return null;
//                        }
//                    }
//                })
//                .doOnNext(new Action1<List<OxDictionary>>() {
//                    @Override
//                    public void call(List<OxDictionary> oxDictionaries) {
//                        tmpList[0] = oxDictionaries;
//                    }
//                })
//              .subscribeOn(Schedulers.io())
//              .observeOn(AndroidSchedulers.mainThread())
//              .doOnCompleted(new Action0() {
//                  @Override
//                  public void call() {
//                        return tmpList[0];
//                  }
//              })
//              .doOnError(throwable -> System.out.println(throwable))
//              .subscribe();
//        //AsyncTask str = new SynonymsAntonymsCallbackTask().execute(dictionaryEntries(id));
//        //Log.i("ghg", "Async: " + str.get() +
//        //        " just: " + SynonymsAntonymsCallbackTask.oxfordNetworkCall(dictionaryEntries(id)));
//       // return getResult(str.get());
//        return null;
//    }
//
//    private String dictionaryEntries(String id) {
//        final String language = "en";
//        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + id.toLowerCase() + "/synonyms;antonyms";
//    }
//
////    public List<OxDictionary> getResult(Object json) throws JSONException {
////        JSONObject obj = new JSONObject(json.toString());
////        JSONObject results = obj.getJSONArray("results").getJSONObject(0);
////
////        JSONArray lexicalEntries = results.getJSONArray("lexicalEntries");
////        for (int i = 0; i < lexicalEntries.length(); i++) {
////            setNull();
////
////
////            JSONArray entriesAntonyms = null;
////            try {
////                entriesAntonyms = lexicalEntries.getJSONObject(i).getJSONArray("entries")
////                        .getJSONObject(0)
////                        .getJSONArray("senses").getJSONObject(0).getJSONArray("antonyms");
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
////
////            if(entriesAntonyms != null) {
////                for (int j = 0; j < entriesAntonyms.length(); j++) {
////                    antonyms.add(entriesAntonyms.getJSONObject(j).getString("text"));
////                }
////            }
////
////            JSONArray entriesSynonyms = lexicalEntries.getJSONObject(i).getJSONArray("entries")
////                    .getJSONObject(0)
////                    .getJSONArray("senses").getJSONObject(0).getJSONArray("synonyms");
////
////            for (int j = 0; j < entriesSynonyms.length(); j++) {
////                synonyms.add(entriesSynonyms.getJSONObject(j).getString("text"));
////            }
////
////
////            JSONArray entriesExamples = lexicalEntries.getJSONObject(i).getJSONArray("entries").getJSONObject(0)
////                    .getJSONArray("senses").getJSONObject(0).getJSONArray("examples");
////
////            for (int j = 0; j < entriesExamples.length(); j++) {
////                examples.add(entriesExamples.getJSONObject(j).getString("text"));
////            }
////
////            oD.setLexicalCategory(lexicalEntries.getJSONObject(i).getString("lexicalCategory"));
////            oD.setAntonyms(antonyms);
////            oD.setExamples(examples);
////            oD.setSynonyms(synonyms);
////
////            oxDictionary.add(oD);
////
////        }
////
////        return oxDictionary;
////    }
//
////    public class OxDictionary{
////        private List<String> antonyms;
////        private List<String> synonyms;
////        private List<String> examples;
////        private String lexicalCategory;
////
////        public OxDictionary() {
////        }
////
////        public List<String> getAntonyms() {
////            return antonyms;
////        }
////
////        public List<String> getSynonyms() {
////            return synonyms;
////        }
////
////        public List<String> getExamples() {
////            return examples;
////        }
////
////        public String getLexicalCategory() {
////            return lexicalCategory;
////        }
////
////        public void setAntonyms(List<String> antonyms) {
////            this.antonyms = antonyms;
////        }
////
////        public void setSynonyms(List<String> synonyms) {
////            this.synonyms = synonyms;
////        }
////
////        public void setExamples(List<String> examples) {
////            this.examples = examples;
////        }
////
////        public void setLexicalCategory(String lexicalCategory) {
////            this.lexicalCategory = lexicalCategory;
////        }
////
////        public String synonymsToString(){
////            String synonymsString = "";
////            for (int i = 0; i < getSynonyms().size(); i++) {
////                synonymsString = synonymsString + ", " + getSynonyms().get(i);
////            }
////            return synonymsString.replaceFirst(", ", "").trim();
////        }
////
////        public String antonymsToString(){
////            String antonymsToString = "";
////            for (int i = 0; i < getAntonyms().size(); i++) {
////                antonymsToString = antonymsToString + ", " + getAntonyms().get(i);
////            }
////            return antonymsToString.replaceFirst(", ", "").trim();
////        }
////
////        public String examplesToString() {
////            String examplesString = "";
////            for (int i = 0; i < getExamples().size(); i++) {
////                examplesString = examplesString + ", " + getExamples().get(i);
////            }
////            return examplesString.replaceFirst(", ", "").trim();
////        }
////
////        public String toString (){
////            String oxDictionaryString = "";
////
////            oxDictionaryString = "Lexical Category: " + getLexicalCategory() + "\n" + "\n";
////            if (synonymsIsEmpty()){
////                oxDictionaryString = oxDictionaryString
////                        + "Synonyms: " + synonymsToString() + "\n" + "\n";
////            }
////            if (antonymsIsEmpty()){
////                oxDictionaryString = oxDictionaryString
////                        + "Antonyms: " + antonymsToString() + "\n" + "\n";
////            }
////            if (examplesIsEmpty()){
////                oxDictionaryString = oxDictionaryString
////                        + "Examples: " + examplesToString();
////            }
////            return oxDictionaryString;
////        }
////
////
////        public Boolean antonymsIsEmpty(){
////            if (!antonyms.isEmpty()) return true;
////            return false;
////        }
////        public Boolean synonymsIsEmpty(){
////            if (!synonyms.isEmpty()) return true;
////            return false;
////        }
////        public Boolean examplesIsEmpty(){
////            if (!examples.isEmpty()) return true;
////            return false;
////        }
////    }
//
////    public List<OxDictionary> getOxDictionary() {
////        return oxDictionary;
////    }
//
//
//}
