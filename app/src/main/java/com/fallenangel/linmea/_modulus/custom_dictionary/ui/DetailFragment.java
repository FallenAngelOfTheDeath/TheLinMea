/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 9:01 PM
 */

package com.fallenangel.linmea._modulus.custom_dictionary.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.custom_dictionary.adapter.LinkedWordAdapter;
import com.fallenangel.linmea._modulus.custom_dictionary.data.FirebaseDictionaryWrapper;
import com.fallenangel.linmea._modulus.custom_dictionary.data.RxFirebaseHelper;
import com.fallenangel.linmea._modulus.custom_dictionary.model.CustomDictionaryModel;
import com.fallenangel.linmea._modulus.grammar.db.OnFillListListener;
import com.fallenangel.linmea._modulus.main.supclasses.SuperFragment;
import com.fallenangel.linmea._modulus.non.Constant;
import com.fallenangel.linmea._modulus.non.SynonymsAntonymsCallbackTask;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.prferences.DictionaryCustomizer;
import com.fallenangel.linmea._modulus.prferences.ui.MainPreferenceActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends SuperFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, TextToSpeech.OnInitListener, OnRecyclerViewClickListener {

    @Inject Context mContext;
    @Inject DatabaseReference mDBR;
    ProgressBar progressBar;
    private static final String TAG = "DetailFragment";
    private EditText mWordET, mTranslationET, mDescriptionET;
    private Switch mLearnedSwitcher, mFavoriteSwitcher;
    private FloatingActionButton mFabDetail;
    private CardView mDescriptionCV;
    private RecyclerView mLinkedRV;
    private ImageView mAddLinked;
    private FirebaseDictionaryWrapper mFirebaseDictionaryWrapper = new FirebaseDictionaryWrapper();
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
//    OxfordDictionary oxfordDictionary = null;
    private int mDictionarySize;
    private TextToSpeech mTextToSpeech;
    private ImageView mPlayWord;
    private LinkedWordAdapter mLinkedWordAdapter;
    private static int speedOfTTS = 2;
    private List<String> mLinkedUIDS = new ArrayList<>();

    private HashSet<CustomDictionaryModel> mLinkedItems = new HashSet<>();
    private String wordKey;
    private List<CustomDictionaryModel> mItems = new ArrayList<>();
    private SparseBooleanArray mLinkedBooleanArray = new SparseBooleanArray();
    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;
    boolean[] checkedItems;
    ArrayList<String> mArrayStr;

    private Intent resultIntent = new Intent();
    private Boolean updUIResult = false;
    private CustomDictionaryModel currentItem;
    private TextView mLexicalCategory;
    private CardView mOxfordCardView;


    private String mMode;
    private Boolean mDetailEdit = false;
    public DetailFragment() {
        // Required empty public constructor
    }

    private CustomDictionaryModel getCurrentItem(){
        return (CustomDictionaryModel) getArguments().getSerializable("Item");
    }



    private String getWord(){
        return currentItem.getWord();
    }

    private String getTranslation(){
        return currentItem.getTranslationString();
    }

    private String getWordUID(){
        if (currentItem.getUID() == null)
            return wordKey;
        else
            return currentItem.getUID();
    }

    private String getDictionary() {
        return getArguments().getString("DictionaryName");
    }

    private int getDictionarySize(){
        return getArguments().getInt("DictionarySize");
    }

    private String getMod(){
        return getArguments().getString("Mod");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getAppComponent().inject(this);
        mMode = getMod();
        currentItem = getCurrentItem();
    }

    @Override
    public void onStart() {
        super.onStart();
        mDictionarySize = getDictionarySize();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_word_detail, container, false);
        implementUI(view);
        implementRecyclerView(view);
        implementRecyclerViewAdapter();
        implementIntentData();
        setMods();
        mTextToSpeech = new TextToSpeech(getActivity(), this);
        return view;
    }

    private void implementUI(View view) {
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbarr);
        mDescriptionCV = (CardView) view.findViewById(R.id.description_card_view);
        mOxfordCardView = (CardView) view.findViewById(R.id.oxford_card_view);
        mWordET = (EditText) view.findViewById(R.id.word_detail_edit_text);
        mPlayWord = (ImageView)  view.findViewById(R.id.play_word);
        mTranslationET = (EditText) view.findViewById(R.id.translation_detail_edit_text);
        mDescriptionET = (EditText) view.findViewById(R.id.description_detail_edit_text);
        mLearnedSwitcher = (Switch) view.findViewById(R.id.learned_switcher);
        mFavoriteSwitcher = (Switch) view.findViewById(R.id.favorite_switcher);
        mLexicalCategory = (TextView) view.findViewById(R.id.oxford_lexical_category);
        mAddLinked = (ImageView) view.findViewById(R.id.add_linked_word);
        mFabDetail = (FloatingActionButton) view.findViewById(R.id.fab_detail);
        mAddLinked.setOnClickListener(this);
        mFabDetail.setOnClickListener(this);
        mOxfordCardView.setVisibility(view.GONE);
        mPlayWord.setVisibility(View.GONE);
        mDescriptionCV.setVisibility(View.GONE);
       // mWordET.setText(getWord());
        //mTranslationET.setText(getTranslation());
    }

    private void implementIntentData(){
        if (currentItem != null){
            mWordET.setText(getWord());
            mTranslationET.setText(getTranslation());
            implementItemData(currentItem);
        } else {
            mWordET.setText("");
            mTranslationET.setText("");
        }
//        for (CustomDictionaryModel item:mItems) {
//            if (item.getUID().equals(getWordUID())) {
//                currentItem = item;
//                implementItemData(item);
//            }
//            collectLinked();
//        }
    }

    private void setDetailViewMod () {
        mMode = "DetailViewMod";
        mPlayWord.setVisibility(View.VISIBLE);
        mWordET.setEnabled(false);
        mTranslationET.setEnabled(false);
        mDescriptionET.setEnabled(false);
        mWordET.setCursorVisible(false);
        mTranslationET.setCursorVisible(false);
        mDescriptionET.setCursorVisible(false);
        mPlayWord.setOnClickListener(this);
        mFavoriteSwitcher.setOnCheckedChangeListener(this);
        mLearnedSwitcher.setOnCheckedChangeListener(this);
        mWordET.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);
        mTranslationET.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);
        mDescriptionET.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);
        mFabDetail.setImageResource(R.drawable.ic_edit);
    }

    private void setEditMod () {
        mMode = "EditMod";
        mDescriptionCV.setVisibility(View.VISIBLE);
        mWordET.setEnabled(true);
        mWordET.setCursorVisible(true);
        mTranslationET.setEnabled(true);
        mDescriptionET.setEnabled(true);
        mTranslationET.setCursorVisible(true);
        mDescriptionET.setCursorVisible(true);
        mWordET.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        mTranslationET.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        mDescriptionET.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        mFabDetail.setImageResource(R.drawable.ic_done);
        //mWordET.invalidate();
    }

    private void setAddMod () {
        mDescriptionCV.setVisibility(View.VISIBLE);
        mDescriptionET.setText("");
        mDescriptionET.setText("");
        mFabDetail.setImageResource(R.drawable.ic_done);
        mFavoriteSwitcher.setChecked(false);
        mLearnedSwitcher.setChecked(false);
    }

    private void implementRecyclerView(View rootView) {
        mLinkedRV = (RecyclerView) rootView.findViewById(R.id.recycler_view_linked);
        mLinkedRV.setHasFixedSize(true);
        mLinkedRV.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void implementRecyclerViewAdapter() {
        mLinkedWordAdapter = new LinkedWordAdapter(mLinkedItems, this);
        mLinkedWordAdapter.clear();
        mLinkedRV.setAdapter(mLinkedWordAdapter);
        mLinkedWordAdapter.notifyDataSetChanged();
    }

    private void loadDictionary() {
        RxFirebaseHelper mFBHelper =
                new RxFirebaseHelper<>(mDBR, mItems,
                        "/custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/" + getDictionary(),
                        "/custom_dictionary/" + User.getCurrentUserUID() + "/dictionaries/" + getDictionary());
        mFBHelper.singleListFiller(new OnFillListListener<CustomDictionaryModel>() {
            @Override
            public void doOnNext(CustomDictionaryModel item) {
//                if (item.getUID().equals(getWordUID()))
//                    currentItem[0] = item;
            }

            @Override
            public void doOnCompleted() {
//                if (!mMode.equals(Constant.ADD_MOD))
//                    implementItemData(currentItem[0]);
                try {
                    collectLinked(currentItem.getLinked());
                } catch (NullPointerException e){
                    System.out.println(e);
                } catch (Exception e){
                    System.out.println(e);
                }
            }

            @Override
            public CustomDictionaryModel itemWrapper(DataSnapshot dataSnapshot) {
                return mFirebaseDictionaryWrapper.getCustomDictionaryWord(dataSnapshot);
            }
        });
    }

    private void collectLinked(List<String> mLinkedUIDS){
        rx.Observable.from(mLinkedUIDS)
                .doOnNext(uid -> {
                    for (int i = 0; i < mItems.size(); i++) {
                        if (mItems.get(i).getUID().equals(uid)){
                            mLinkedItems.add(mItems.get(i));
                            mLinkedBooleanArray.put(i, true);
                        }
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> mLinkedWordAdapter.notifyDataSetChanged())
                .subscribe();
    }

    private void implementItemData(CustomDictionaryModel item){
        mOxfordCardView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        updUIResult = false;
        if (item.getDescription() != null){
            mDescriptionCV.setVisibility(View.VISIBLE);
            mDescriptionET.setText(item.getDescription());
        } else {
            if (mMode.equals("DetailViewMod"))
                mDescriptionCV.setVisibility(View.GONE);
            else
                mDescriptionET.setText("");
        }
        if (item.getFavorite() != null)
        mFavoriteSwitcher.setChecked(item.getFavorite());
        if (item.getStatus() != null)
            mLearnedSwitcher.setChecked(item.getStatus());
        if (item.getLinked() != null){
            mLinkedUIDS.addAll(item.getLinked());
        }
        //if (!mMode.equals(Constant.ADD_MOD) || !mMode.equals("EditMod")) {
        if (mMode.equals("DetailViewMod")) {
            SynonymsAntonymsCallbackTask ox = new SynonymsAntonymsCallbackTask();
            final String[] oxfordStr = {""};
            ox.getOxDictionary(item.getWord(), new SynonymsAntonymsCallbackTask.OxfordListener() {
                @Override
                public void doOnCompleted(List<SynonymsAntonymsCallbackTask.OxDictionary> l) {
                    rx.Observable.from(l)
                            .doOnNext(new Action1<SynonymsAntonymsCallbackTask.OxDictionary>() {
                                @Override
                                public void call(SynonymsAntonymsCallbackTask.OxDictionary oxItem) {
                                    if (oxfordStr[0] == "") oxfordStr[0] = oxItem.toString();
                                    else oxfordStr[0] = oxfordStr[0] + "\n\n" + oxItem.toString();
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnCompleted(new Action0() {
                                @Override
                                public void call() {
                                    progressBar.setVisibility(View.GONE);
                                    mLexicalCategory.setText(oxfordStr[0]);
                                }
                            })
                            .doOnError(new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    progressBar.setVisibility(View.GONE);
                                    mOxfordCardView.setVisibility(View.VISIBLE);
                                    mLexicalCategory.setText("no information about " + item.getWord());
                                }
                            })
                            .subscribe();

                }

                @Override
                public void doOnError(Throwable throwable) {
                    progressBar.setVisibility(View.GONE);
                    mOxfordCardView.setVisibility(View.VISIBLE);
                    mLexicalCategory.setText("no information about " + item.getWord());
                }
            });


//            try {
//                oxfordDictionary = new OxfordDictionary();
//                oxfordDictionary.getWordById(item.getWord());
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

//
//            try {
//                oxfordDictionary = new OxfordDictionary();
//                final String[] oxfordStr = {""};
//                rx.Observable.from(oxfordDictionary.getWordById(item.getWord()))
//                        .doOnNext(new Action1<OxfordDictionary.OxDictionary>() {
//                            @Override
//                            public void call(OxfordDictionary.OxDictionary oxItem) {
//                                if (oxfordStr[0] == "") oxfordStr[0] = oxItem.toString();
//                                else oxfordStr[0] = oxfordStr[0] + "\n\n" + oxItem.toString();
//                            }
//                        })
//                        .subscribeOn(Schedulers.computation())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .doOnCompleted(new Action0() {
//                            @Override
//                            public void call() {
//                                mLexicalCategory.setText(oxfordStr[0]);
//                                mOxfordCardView.setVisibility(View.VISIBLE);
//                            }
//                        })
//                        .subscribe();
//
//
//
//            //
//            //                oxfordDictionary = new OxfordDictionary(item.getWord());
//            //                mOxfordCardView.setVisibility(View.VISIBLE);
//            //
//            //                List<OxfordDictionary.OxDictionary> oxDictionary = oxfordDictionary.getOxDictionary();
//            //                String oxfordStr = "";
//            //                for (OxfordDictionary.OxDictionary oxItem : oxDictionary) {
//            //                    if (oxfordStr == "") oxfordStr = oxItem.toString();
//            //                    else oxfordStr = oxfordStr + "\n\n" + oxItem.toString();
//            //                }
//            //                mLexicalCategory.setText(oxfordStr);
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    private void setMods () {
        switch (mMode){
            case "DetailViewMod":
                setDetailViewMod();
                loadDictionary();
                break;
            case "EditMod":
                setEditMod();
                loadDictionary();
                break;
            case Constant.ADD_MOD:
                setAddMod();
                loadDictionary();
                break;
        }
    }

    private void alertDialogLinkedWords(){
        checkedItems = new boolean[mItems.size()];
        mAlertDialogBuilder = new AlertDialog.Builder(getActivity());
        List<Integer> mCheckedItemsOfAlertDialog = null;
        mArrayStr = new ArrayList<>();
        for (int j = 0; j < mItems.size(); j++) {
            mArrayStr.add(mItems.get(j).getWord() + " - " + mItems.get(j).getTranslationString());
        }
        String[] mStringOfDictionaries = mArrayStr.toArray(new String[0]);
        for (int j = 0; j < mItems.size(); j++) {
            if (mLinkedBooleanArray.get(j) == true){
                checkedItems[j] = true;
            } else {
                checkedItems[j] = false;
            }
        }
        if (mItems.isEmpty()){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View text_view_layout = inflater.inflate(R.layout.text_view_alert_dialog_dictionary, null);
            mAlertDialogBuilder.setView(text_view_layout);
        }else {
            mAlertDialogBuilder.setTitle(R.string.dict_customizer_dict_description);
            mAlertDialogBuilder.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_multiple_choice, mStringOfDictionaries), null);
            mAlertDialogBuilder.setMultiChoiceItems(mStringOfDictionaries, checkedItems, (dialog, which, isChecked) -> mLinkedBooleanArray.put(which, isChecked));
        }
        mAlertDialogBuilder.setPositiveButton(R.string.ok, (dialog, which) -> addLinkedWord());
        mAlertDialogBuilder.setNegativeButton(this.getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    private void addLinkedWord(){
        if(mLinkedUIDS != null)
            mLinkedUIDS.clear();
        for (int i = 0; i < mItems.size(); i++) {
            if (mLinkedBooleanArray.get(i)) mLinkedUIDS.add(mItems.get(i).getUID());
        }
        if (!mMode.equals(Constant.ADD_MOD)) {
            mDBR.child("/custom_dictionary/" + User.getCurrentUserUID() + "/dictionaries/"
                    + getDictionary() + "/" + getWordUID() + "/linked").setValue(mLinkedUIDS);
        }
        if (mLinkedItems != null) mLinkedItems.clear();
        for (String uid: mLinkedUIDS) {
            for (int i = 0; i < mItems.size(); i++) {
                if (mItems.get(i).getUID().equals(uid)){
                    mLinkedItems.add(mItems.get(i));
                    mLinkedWordAdapter.notifyDataSetChanged();
                }
            }
        }
        mLinkedWordAdapter = new LinkedWordAdapter(mLinkedItems, DetailFragment.this);
        mLinkedRV.setAdapter(mLinkedWordAdapter);
        mLinkedWordAdapter.notifyDataSetChanged();

        currentItem.setLinked(mLinkedUIDS);
        resultIntent.putExtra("ITEM_CHANGE", currentItem);
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }

    public void changeWord () {
        String[] translationsList = (mTranslationET.getText().toString()).split(", ");
        ArrayList<String> translationList = new ArrayList<String>(Arrays.asList(translationsList));

        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("word", mWordET.getText().toString());
        hashMap.put("translation", translationList);
        if  (mDescriptionET.getText().length() != 0) {hashMap.put("description", mDescriptionET.getText().toString());}
        hashMap.put("status", mLearnedSwitcher.isChecked());
        hashMap.put("favorite", mFavoriteSwitcher.isChecked());
        hashMap.put("linked", mLinkedUIDS);

        mDBR.child("custom_dictionary")
                .child(User.getCurrentUserUID())
                .child("dictionaries")
                .child(getDictionary())
                .child(getWordUID())
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(mContext, "Word has benn changed", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(mContext, "Oops, something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
        if (mDetailEdit == true) {
            setDetailViewMod();
            mMode = "DetailViewMod";
        }

        resultIntent.putExtra("ITEM_ADD",
               new CustomDictionaryModel((String) hashMap.get("word"), (ArrayList<String>) hashMap.get("translation"), (String) hashMap.get("description"),
                       (Boolean) hashMap.get("favorite"), (Boolean) hashMap.get("status"), (ArrayList<String>) hashMap.get("linked")));
        resultIntent.putExtra("ITEM_CHANGE", currentItem);
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
        getActivity().finish();
    }

    private void addNewWord () {
        Map<String, Object> hashMap = new HashMap<String, Object>();
        if (getDictionary() == null){
            Toast.makeText(getActivity(), "Pleas reselect dictionary in setting of this dictionary", Toast.LENGTH_LONG).show();
        } else {
            String[] translationsList = (mTranslationET.getText().toString()).split(", ");
            ArrayList<String> translationList = new ArrayList<String>(Arrays.asList(translationsList));
            wordKey = mDatabaseReference
                    .child("custom_dictionary")
                    .child(User.getCurrentUserUID())
                    .child("dictionaries")
                    .child(getDictionary())
                    .push()
                    .getKey();

            hashMap.put("id", mDictionarySize);
            hashMap.put("word", mWordET.getText().toString());
            hashMap.put("translation", translationList);
            if (mDescriptionET.getText().length() != 0) {
                hashMap.put("description", mDescriptionET.getText().toString());
            }
            hashMap.put("status", mLearnedSwitcher.isChecked());
            hashMap.put("favorite", mFavoriteSwitcher.isChecked());
            hashMap.put("linked", mLinkedUIDS);

            if (mWordET.getText().length() != 0 && mTranslationET.getText().length() != 0) {
                mDatabaseReference
                        .child("custom_dictionary")
                        .child(User.getCurrentUserUID())
                        .child("dictionaries")
                        .child(getDictionary())
                        .child(wordKey).setValue(hashMap);
                String path = "custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/" + getDictionary() + "/size";
                mDatabaseReference.child(path).setValue(mDictionarySize+1);
                mDictionarySize++;
                actionAfterAdd();
            } else {
                Toast.makeText(getActivity(), R.string.field_of_a_word_or_translation_is_empty, Toast.LENGTH_LONG).show();
            }

        }
        //resultIntent.putExtra("ITEM_ADD",
         //       new CustomDictionaryModel((String) hashMap.get("word"), (ArrayList<String>) hashMap.get("translation"), (String) hashMap.get("description"),
         //               (Boolean) hashMap.get("favorite"), (Boolean) hashMap.get("status"), (ArrayList<String>) hashMap.get("linked")));
        //resultIntent.putExtra("ITEM_ADD", true);
        //getActivity().setResult(Activity.RESULT_OK, resultIntent);

    }

    private void actionAfterAdd(){
        resultIntent.putExtra("ITEM_ADD", true);
        getActivity().setResult(getActivity().RESULT_OK, resultIntent);
            switch (DictionaryCustomizer.getCloseAfterAddOrChangeWord(mContext)) {
                case 0:
                    clearEditMod();
                    break;
                case 1:
                    setDetailViewMod();
                    //replaceFragment(new DetailFragment());
                    break;
                case 2:
                    getActivity().onBackPressed();
                    break;
            }
    }

    private void clearEditMod(){
        mWordET.setText("");
        mTranslationET.setText("");
        mDescriptionET.setText("");
        mFavoriteSwitcher.setChecked(false);
        mLearnedSwitcher.setChecked(false);
        mLinkedWordAdapter.clear();
        mLinkedWordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_word:
                setSpeed();
                speakOut(mWordET.getText().toString());
                break;
            case R.id.fab_detail:
                switch (mMode){
                    case "DetailViewMod":
                        setEditMod();
                        mDetailEdit = true;
                        break;
                    case "EditMod":
                        changeWord();
                        break;
                    case Constant.ADD_MOD:
                        addNewWord();
                        break;
                }
                break;
            case R.id.add_linked_word:
                alertDialogLinkedWords();
                break;
        }
    }

    /*
    *       Options Menu
    */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_view_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent settings = new Intent(getActivity(), MainPreferenceActivity.class);
                startActivity(settings);
                break;
            case R.id.gg:
                doShare();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void doShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Word: " + currentItem.getWord()  +
                        "\nTranslation: " + currentItem.getTranslationString());
        startActivity(intent);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        switch (buttonView.getId()){
            case R.id.favorite_switcher:
                databaseReference
                        .child("custom_dictionary")
                        .child(User.getCurrentUserUID())
                        .child("dictionaries")
                        .child(getDictionary())
                        .child(getWordUID())
                        .child("favorite")
                        .setValue(mFavoriteSwitcher.isChecked());
                currentItem.setFavorite(mFavoriteSwitcher.isChecked());
                resultIntent.putExtra("ITEM_CHANGE", currentItem);
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                break;
            case R.id.learned_switcher:
                databaseReference
                        .child("custom_dictionary")
                        .child(User.getCurrentUserUID())
                        .child("dictionaries")
                        .child(getDictionary())
                        .child(getWordUID())
                        .child("status")
                        .setValue(mLearnedSwitcher.isChecked());
                currentItem.setStatus(mLearnedSwitcher.isChecked());
                resultIntent.putExtra("ITEM_CHANGE", currentItem);
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                break;
        }
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int result = mTextToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getActivity(), getResources().getString(R.string.lang_is_not_supported), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void setSpeed(){
        speedOfTTS = DictionaryCustomizer.getTTSSpeed(getActivity());
        switch (speedOfTTS){
            case 0:
                mTextToSpeech.setSpeechRate(0.1f);
                break;
            case 1:
                mTextToSpeech.setSpeechRate(0.5f);
                break;
            case 2:
                mTextToSpeech.setSpeechRate(1.0f);
                break;
            case 3:
                mTextToSpeech.setSpeechRate(1.5f);
                break;
            case 4:
                mTextToSpeech.setSpeechRate(2.0f);
                break;
            default:
                mTextToSpeech.setSpeechRate(1.0f);
                break;
        }
    }

    private void speakOut(String textToSpeech) {
        mTextToSpeech.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onItemClicked(View view, int position) {
        Intent intent = new Intent(getActivity(), BaseDetailActivity.class);
        intent.putExtra("Word", mLinkedWordAdapter.getItem(position).getWord());
        intent.putExtra("Translation", mLinkedWordAdapter.getItem(position).getTranslationString());
        intent.putExtra("WordUID", covertSetToList(mLinkedItems).get(position).getUID());
        intent.putExtra("DictionaryName", getDictionary());
        intent.putExtra("DictionarySize", mDictionarySize);
        intent.putExtra("Mod", "DetailViewMod");
        startActivity(intent);
    }

    private List<CustomDictionaryModel> covertSetToList(Set<CustomDictionaryModel> mLinkedItems){
        List<CustomDictionaryModel> items = new ArrayList<>();
        for (CustomDictionaryModel subset : mLinkedItems) {
            items.add(subset);
        }
        return items;
    }

    @Override
    public void onOptionsClicked(View view, int position) {
    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        return false;
    }
}