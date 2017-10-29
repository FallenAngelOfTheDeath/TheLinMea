package com.fallenangel.linmea.linmea.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.fallenangel.linmea.R;

/**
 * Created by NineB on 9/30/2017.
 */

public class CustomDictionaryPreference extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dictionary_preference);

        getFragmentManager().beginTransaction().replace(R.id.main_pref_container, new CustomDictionaryPreference.SettingFragment()).commit();
    }

    public static class SettingFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.custom_dictionary_settings);


        }

    }

}
