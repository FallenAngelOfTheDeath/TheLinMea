/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.main.dagger;

import com.fallenangel.linmea._modulus.auth.ui.LoginActivity;
import com.fallenangel.linmea._modulus.auth.ui.SignUpActivity;
import com.fallenangel.linmea._modulus.auth.ui.UserProfileActivity;
import com.fallenangel.linmea._modulus.custom_dictionary.ui.AddCustomDictionaryActivity;
import com.fallenangel.linmea._modulus.custom_dictionary.ui.CustomDictionaryFragment;
import com.fallenangel.linmea._modulus.custom_dictionary.ui.CustomDictionaryListFragment;
import com.fallenangel.linmea._modulus.custom_dictionary.ui.DetailFragment;
import com.fallenangel.linmea._modulus.grammar.ui.CategoryGrammarFragment;
import com.fallenangel.linmea._modulus.grammar.ui.DetailGrammarActivity;
import com.fallenangel.linmea._modulus.grammar.ui.GrammarsListActivity;
import com.fallenangel.linmea._modulus.grammar.ui.OnlyFavoriteGrammarFragment;
import com.fallenangel.linmea._modulus.main.ui.MainActivity;
import com.fallenangel.linmea._modulus.main.ui.SplashScreen;
import com.fallenangel.linmea._modulus.main_dictionary.MainDictionaryFragment;
import com.fallenangel.linmea._modulus.main_dictionary.ViewMainWordActivity;
import com.fallenangel.linmea._modulus.prferences.ui.DictionaryCustomizerActivity;
import com.fallenangel.linmea._modulus.prferences.ui.MainPreferenceActivity;
import com.fallenangel.linmea._modulus.testing.TestConfiguratorActivity;
import com.fallenangel.linmea._modulus.testing.TestingActivity;
import com.fallenangel.linmea._modulus.translator.TranslateHistory;
import com.fallenangel.linmea._modulus.translator.TranslatorFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by NineB on 1/18/2018.
 */
@Singleton
@Component(modules = {AppModule.class, AndroidModule.class})
public interface AppComponent {
    void inject(LoginActivity activity);
    void inject(SignUpActivity activity);
    void inject(SplashScreen activity);
    void inject(MainActivity activity);
    void inject(MainPreferenceActivity activity);
    void inject(DictionaryCustomizerActivity activity);
    void inject(UserProfileActivity activity);
    void inject(CustomDictionaryFragment fragment);
    void inject(TranslatorFragment fragment);
    void inject(CustomDictionaryListFragment fragment);
    void inject(OnlyFavoriteGrammarFragment fragment);
    void inject(GrammarsListActivity activity);
    void inject(DetailGrammarActivity activity);
    void inject(MainDictionaryFragment fragment);
    void inject(ViewMainWordActivity activity);
    void inject(TranslateHistory fragment);
    void inject(TestingActivity activity);
    void inject(TestConfiguratorActivity activity);
    void inject(CategoryGrammarFragment activity);
    void inject(AddCustomDictionaryActivity activity);
    void inject(DetailFragment fragment);
}
