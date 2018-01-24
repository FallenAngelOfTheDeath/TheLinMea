package com.fallenangel.linmea._linmea.model;

import android.os.AsyncTask;

import com.fallenangel.linmea._linmea.data.SynonymsAntonymsCallbackTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by NineB on 1/6/2018.
 */

public class OxfordDictionary {
    //private Gson gson = new Gson();
    private static final String TAG = "OxfordDictionary";

    private String id;
    private Object jsonString;

    private SynonymsAntonymsCallbackTask synonymsAntonymsCallbackTask;

    private List<String> antonyms;
    private List<String> synonyms;
    private List<String> examples;
    private String lexicalCategory;

    private OxDictionary oD;

    private List<OxDictionary> oxDictionary;

    public OxfordDictionary(String id) throws ExecutionException, InterruptedException, JSONException {
        this.id = id;
        oxDictionary = new ArrayList<>();
        antonyms = new ArrayList<>();
        synonyms = new ArrayList<>();
        examples = new ArrayList<>();
        jsonString = getWordDataById(id);
        getResult(jsonString);
    }

    private void setNull(){
        oD = new OxDictionary();
        antonyms = new ArrayList<>();
        synonyms = new ArrayList<>();
        examples = new ArrayList<>();
        lexicalCategory = null;
    }

    public Object getWordDataById (String id) throws ExecutionException, InterruptedException {
        AsyncTask str = new SynonymsAntonymsCallbackTask().execute(dictionaryEntries(id));
        return str.get();
    }

    private String dictionaryEntries(String id) {
        final String language = "en";
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + id.toLowerCase() + "/synonyms;antonyms";
    }

    public void getResult(Object json) throws JSONException {
        JSONObject obj = new JSONObject(json.toString());
        JSONObject results = obj.getJSONArray("results").getJSONObject(0);

        JSONArray lexicalEntries = results.getJSONArray("lexicalEntries");
        for (int i = 0; i < lexicalEntries.length(); i++) {
            setNull();
            //Log.i(TAG, "getResult: __________________________" + i + "________________________________");
           // Log.i(TAG, "getResult: " + lexicalEntries.getJSONObject(i).getString("lexicalCategory"));


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
                 //   Log.i(TAG, "getResult antonyms: " + entriesAntonyms.getJSONObject(j).getString("text"));
                }
            }

            JSONArray entriesSynonyms = lexicalEntries.getJSONObject(i).getJSONArray("entries")
                    .getJSONObject(0)
                    .getJSONArray("senses").getJSONObject(0).getJSONArray("synonyms");

            for (int j = 0; j < entriesSynonyms.length(); j++) {
                synonyms.add(entriesSynonyms.getJSONObject(j).getString("text"));
             //   Log.i(TAG, "getResult synonyms: " + entriesSynonyms.getJSONObject(j).getString("text"));
            }


            JSONArray entriesExamples = lexicalEntries.getJSONObject(i).getJSONArray("entries").getJSONObject(0)
                    .getJSONArray("senses").getJSONObject(0).getJSONArray("examples");

            for (int j = 0; j < entriesExamples.length(); j++) {
                examples.add(entriesExamples.getJSONObject(j).getString("text"));
             //   Log.i(TAG, "getResult examples: " + entriesExamples.getJSONObject(j).getString("text"));
            }

            oD.setLexicalCategory(lexicalEntries.getJSONObject(i).getString("lexicalCategory"));
            oD.setAntonyms(antonyms);
            oD.setExamples(examples);
            oD.setSynonyms(synonyms);

            oxDictionary.add(oD);

          //  Log.i(TAG, "getResult[" + i + "]: " + lexicalEntries.get(i).toString());

        }

//        for (int i = 0; i < oxDictionary.size(); i++) {
//            Log.i(TAG, "getResult dict: " + oxDictionary.get(i).toString());
//        }

    }

    public List<OxDictionary> getOxDictionary() {
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
