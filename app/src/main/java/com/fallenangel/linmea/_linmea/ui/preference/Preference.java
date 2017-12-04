package com.fallenangel.linmea._linmea.ui.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fallenangel.linmea.R;

/**
 * Created by NineB on 9/30/2017.
 */

public class Preference extends AppCompatActivity {

        private Toolbar mToolbar;

        public static class CustomDictionaryPreference extends AppCompatActivity implements View.OnClickListener {
        private Toolbar mToolbar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_custom_dictionary_preference);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            mToolbar.setNavigationOnClickListener(this);
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            mToolbar.setTitle(this.getResources().getString(R.string.title_activity_settings));
            getFragmentManager().beginTransaction().replace(R.id.main_pref_container, new SettingFragment()).commit();
        }

            @Override
            public void onClick(View v) {
                finish();
            }

            public static class SettingFragment extends PreferenceFragment {
            @Override
            public void onCreate(final Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.custom_dictionary_settings);
            }
        }
    }

    public static class MainPreference extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_custom_dictionary_preference);
            getFragmentManager().beginTransaction().replace(R.id.main_pref_container, new MainSettings()).commit();
        }
        public static class MainSettings extends PreferenceFragment{
            @Override
            public void onCreate(final Bundle savedInstanceState){
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.custom_dictionary_settings);
            }
        }
    }

    public static class DetailViewPreference extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_custom_dictionary_preference);
            getFragmentManager().beginTransaction().replace(R.id.main_pref_container, new DetailViewSetting()).commit();
        }
        public static class DetailViewSetting extends PreferenceFragment {
            @Override
            public void onCreate(final Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.detail_view_settings);
            }
        }
    }

}
