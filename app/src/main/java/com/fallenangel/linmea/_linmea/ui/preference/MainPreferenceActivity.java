package com.fallenangel.linmea._linmea.ui.preference;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.model.DictionaryCustomizer;
import com.fallenangel.linmea._linmea.util.ColorUtils;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.non.Constant;
import com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import javax.inject.Inject;

import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.MAIN_GLOBAL_SETTINGS;

public class MainPreferenceActivity extends SuperAppCompatActivity implements View.OnClickListener {

    @Inject public DictionaryCustomizer mDictionaryCustomizer;
    private ColorUtils colorUtils = new ColorUtils();

    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;

    private Toolbar mToolbar;
    private TextView mOptionsMenuDescription, mTTSSpeedDescription, mLearnedColorPickerDescription, mSelectedColorPickerDescription, mDefaultPageDescription;
    private LinearLayout mOptionsMenuContainer, mTTSSpeedContainer, mLearnedColorPickerContainer, mSelectedColorPicker, mDefaultPage;
    private Button okColor;
    private int colorPickerType;

    private ColorPicker colorPicker;

    private int mCheckedItemOfAlertDialog;
    private int mPickedFilterValue;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_preference);
        getAppComponent().inject(this);


        mDictionaryCustomizer.loadMode(MAIN_GLOBAL_SETTINGS);
        //mDictionaryCustomizer = new DictionaryCustomizer(this, MAIN_GLOBAL_SETTINGS);
        //colorPicker = new ColorPicker(this, defaultAlphaValue, defaultColorR, defaultColorG, defaultColorB);
        implementUI();
    }

    private void implementUI(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setTitle("Main settings");
        setSupportActionBar(mToolbar);

        mOptionsMenuDescription = (TextView) findViewById(R.id.pref_options_menu_description);
        mTTSSpeedDescription = (TextView) findViewById(R.id.pref_tts_description);
        mLearnedColorPickerDescription = (TextView) findViewById(R.id.color_picker_description);
        mSelectedColorPickerDescription = (TextView) findViewById(R.id.color_of_selected_description);
        mDefaultPageDescription = (TextView) findViewById(R.id.default_page_description);

        mOptionsMenuContainer = (LinearLayout) findViewById(R.id.pref_options_menu_container);
        mTTSSpeedContainer = (LinearLayout) findViewById(R.id.pref_tts_menu_container);
        mLearnedColorPickerContainer = (LinearLayout) findViewById(R.id.color_picker_container);
        mSelectedColorPicker = (LinearLayout) findViewById(R.id.color_of_selected_container);
        mDefaultPage = (LinearLayout) findViewById(R.id.default_page_container);
        
        mOptionsMenuContainer.setOnClickListener(this);
        mTTSSpeedContainer.setOnClickListener(this);
        mLearnedColorPickerContainer.setOnClickListener(this);
        mSelectedColorPicker.setOnClickListener(this);
        mDefaultPage.setOnClickListener(this);
        
       updateUI();
    }

    private void updateUI(){
        mOptionsMenuDescription.setText(mDictionaryCustomizer.switchHelper(getApplicationContext(), mDictionaryCustomizer.getOptionsMenu(), R.array.options_menu));
        mTTSSpeedDescription.setText(mDictionaryCustomizer.switchHelper(getApplicationContext(), mDictionaryCustomizer.getTTSSpeedNonStat(), R.array.speed_of_tts));
        //mColorPickerDescription.setText();
        mSelectedColorPickerDescription.setText(colorUtils.getColorNameFromRgb(Color.red(mDictionaryCustomizer.getColorOfSelected()),
                Color.green(mDictionaryCustomizer.getColorOfSelected()), Color.blue(mDictionaryCustomizer.getColorOfSelected())));
        mLearnedColorPickerDescription.setText(colorUtils.getColorNameFromRgb(Color.red(mDictionaryCustomizer.getLearnedColor()),
                Color.green(mDictionaryCustomizer.getLearnedColor()), Color.blue(mDictionaryCustomizer.getLearnedColor())));


        mDefaultPageDescription.setText(mDictionaryCustomizer.getDefaultPage().getName());
    }

    private void implementColorPicker(int defaultAlphaValue, int defaultColorR, int defaultColorG, int defaultColorB){
        colorPicker = new ColorPicker(this, defaultAlphaValue, defaultColorR, defaultColorG, defaultColorB);
        colorPicker.show();
        okColor = (Button)colorPicker.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (Constant.DEBUG == 1) Log.i("gggg", "onClick: " + v.getId());
        switch (v.getId()){
            case -1:
                finish();
                break;
            case R.id.pref_options_menu_container:
                alertDialogOptionsMenu();
                break;
            case R.id.pref_tts_menu_container:
                alertDialogTTSSpeed();
                break;
            case R.id.color_picker_container:
                colorPickerType = 0;
                implementColorPicker(
                        Color.alpha(mDictionaryCustomizer.getLearnedColor()),
                        Color.red(mDictionaryCustomizer.getLearnedColor()),
                        Color.green(mDictionaryCustomizer.getLearnedColor()),
                        Color.blue(mDictionaryCustomizer.getLearnedColor()));
                break;
            case R.id.okColorButton:
                switch(colorPickerType){
                    case 0:
                        mDictionaryCustomizer.setLearnedColor(MainPreferenceActivity.this, colorPicker.getAlpha(), colorPicker.getRed(), colorPicker.getGreen(), colorPicker.getBlue());
                        break;
                    case 1:
                        mDictionaryCustomizer.setColorOfSelected(MainPreferenceActivity.this, colorPicker.getAlpha(), colorPicker.getRed(), colorPicker.getGreen(), colorPicker.getBlue());
                        break;
                }
                colorPicker.dismiss();
                updateUI();
                break;
            case R.id.color_of_selected_container:
                colorPickerType = 1;
                implementColorPicker(
                        Color.alpha(mDictionaryCustomizer.getColorOfSelected()),
                        Color.red(mDictionaryCustomizer.getColorOfSelected()),
                        Color.green(mDictionaryCustomizer.getColorOfSelected()),
                        Color.blue(mDictionaryCustomizer.getColorOfSelected()));
                break;
            case R.id.default_page_container:
                alertDialogPickDefaultPage();
                break;
        }
    }
    private void alertDialogPickDefaultPage() {
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle(this.getResources().getString(R.string.default_start_up_page));
        String[] items = PreferenceMode.toArrayNames();
        PreferenceMode saved = mDictionaryCustomizer.getDefaultPage();
        if (saved != null) {
            for (int i = 0; i < items.length; i++) {
                if (items[i].equals(saved.getName()))
                    mCheckedItemOfAlertDialog = i;
            }
        } else {
            saved = PreferenceMode.CUSTOM_DICTIONARY_PAGE_1;
        }
        mAlertDialogBuilder.setSingleChoiceItems(items, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mPickedFilterValue = i;
            }
        });
        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (PreferenceMode item:PreferenceMode.values()) {
                    if (item.getName().equals(items[mPickedFilterValue]))
                        mDictionaryCustomizer.setDefaultPage(item);
                }
                updateUI();
            }
        });
        mAlertDialogBuilder.setNegativeButton(this.getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    public void alertDialogOptionsMenu(){
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle(this.getResources().getString(R.string.options_menu_title));
        //mAlertDialogBuilder.setMessage(mContext.getResources().getString(R.string.description_options_menu));
        mCheckedItemOfAlertDialog = mDictionaryCustomizer.getOptionsMenu();
        mAlertDialogBuilder.setSingleChoiceItems(R.array.options_menu, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPickedFilterValue = which;
            }
        });
        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDictionaryCustomizer.setOptionsMenu(mPickedFilterValue);
                updateUI();
            }
        });
        mAlertDialogBuilder.setNegativeButton(this.getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    public void alertDialogTTSSpeed(){
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle(this.getResources().getString(R.string.speed_of_tts));
        //mAlertDialogBuilder.setMessage(mContext.getResources().getString(R.string.description_options_menu));
        mCheckedItemOfAlertDialog = mDictionaryCustomizer.getTTSSpeedNonStat();
        mAlertDialogBuilder.setSingleChoiceItems(R.array.speed_of_tts, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPickedFilterValue = which;
            }
        });
        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDictionaryCustomizer.setTTSSpeed(getApplicationContext(), mPickedFilterValue);
                //updateUI.updateUI();
            }
        });
        mAlertDialogBuilder.setNegativeButton(this.getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }
}
