/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.main.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.auth.ui.LoginActivity;
import com.fallenangel.linmea._modulus.auth.ui.UserProfileActivity;
import com.fallenangel.linmea._modulus.custom_dictionary.ui.CustomDictionaryFragment;
import com.fallenangel.linmea._modulus.custom_dictionary.ui.CustomDictionaryListFragment;
import com.fallenangel.linmea._modulus.grammar.ui.CategoryGrammarFragment;
import com.fallenangel.linmea._modulus.grammar.ui.OnlyFavoriteGrammarFragment;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.main_dictionary.MainDictionaryFragment;
import com.fallenangel.linmea._modulus.non.Constant;
import com.fallenangel.linmea._modulus.non.interfaces.OnFABClickListener;
import com.fallenangel.linmea._modulus.non.utils.Blur;
import com.fallenangel.linmea._modulus.prferences.DictionaryCustomizer;
import com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey;
import com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode;
import com.fallenangel.linmea._modulus.prferences.ui.MainPreferenceActivity;
import com.fallenangel.linmea._modulus.prferences.utils.LoadDefaultConfig;
import com.fallenangel.linmea._modulus.translator.TranslateHistory;
import com.fallenangel.linmea._modulus.translator.TranslatorFragment;

import javax.inject.Inject;

import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.CUSTOM_DICTIONARY_LIST;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.CUSTOM_DICTIONARY_PAGE_1;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.CUSTOM_DICTIONARY_PAGE_2;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.GRAMMAR_CATEGORIES;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.GRAMMAR_FAVORITE;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.MAIN_DICTIONARY_PAGE_1;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.TRANSLATOR;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.TRANSLATOR_HISTORY;

public class MainActivity extends SuperAppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, OnFABClickListener, NavigationView.OnNavigationItemSelectedListener {

    @Inject Context mContext;
    @Inject User user;
    @Inject DictionaryCustomizer mDC;

    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private NavigationView mNavDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private BottomNavigationView mBottomNavigationView;

    private ImageView logOut;
    private TextView signUpTVheader, email;
    private MenuItem miFDictionary, miSDictionary;

    private FrameLayout mContainer;

    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getAppComponent().inject(this);
        implementUI();
        commitDefaultFragment();
            //parseTxt();
    }


    private void commitDefaultFragment(){
        Log.i(TAG, "commitDefaultFragment: " + mDC.getDefaultPage());
        switch (mDC.getDefaultPage()){
            case MAIN_DICTIONARY_PAGE_1:
                fragmentCommit(MAIN_DICTIONARY_PAGE_1.getFragment(), null, MAIN_DICTIONARY_PAGE_1);
                mBottomNavigationView.setVisibility(View.GONE);
                break;

            case CUSTOM_DICTIONARY_PAGE_1:
                fragmentCommit(CUSTOM_DICTIONARY_PAGE_1.getFragment(), null, CUSTOM_DICTIONARY_PAGE_1);
                mBottomNavigationView.setVisibility(View.VISIBLE);
                mBottomNavigationView.getMenu().clear();
                mBottomNavigationView.inflateMenu(R.menu.bottom_action_bar);
                miFDictionary = mBottomNavigationView.getMenu().findItem(R.id.custom_dictionary_page_1_bottom_bar);
                miSDictionary = mBottomNavigationView.getMenu().findItem(R.id.custom_dictionary_page_2_bottom_bar);
                miFDictionary.setTitle(mDC.getCustomDictionaryName(CUSTOM_DICTIONARY_PAGE_1));
                miSDictionary.setTitle(mDC.getCustomDictionaryName(CUSTOM_DICTIONARY_PAGE_2));
                mBottomNavigationView.setOnNavigationItemSelectedListener(this);
                break;

            case CUSTOM_DICTIONARY_PAGE_2:
                fragmentCommit(CUSTOM_DICTIONARY_PAGE_2.getFragment(), null, CUSTOM_DICTIONARY_PAGE_2);
                mBottomNavigationView.setVisibility(View.VISIBLE);
                mBottomNavigationView.getMenu().clear();
                mBottomNavigationView.inflateMenu(R.menu.bottom_action_bar);
                miFDictionary = mBottomNavigationView.getMenu().findItem(R.id.custom_dictionary_page_1_bottom_bar);
                miSDictionary = mBottomNavigationView.getMenu().findItem(R.id.custom_dictionary_page_2_bottom_bar);
                miFDictionary.setTitle(mDC.getCustomDictionaryName(CUSTOM_DICTIONARY_PAGE_1));
                miSDictionary.setTitle(mDC.getCustomDictionaryName(CUSTOM_DICTIONARY_PAGE_2));
                mBottomNavigationView.setOnNavigationItemSelectedListener(this);
                break;

            case CUSTOM_DICTIONARY_LIST:
                fragmentCommit(CUSTOM_DICTIONARY_LIST.getFragment(), null, CUSTOM_DICTIONARY_LIST);
                mBottomNavigationView.setVisibility(View.VISIBLE);
                mBottomNavigationView.getMenu().clear();
                mBottomNavigationView.inflateMenu(R.menu.bottom_action_bar);
                mBottomNavigationView.setOnNavigationItemSelectedListener(this);
                break;

            case GRAMMAR_CATEGORIES:
                fragmentCommit(GRAMMAR_CATEGORIES.getFragment(), null, GRAMMAR_CATEGORIES);
                mBottomNavigationView.setVisibility(View.VISIBLE);
                mBottomNavigationView.getMenu().clear();
                mBottomNavigationView.inflateMenu(R.menu.bottom_actionbar_grammar);
                mBottomNavigationView.setOnNavigationItemSelectedListener(this);
                break;

            case GRAMMAR_FAVORITE:
                fragmentCommit(GRAMMAR_FAVORITE.getFragment(), null, GRAMMAR_FAVORITE);
                mBottomNavigationView.setVisibility(View.VISIBLE);
                mBottomNavigationView.getMenu().clear();
                mBottomNavigationView.inflateMenu(R.menu.bottom_actionbar_grammar);
                mBottomNavigationView.setOnNavigationItemSelectedListener(this);
                break;

            case TRANSLATOR:
                fragmentCommit(TRANSLATOR.getFragment(), null, TRANSLATOR);
                mBottomNavigationView.setVisibility(View.VISIBLE);
                mBottomNavigationView.getMenu().clear();
                mBottomNavigationView.inflateMenu(R.menu.bottom_action_bar_translator);
                mBottomNavigationView.setOnNavigationItemSelectedListener(this);
                break;

            case TRANSLATOR_HISTORY:
                fragmentCommit(TRANSLATOR_HISTORY.getFragment(), null, TRANSLATOR_HISTORY);
                mBottomNavigationView.setVisibility(View.VISIBLE);
                mBottomNavigationView.getMenu().clear();
                mBottomNavigationView.inflateMenu(R.menu.bottom_action_bar_translator);
                mBottomNavigationView.setOnNavigationItemSelectedListener(this);
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserData();
    }

    private void implementNavDrawerHeader () {
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.nav_header, mNavDrawer, false);
        mNavDrawer.addHeaderView(header);

        View headerView = mNavDrawer.getHeaderView(0);
        headerView.setBackgroundResource(R.drawable.header_02);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            Blur.applyBlur(headerView, MainActivity.this);
        logOut = (ImageView) headerView.findViewById(R.id.log_out_header_image_view);
        signUpTVheader = (TextView) headerView.findViewById(R.id.sign_up_header_text_view);
        email = (TextView) headerView.findViewById(R.id.email_header_text_view);
        email.setOnClickListener(this);
        signUpTVheader.setOnClickListener(this);
        logOut.setOnClickListener(this);
        updateUserData();
    }

    private void updateUserData(){
        signUpTVheader.setText(user.getCurrentUser().getDisplayName());
        email.setText(user.getCurrentUser().getEmail());
    }

    private void implementUI (){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavDrawer = (NavigationView) findViewById(R.id.nvView);
        mContainer = (FrameLayout) findViewById(R.id.main_container);
        setSupportActionBar(mToolbar);
        mNavDrawer.setNavigationItemSelectedListener(this);
        mDrawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(mDrawerToggle);
        mBottomNavigationView.getMenu().clear();
        mBottomNavigationView.inflateMenu(R.menu.bottom_action_bar);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        implementNavDrawerHeader();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressLint("RestrictedApi")
    private void fragmentCommit (Class fragmentClass, MenuItem menuItem, PreferenceMode homeFragmentId){
        String fragmentName = homeFragmentId.name();
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentByTag(fragmentName);

        if (fragment == null || !fragment.isVisible()){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                fragment.setArguments(createBundle(homeFragmentId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            transaction
                    .replace(R.id.main_container, fragment, fragmentName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }

        if (menuItem != null)
            menuItem.setChecked(true);
        if (mDrawer.isDrawerOpen(GravityCompat.START))
            mDrawer.closeDrawers();
        setTitle(mDC.getDictionaryName(mContext, homeFragmentId));
        setTitle(homeFragmentId.getName());
        if (miFDictionary != null)
            miFDictionary.setTitle(mDC.getDictionaryName(mContext, CUSTOM_DICTIONARY_PAGE_1));
        if (miSDictionary != null) miSDictionary.setTitle(mDC.getDictionaryName(mContext, CUSTOM_DICTIONARY_PAGE_2));
        if (Constant.DEBUG == 1) Log.i(TAG, "fragmentCommit: " + homeFragmentId.toString());
    }

    public static Bundle createBundle(PreferenceMode mode){
        Bundle bundle = new Bundle();
        bundle.putSerializable(PreferenceKey.DICTIONARY_PAGE.name(), mode);
        return bundle;
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void profileIntent(){
        Intent profileIntent = new Intent(mContext, UserProfileActivity.class);
        startActivity(profileIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_up_header_text_view:
                profileIntent();
                break;
            case R.id.email_header_text_view:
                profileIntent();
                break;
            //--------------------------------------------------------------------------------------
            case R.id.log_out_header_image_view:
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Log OUT");
                adb.setMessage("you are sure?");
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.signOut();
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                adb.setNegativeButton("No", null);
                adb.show();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            //BOTTOM BAR
            case R.id.categories_bottom_bar:
                fragmentCommit(CategoryGrammarFragment.class, item, GRAMMAR_CATEGORIES);
                break;
            case R.id.fav_bottom_bar:
                fragmentCommit(OnlyFavoriteGrammarFragment.class, item, GRAMMAR_FAVORITE);
                break;
            case R.id.translator_bottom_bar:
                fragmentCommit(TranslatorFragment.class, item, TRANSLATOR);
                break;
            case R.id.translator_history_bottom_bar:
                fragmentCommit(TranslateHistory.class, item, TRANSLATOR_HISTORY);
                break;
            case R.id.custom_dictionary_page_1_bottom_bar:
                fragmentCommit(CustomDictionaryFragment.class, item, CUSTOM_DICTIONARY_PAGE_1);
                break;
            case R.id.custom_dictionary_page_2_bottom_bar:
                fragmentCommit(CustomDictionaryFragment.class, item, CUSTOM_DICTIONARY_PAGE_2);
                break;
            case R.id.custom_dictionary_list_bottom_bar:
                fragmentCommit(CustomDictionaryListFragment.class, item, CUSTOM_DICTIONARY_LIST);
                break;


            ///NAV DRAWER
            case R.id.main_dictionaries_item_nd:
                fragmentCommit(MainDictionaryFragment.class, item, MAIN_DICTIONARY_PAGE_1);
                mBottomNavigationView.setVisibility(View.GONE);
                break;
            case R.id.custom_dictionary_item_nd:
                fragmentCommit(CustomDictionaryFragment.class, item, CUSTOM_DICTIONARY_PAGE_1);
                mBottomNavigationView.setVisibility(View.VISIBLE);
                mBottomNavigationView.getMenu().clear();
                mBottomNavigationView.inflateMenu(R.menu.bottom_action_bar);
                miFDictionary = mBottomNavigationView.getMenu().findItem(R.id.custom_dictionary_page_1_bottom_bar);
                miSDictionary = mBottomNavigationView.getMenu().findItem(R.id.custom_dictionary_page_2_bottom_bar);
                miFDictionary.setTitle(mDC.getCustomDictionaryName(CUSTOM_DICTIONARY_PAGE_1));
                miSDictionary.setTitle(mDC.getCustomDictionaryName(CUSTOM_DICTIONARY_PAGE_2));
                mBottomNavigationView.setOnNavigationItemSelectedListener(this);
                break;
            case R.id.shared_dictionaries_item_nd:
                fragmentCommit(TranslatorFragment.class, item, TRANSLATOR);
                mBottomNavigationView.setVisibility(View.VISIBLE);
                mBottomNavigationView.getMenu().clear();
                mBottomNavigationView.inflateMenu(R.menu.bottom_action_bar_translator);
                mBottomNavigationView.setOnNavigationItemSelectedListener(this);
                break;
            case R.id.society_item_nd:
                LoadDefaultConfig loadDefaultConfig = new LoadDefaultConfig(mContext);
                loadDefaultConfig.resetAllSettings();
                Snackbar.make(mContainer, "HAS BEEN RESET", Snackbar.LENGTH_SHORT).show();
                break;
            case  R.id.grammar_item_nd:
                fragmentCommit(CategoryGrammarFragment.class, item, GRAMMAR_CATEGORIES);
                mBottomNavigationView.setVisibility(View.VISIBLE);
                mBottomNavigationView.getMenu().clear();
                mBottomNavigationView.inflateMenu(R.menu.bottom_actionbar_grammar);
                mBottomNavigationView.setOnNavigationItemSelectedListener(this);
                break;
            case R.id.settings_item_nd:
                Intent settings = new Intent(this, MainPreferenceActivity.class);
                startActivity(settings);
                if (mDrawer.isDrawerOpen(GravityCompat.START))
                    mDrawer.closeDrawers();
                break;
            case R.id.about_item_nd:
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
                if (mDrawer.isDrawerOpen(GravityCompat.START))
                    mDrawer.closeDrawers();
                break;
        }
        return false;
    }
//    public  boolean isReadStoragePermissionGranted() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                return true;
//            } else {
//
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
//                return false;
//            }
//        }
//        else {
//            return true;
//        }
//    }
//
//    private class mainTmpDict{
//        String word, translation;
//
//        public mainTmpDict(String word, String translation) {
//            this.word = word;
//            this.translation = translation;
//        }
//    }

//    private void parseTxt() {
//
//        Thread parsingThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                isReadStoragePermissionGranted();
//
//                DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("/main_dictionary/main/");
//
//                String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
//                File file = new File(sdcard, "rsen00.txt");
//                if (file.exists()) {
//                    FileInputStream fIn = null;
//                    try {
//                        fIn = new FileInputStream(file);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    BufferedReader myReader = new BufferedReader(
//                            new InputStreamReader(fIn));
//                    String line;
//
//
//                    int count = 0;
//                    try {
//                        while ((line = myReader.readLine()) != null) {
//
//                            String[] tmp = line.split(":");
//
//                            String[] eng = tmp[1].split(", ");
//
//                            for (String en : eng) {
//                                count++;
//                                HashMap<String, String> word = new HashMap<>();
//                                word.put("word", en.trim());
//                                word.put("translation", tmp[0].trim());
//
//
//                                Log.i(TAG, "Thread: " + Thread.currentThread().getName() + ", parseTxt[" + count + "]: " + word);
//
//                                String key = dbr.push().getKey();
//
//                                dbr.child(key).setValue(word);
//                                try {
//                                    Thread.sleep(250);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        parsingThread.start();
//    }
}
