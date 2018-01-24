package com.fallenangel.linmea._linmea.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by NineB on 12/4/2017.
 */

public class TranslatorTask extends AsyncTask<String, Void, String> {

    private AsyncResponse delegate;

    public TranslatorTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        String textToTranslate = params[0];
        String languagePair = params[1];
        String jsonResult;
        try {
            String yandexKey = "trnsl.1.1.20171204T173948Z.38b5e8c4370a447a.880aa8ef40fb744d054d8fa98af8f6ce03f5c23d";
            String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                    + "&text=" + textToTranslate + "&lang=" + languagePair;
            URL yandexTranslateURL = new URL(yandexUrl);

            HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
            InputStream inputStream = httpJsonConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder jsonStringBuilder = new StringBuilder();
            while ((jsonResult = bufferedReader.readLine()) != null) {
                jsonStringBuilder.append(jsonResult + "\n");
            }
            bufferedReader.close();
            inputStream.close();
            httpJsonConnection.disconnect();

            String resultString = jsonStringBuilder.toString().trim();
            resultString = resultString.substring(resultString.indexOf('[') + 1);
            resultString = resultString.substring(0, resultString.indexOf("]"));
            resultString = resultString.substring(resultString.indexOf("\"") + 1);
            resultString = resultString.substring(0, resultString.indexOf("\""));
            Log.d("TranslationResult:", resultString);
            //return jsonStringBuilder.toString().trim();
            return resultString;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
      //  super.onPostExecute(s);
        delegate.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public interface AsyncResponse {
        void onPostExecute(String s);
    }

}
