package com.fallenangel.linmea._modulus.main.dagger;

import com.fallenangel.linmea._linmea.ui.dictionary.CustomDictionaryFragment;
import com.fallenangel.linmea._linmea.ui.dictionary.CustomDictionaryListFragment;
import com.fallenangel.linmea._linmea.ui.preference.DictionaryCustomizerActivity;
import com.fallenangel.linmea._linmea.ui.preference.MainPreferenceActivity;
import com.fallenangel.linmea._linmea.ui.translator.TranslatorFragment;
import com.fallenangel.linmea._modulus.auth.ui.LoginActivity;
import com.fallenangel.linmea._modulus.auth.ui.SignUpActivity;
import com.fallenangel.linmea._modulus.auth.ui.UserProfileActivity;
import com.fallenangel.linmea._modulus.main.ui.MainActivity;
import com.fallenangel.linmea._modulus.main.ui.SplashScreen;

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
}
