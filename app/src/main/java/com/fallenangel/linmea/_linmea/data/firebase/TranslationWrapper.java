package com.fallenangel.linmea._linmea.data.firebase;

import com.fallenangel.linmea._linmea.model.Translation;
import com.google.firebase.database.DataSnapshot;

/**
 * Created by NineB on 1/17/2018.
 */

public class TranslationWrapper {

    private String uid, sourceText, translatedText;
    public Translation getTranslationHistory(DataSnapshot dataSnapshot){
        uid = dataSnapshot.getKey();
        sourceText = (String) dataSnapshot.child("source_text").getValue();
        translatedText = (String) dataSnapshot.child("translated_text").getValue();

        Translation trHistory = new Translation();

        trHistory.setUID(uid);
        trHistory.setSourceText(sourceText);
        trHistory.setTranslatedText(translatedText);
        return trHistory;
    }
}
