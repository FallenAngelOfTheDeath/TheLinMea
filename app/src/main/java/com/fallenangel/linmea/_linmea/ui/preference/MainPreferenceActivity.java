package com.fallenangel.linmea._linmea.ui.preference;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.model.DictionaryCustomizer;

import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.*;

public class MainPreferenceActivity extends AppCompatActivity implements View.OnClickListener {

    private DictionaryCustomizer mDictionaryCustomizer;

    private TextView mOptionsMenuDescription, mTTSSpeedDescription;
    private LinearLayout mOptionsMenuContainer, mTTSSpeedContainer;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_preference);
        mDictionaryCustomizer = new DictionaryCustomizer(this, MAIN_GLOBAL_SETTINGS);
        implementUI();
    }

    private void implementUI(){
        mOptionsMenuDescription = (TextView) findViewById(R.id.pref_options_menu_description);
        mTTSSpeedDescription = (TextView) findViewById(R.id.pref_tts_description);
        mOptionsMenuContainer = (LinearLayout) findViewById(R.id.pref_options_menu_container);
        mTTSSpeedContainer = (LinearLayout) findViewById(R.id.pref_tts_menu_container);

        mOptionsMenuContainer.setOnClickListener(this);
        mTTSSpeedContainer.setOnClickListener(this);
        mOptionsMenuDescription.setText(mDictionaryCustomizer.switchHelper(mDictionaryCustomizer.getOptionsMenu(), R.array.options_menu));
        mTTSSpeedDescription.setText(mDictionaryCustomizer.switchHelper(mDictionaryCustomizer.getTTSSpeed(this), R.array.speed_of_tts));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pref_options_menu_container:
                mDictionaryCustomizer.alertDialogOptionsMenu();
                break;
            case R.id.pref_tts_menu_container:
                mDictionaryCustomizer.alertDialogTTSSpeed();
                break;
        }
    }
}
