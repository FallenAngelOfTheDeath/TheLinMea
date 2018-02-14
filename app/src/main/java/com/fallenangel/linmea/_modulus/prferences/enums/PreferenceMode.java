/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.prferences.enums;

import com.fallenangel.linmea._modulus.custom_dictionary.ui.CustomDictionaryFragment;
import com.fallenangel.linmea._modulus.custom_dictionary.ui.CustomDictionaryListFragment;
import com.fallenangel.linmea._modulus.grammar.ui.CategoryGrammarFragment;
import com.fallenangel.linmea._modulus.grammar.ui.OnlyFavoriteGrammarFragment;
import com.fallenangel.linmea._modulus.main_dictionary.MainDictionaryFragment;
import com.fallenangel.linmea._modulus.prferences.ui.MainPreferenceActivity;
import com.fallenangel.linmea._modulus.translator.TranslateHistory;
import com.fallenangel.linmea._modulus.translator.TranslatorFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NineB on 1/19/2018.
 */

public enum PreferenceMode {
    MAIN_DICTIONARY_PAGE_1(0, "Main dictionary", true, MainDictionaryFragment.class),
    CUSTOM_DICTIONARY_PAGE_1(1, "Custom dictionary 1st page", true, CustomDictionaryFragment.class),
    CUSTOM_DICTIONARY_PAGE_2(2, "Custom dictionary 2nd page", true, CustomDictionaryFragment.class),
    CUSTOM_DICTIONARY_LIST(3, "List of custom dictionaries", true, CustomDictionaryListFragment.class),
    //SHARED_DICTIONARY_PAGE_1,
    //SHARED_DICTIONARY_PAGE_2
    SOCIETY_FRIENDS_LIST(8, "Friends", false, null),
    GRAMMAR_CATEGORIES(4, "Grammar categories", true, CategoryGrammarFragment.class),
    GRAMMAR_FAVORITE(5, "Favorite grammar", true, OnlyFavoriteGrammarFragment.class),
    TRANSLATOR(6, "Translator", true, TranslatorFragment.class),
    TRANSLATOR_HISTORY(7, "History of translate", true, TranslateHistory.class),
    MAIN_GLOBAL_SETTINGS(8, "Main settings", false, MainPreferenceActivity.class),
    SINGLE_PAGE(9, "Single custom dictionary", false, CustomDictionaryFragment.class);

    int index;
    private String name, UID;
    private Boolean status;
    private Class fragment;

    PreferenceMode(int index, String name, Boolean status, Class fragment) {
        this.index = index;
        this.name = name;
        this.status = status;
        this.fragment = fragment;
    }

    public String getName(){
        return name;
    }

    public Boolean getStatus() {
        return status;
    }

    public Class getFragment() {
        return fragment;
    }

    public static PreferenceMode strToEnum (String str) {
        try {
            return valueOf(str);
        } catch (Exception ex) {
            return null;
        }
    }

    public int getIndex() {
        return index;
    }

    public static String[] toArrayNames() {
        PreferenceMode[] mods = PreferenceMode.values();
        //String[] names = new String[mods.length];
        List<String> str = new ArrayList<>();
        for (int i = 0; i < mods.length; i++) {
            if (mods[i].getStatus() == true)
                str.add(mods[i].getName());
                //names[i] = mods[i].getName();
        }

        return str.toArray(new String[str.size()]);
    }


}
