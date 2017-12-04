package com.fallenangel.linmea._linmea.data.collection;

import com.fallenangel.linmea._linmea.model.CustomDictionaryModel;

import java.util.Comparator;

/**
 * Created by NineB on 10/2/2017.
 */
public class DictionaryCompare {

    public static class WordCompare implements Comparator<CustomDictionaryModel> {
        @Override
        public int compare(CustomDictionaryModel o1, CustomDictionaryModel o2) {
            return o1.getWord().toLowerCase().compareTo(o2.getWord().toLowerCase());
        }
    }

    public static class TranslationCompare implements Comparator<CustomDictionaryModel> {
        @Override
        public int compare(CustomDictionaryModel o1, CustomDictionaryModel o2) {
            return o1.getTranslationString().toLowerCase().compareTo(o2.getTranslationString().toLowerCase());
        }
    }

}

