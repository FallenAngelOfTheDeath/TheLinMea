/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/27/18 5:40 PM
 */

package com.fallenangel.linmea._modulus.non;

import com.fallenangel.linmea._modulus.custom_dictionary.model.CustomDictionaryModel;
import com.fallenangel.linmea._modulus.main_dictionary.MainDictionaryModel;

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

    public static class WordCompareMain implements Comparator<MainDictionaryModel> {
        @Override
        public int compare(MainDictionaryModel o1, MainDictionaryModel o2) {
            return o1.getWord().toLowerCase().compareTo(o2.getWord().toLowerCase());
        }
    }

    public static class TranslationCompareMain implements Comparator<MainDictionaryModel> {
        @Override
        public int compare(MainDictionaryModel o1, MainDictionaryModel o2) {
            return o1.getTranslation().toLowerCase().compareTo(o2.getTranslation().toLowerCase());
        }
    }

}

