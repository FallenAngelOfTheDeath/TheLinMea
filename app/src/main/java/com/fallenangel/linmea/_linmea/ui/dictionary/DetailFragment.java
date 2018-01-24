package com.fallenangel.linmea._linmea.ui.dictionary;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.adapter.LinkedWordAdapter;
import com.fallenangel.linmea._linmea.data.firebase.FirebaseDictionaryWrapper;
import com.fallenangel.linmea._linmea.data.firebase.FirebaseHelper;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._linmea.interfaces.OnValueEventListener;
import com.fallenangel.linmea._linmea.model.CustomDictionaryModel;
import com.fallenangel.linmea._linmea.model.DictionaryCustomizer;
import com.fallenangel.linmea._linmea.model.OxfordDictionary;
import com.fallenangel.linmea._linmea.ui.preference.Preference;
import com.fallenangel.linmea._modulus.auth.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, TextToSpeech.OnInitListener, OnRecyclerViewClickListener {

    private static final String TAG = "DetailFragment";
    private EditText mWordET, mTranslationET, mDescriptionET;
    private Switch mLearnedSwitcher, mFavoriteSwitcher;
    private FloatingActionButton mFabDetail;
    private CardView mDescriptionCV;
    private RecyclerView mLinkedRV, mTagsRV;
    private ImageView mAddLinked;
    private Toolbar mActionBarToolbar;
    private FirebaseHelper<CustomDictionaryModel> mFirebaseHelper = new FirebaseHelper<>(null, null, null,null);
    private FirebaseDictionaryWrapper mFirebaseDictionaryWrapper = new FirebaseDictionaryWrapper();
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    OxfordDictionary oxfordDictionary = null;
    private String mod;
    private int mDictionarySize;
    private TextToSpeech mTextToSpeech;
    private ImageView mPlayWord;
    private LinkedWordAdapter mLinkedWordAdapter;
    ///TextToSpeech tts;
    private static int speedOfTTS = 2;
    //private static final String SPEED_OF_TEXT_TO_SPEECH_PREF_ID = "SPEED_OF_TEXT_TO_SPEECH";
    private ChildEventListener childEventListener;
    private List<String> mLinkedUIDS = new ArrayList<>();
            //mTags = new ArrayList<>();

    private HashSet<CustomDictionaryModel> mLinkedItems = new HashSet<>();

    private List<CustomDictionaryModel> mItems = new ArrayList<>();
    private SparseBooleanArray mLinkedBooleanArray = new SparseBooleanArray();
    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;
    boolean[] checkedItems;
    ArrayList<String> mArrayStr;



    //Oxford
   // private int oxfordKnifeSwitch = 1;

    private TextView mLexicalCategory;
    private CardView mOxfordCardView;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        mDictionarySize = getDictionarySize();
        implementRecyclerViewAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

//                HashSet<CustomDictionaryModel> d = new HashSet<>();
//
//        CustomDictionaryModel m1 = new CustomDictionaryModel();
//        CustomDictionaryModel m2 = new CustomDictionaryModel();
//        m1.setNewWord("fsdfsd");
//        ArrayList<String> fgd = new ArrayList<>();
//        fgd.add("dsads");
//        m1.setTranslation(fgd);
//        m2.setNewWord("452452");
//        ArrayList<String> fgd2 = new ArrayList<>();
//        fgd.add("0410");
//        m1.setTranslation(fgd2);
//
//        d.add(m1);
//        d.add(m2);
    }


    @Override
    public void onResume() {
        super.onResume();
        //implUI();
        //setMods();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_word_detail, container, false);
        implementUI(view);
        implementRecyclerView(view);
        setMods();

        mTextToSpeech = new TextToSpeech(getActivity(), this);

        return view;
    }

    private void implementRecyclerView(View rootView) {
        mLinkedRV = (RecyclerView) rootView.findViewById(R.id.recycler_view_linked);
        mLinkedRV.setHasFixedSize(true);
        mLinkedRV.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void implementUI(View view) {
        mActionBarToolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        mAddLinked = (ImageView) view.findViewById(R.id.add_linked_word);
        mWordET = (EditText) view.findViewById(R.id.word_detail_edit_text);
        mTranslationET = (EditText) view.findViewById(R.id.translation_detail_edit_text);
        mDescriptionET = (EditText) view.findViewById(R.id.description_detail_edit_text);
        mDescriptionCV = (CardView) view.findViewById(R.id.description_card_view);
        mLearnedSwitcher = (Switch) view.findViewById(R.id.learned_switcher);
        mFavoriteSwitcher = (Switch) view.findViewById(R.id.favorite_switcher);
        mFabDetail = (FloatingActionButton) view.findViewById(R.id.fab_detail);
        mPlayWord = (ImageView)  view.findViewById(R.id.play_word);
        mPlayWord.setVisibility(View.GONE);
        mAddLinked.setOnClickListener(this);
        mFabDetail.setOnClickListener(this);


        //Oxford
        mLexicalCategory = (TextView) view.findViewById(R.id.oxford_lexical_category);
        mOxfordCardView = (CardView) view.findViewById(R.id.oxford_card_view);
        mOxfordCardView.setVisibility(view.GONE);



    }

    private void implementRecyclerViewAdapter() {

        mLinkedWordAdapter = new LinkedWordAdapter(mLinkedItems, this);
        mLinkedWordAdapter.clear();
        mLinkedRV.setAdapter(mLinkedWordAdapter);
//        if (getDictionaryName() != null && getCurrentUser() != null) {
//            implUI();
//        }
        mLinkedWordAdapter.notifyDataSetChanged();







    }

    private void updateData() {

        String path = "custom_dictionary/" + User.getCurrentUserUID() + "/dictionaries/" + getDictionary();
        childEventListener =  new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CustomDictionaryModel item = mFirebaseDictionaryWrapper.getCustomDictionaryWord(dataSnapshot);
                mItems.add(item);
                for (String uid: mLinkedUIDS) {
                    for (int i = 0; i < mItems.size(); i++) {
                        if (mItems.get(i).getUID().equals(uid)){
                            mLinkedItems.add(mItems.get(i));
                            mLinkedBooleanArray.put(i, true);
                          //  Log.i(TAG, "dsadasdasdasad: " + mItems.get(i).getNewWord());
                        }
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabaseReference.child(path).addChildEventListener(childEventListener);


    }

    private void implUI(){
         String path = "custom_dictionary/" + User.getCurrentUserUID() + "/dictionaries/" + getDictionary();
        FirebaseDatabase.getInstance().getReference().child(path).orderByChild("word").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CustomDictionaryModel item = mFirebaseDictionaryWrapper.getCustomDictionaryWord(dataSnapshot);
                if (item != null){

                    mItems.add(item);
                    //CollectionConverter.sort(mItems, new DictionaryCompare.WordCompare());
                }

                if (item.getUID().equals(getWordUID())){
                    getMainWordData(item);
                    getOxfordData(item);
                }

                getLinkedRVData();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getLinkedRVData(){
        for (String uid: mLinkedUIDS) {
            for (int i = 0; i < mItems.size(); i++) {
                if (mItems.get(i).getUID().equals(uid)){
                    mLinkedItems.add(mItems.get(i));
                    mLinkedWordAdapter.setItems(mLinkedItems);
                    mLinkedBooleanArray.put(i, true);
                }
            }
        }
        mLinkedWordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        mDatabaseReference.removeEventListener(childEventListener);
    }

    private void setMods () {
        switch (getMod()){
            case "DetailViewMod":
                mod = getMod();
                setDetailViewMod();

                implUI();

                //implementUIData(getWordUID());
                //updateData();
                break;
            case "EditMod":
                mod = getMod();
                setEditMod();
                implementUIData(getWordUID());
                updateData();
                break;
            case "AddMOD":
                mod = getMod();
                setAddMod();
                break;
        }
    }

    private void setDetailViewMod () {
        mWordET.setEnabled(false);
        mTranslationET.setEnabled(false);
        mDescriptionET.setEnabled(false);

        mWordET.setCursorVisible(false);
        mWordET.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);

        mTranslationET.setCursorVisible(false);
        mTranslationET.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);

        mDescriptionET.setCursorVisible(false);
        mDescriptionET.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);

        mPlayWord.setVisibility(View.VISIBLE);
       // mPlayTranslation.setVisibility(View.VISIBLE);

        mPlayWord.setOnClickListener(this);
    //    mWordET.setBackgroundColor(getResources().getColor(R.color.backgroud_edit_text));
    //    mTranslationET.setBackgroundColor(getResources().getColor(R.color.backgroud_edit_text));
    //    mDescriptionET.setBackgroundColor(getResources().getColor(R.color.backgroud_edit_text));

        mFabDetail.setImageResource(R.drawable.ic_edit);

        mFavoriteSwitcher.setOnCheckedChangeListener(this);
        mLearnedSwitcher.setOnCheckedChangeListener(this);
    }

    private void setEditMod () {
        mod = "EditMod";
        mWordET.setEnabled(true);
        mTranslationET.setEnabled(true);
        mDescriptionET.setEnabled(true);

        mWordET.setCursorVisible(true);
        mWordET.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        mTranslationET.setCursorVisible(true);
        mTranslationET.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        mDescriptionET.setCursorVisible(true);
        mDescriptionET.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);



        mFabDetail.setImageResource(R.drawable.ic_done);

     //   mWordET.setBackgroundColor(Color.TRANSPARENT);
     //   mTranslationET.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
     //   mDescriptionET.setBackgroundColor(getResources().getColor(R.color.superLightGray));

    }

    private void setAddMod () {
        mWordET.setText("");
        mTranslationET.setText("");
        mDescriptionET.setText("");
        mDescriptionET.setText("");

        mFabDetail.setImageResource(R.drawable.ic_done);

        mFavoriteSwitcher.setChecked(false);
        mLearnedSwitcher.setChecked(false);
    }

    private void getOxfordData(CustomDictionaryModel cdi){
        try {
            oxfordDictionary = new OxfordDictionary(cdi.getWord());
            mOxfordCardView.setVisibility(View.VISIBLE);
            List<OxfordDictionary.OxDictionary> oxDictionary = oxfordDictionary.getOxDictionary();
            String oxfordStr = "";
            for (OxfordDictionary.OxDictionary item:oxDictionary) {
                // Log.i(TAG, "onDataChange: " + item.toString());
                if (oxfordStr == "") oxfordStr = item.toString();
                else oxfordStr = oxfordStr + "\n\n" + item.toString();
            }
            mLexicalCategory.setText(oxfordStr);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getMainWordData(CustomDictionaryModel item){
        mWordET.setText(item.getWord());
        mTranslationET.setText(item.getTranslationString());
        mDescriptionET.setText(item.getDescription());
        if (item.getDescription() == null){
            if (getMod() != "EditMod")
                mDescriptionCV.setVisibility(View.GONE);
        } else{
            mDescriptionET.setText(item.getDescription());
        }

        mFavoriteSwitcher.setChecked(item.getFavorite());
        mLearnedSwitcher.setChecked(item.getStatus());

        if (item.getLinked() != null){
            mLinkedUIDS.addAll(item.getLinked());
        }
    }

    private void implementUIData (String uid) {
        String path = "custom_dictionary/" + User.getCurrentUserUID() + "/dictionaries/" + getDictionary();
        mFirebaseHelper = new FirebaseHelper<>(getActivity(), path, null, null);
        mFirebaseHelper.getItem(getWordUID(), new OnValueEventListener<CustomDictionaryModel>() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot, CustomDictionaryModel mItem) {
                mWordET.setText(mItem.getWord());
                mTranslationET.setText(mItem.getTranslationString());
                mDescriptionET.setText(mItem.getDescription());
                if (mItem.getDescription() == null){
                    if (getMod() != "EditMod")
                    mDescriptionCV.setVisibility(View.GONE);
                } else{
                    mDescriptionET.setText(mItem.getDescription());
                }

                mFavoriteSwitcher.setChecked(mItem.getFavorite());
                mLearnedSwitcher.setChecked(mItem.getStatus());
                if (mItem.getLinked() != null){
                    mLinkedUIDS.addAll(mItem.getLinked());
                    Log.i(TAG, "Linked UIDs: " + mLinkedUIDS);
                }

                OxfordDictionary oxfordDictionary = null;
                try {
                    oxfordDictionary = new OxfordDictionary(mItem.getWord());
                    mOxfordCardView.setVisibility(View.VISIBLE);
                    List<OxfordDictionary.OxDictionary> oxDictionary = oxfordDictionary.getOxDictionary();
                    String oxfordStr = "";
                    for (OxfordDictionary.OxDictionary item:oxDictionary) {
                       // Log.i(TAG, "onDataChange: " + item.toString());
                        if (oxfordStr == "") oxfordStr = item.toString();
                        else oxfordStr = oxfordStr + "\n\n" + item.toString();
                    }
                    mLexicalCategory.setText(oxfordStr);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError, CustomDictionaryModel mItem) {

            }

            @Override
            public CustomDictionaryModel getItem(DataSnapshot dataSnapshot) {
                return mFirebaseDictionaryWrapper.getCustomDictionaryWord(dataSnapshot);
            }
        });

    }

//    private void playText(String text, final Locale locale){
//         mTextToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
//             @Override
//             public void onInit(int status) {
//                 if(status != TextToSpeech.ERROR) {
//                     mTextToSpeech.setLanguage(locale);
//                 }
//             }
//         });
//        mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
//    }




    private String getWordUID(){
        return getArguments().getString("WordUID");
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

    private Boolean closeAfterAdded (){
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("closeAfterAdd", true);
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

        mDatabaseReference
                .child("custom_dictionary")
                .child(User.getCurrentUserUID())
                .child("dictionaries")
                .child(getDictionary())
                .child(getWordUID()).updateChildren(hashMap);
    }


    private void addNewWord () {

        if (getDictionary() == null){
            Toast.makeText(getActivity(), "Pleas reselect dictionary in setting of this dictionary", Toast.LENGTH_LONG).show();
        } else {
            String[] translationsList = (mTranslationET.getText().toString()).split(", ");
            ArrayList<String> translationList = new ArrayList<String>(Arrays.asList(translationsList));

            String key = mDatabaseReference
                    .child("custom_dictionary")
                    .child(User.getCurrentUserUID())
                    .child("dictionaries")
                    .child(getDictionary())
                    .push()
                    .getKey();

            Map<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("id", mDictionarySize);
            hashMap.put("word", mWordET.getText().toString());
            hashMap.put("translation", translationList);
            if (mDescriptionET.getText().length() != 0) {
                hashMap.put("description", mDescriptionET.getText().toString());
            }
            hashMap.put("status", mLearnedSwitcher.isChecked());
            hashMap.put("favorite", mFavoriteSwitcher.isChecked());


            if (mWordET.getText().length() != 0 && mTranslationET.getText().length() != 0) {
                mDatabaseReference
                        .child("custom_dictionary")
                        .child(User.getCurrentUserUID())
                        .child("dictionaries")
                        .child(getDictionary())
                        .child(key).setValue(hashMap);
                if (closeAfterAdded() == true) {
                    getActivity().onBackPressed();
                }
            } else {
                Toast.makeText(getActivity(), R.string.field_of_a_word_or_translation_is_empty, Toast.LENGTH_LONG).show();
            }

            String path = "custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/" + getDictionary() + "/size";
            mDatabaseReference.child(path).setValue(mDictionarySize);

            mDictionarySize++;
        }
    }
    



                                    private void replaceFragmentWithEditMod (Fragment newFragment){
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        Fragment fragment = fm.findFragmentById(R.id.detail_activity_container);
                                        if (fragment == null) {
                                            fragment = newFragment;
                                            fragment.setArguments(BaseDetailActivity.getBundle(getWordUID(), getDictionary(), "EditMod", getDictionarySize()));
                                            fm.beginTransaction().replace(R.id.detail_activity_container, fragment).commit();
                                        }
                                    }

                                    @Override
                                    public void onClick(View v) {
                                        switch (v.getId()){
                                            case R.id.play_word:
                                                setSpeed();
                                                speakOut(mWordET.getText().toString());
                                                break;
                                            case R.id.fab_detail:
                                                switch (getMod()){
                                                    case "DetailViewMod":
                                                        replaceFragmentWithEditMod(new DetailFragment());
                                                        setEditMod();
                                                        //mWordET.invalidate();
                                                        break;
                                                    case "EditMod":
                                                        changeWord();
                                                        getActivity().onBackPressed();
                                                        break;
                                                    case "AddMOD":
                                                        addNewWord();
                                                        break;
                                                }
                                                break;
                                            case R.id.add_linked_word:

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
                                                    mAlertDialogBuilder.setMultiChoiceItems(mStringOfDictionaries, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                                            mLinkedBooleanArray.put(which, isChecked);
                                                        }
                                                    });
                                                }


                                                mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        HashSet<CustomDictionaryModel> dict = new HashSet<>();

                                                        if(mLinkedUIDS != null)
                                                            mLinkedUIDS.clear();
                                                        for (int i = 0; i < mItems.size(); i++) {
                                                            if (mLinkedBooleanArray.get(i) == true)
                                                                mLinkedUIDS.add(mItems.get(i).getUID());
                                                        }
                                                        mDatabaseReference.child("custom_dictionary/" + User.getCurrentUserUID() + "/dictionaries/"
                                                                + getDictionary() + "/" + getWordUID() + "/linked").setValue(mLinkedUIDS);

                                                        if (mLinkedItems != null)
                                                            mLinkedItems.clear();
                                                        for (String uid: mLinkedUIDS) {
                                                            Log.i(TAG, "onClick: " + uid);
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
                                                    }
                                                });
                                                mAlertDialogBuilder.setNegativeButton(this.getString(R.string.cancel), null);
                                                mAlertDialog = mAlertDialogBuilder.create();
                                                mAlertDialog.show();
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
                Intent settings = new Intent(getActivity(), Preference.DetailViewPreference.class);
                startActivity(settings);
                break;
            case R.id.gg:
                Toast.makeText(getActivity(), "Hello World", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



//    @Override
//    public void onClick(View v) {
//        switch (getMod()){
//            case "DetailViewMod":
//                mod = "EditMod";
//                setEditMod();
//                mWordET.invalidate();
//                break;
//            case "EditMod":
//                changeWord();
//                getActivity().onBackPressed();
//                break;
//            case "AddMOD":
//                addNewWord();
//                break;
//        }
//    }

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
                break;
        }
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int result = mTextToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
             //   Log.e("TTS", getResources().getString(R.string.lang_is_not_supported));
                Toast.makeText(getActivity(), getResources().getString(R.string.lang_is_not_supported), Toast.LENGTH_LONG).show();
            } else {
                //speakOut(mWordET.getText().toString());
            }
        } else {
          //  Log.e("TTS", "Initialization Failed!");
        }

//        if(status == TextToSpeech.SUCCESS){
//            int result=tts.setLanguage(Locale.ENGLISH);
//            if(result==TextToSpeech.LANG_MISSING_DATA ||
//                    result==TextToSpeech.LANG_NOT_SUPPORTED){
//                Log.e("error", "This Language is not supported");
//            }
//            else{
//                ConvertTextToSpeech();
//            }
//        }
//        else
//            Log.e("error", "Initilization Failed!");
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
        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String displayTypeN = sharedPref.getString(SPEED_OF_TEXT_TO_SPEECH_PREF_ID, "2");
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
        //String text = editText.getText().toString();
        mTextToSpeech.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onItemClicked(View view, int position) {
        Intent intent = new Intent(getActivity(), BaseDetailActivity.class);
        //intent.putExtra("Word", mItems.get(position).getNewWord());

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

//    private void ConvertTextToSpeech() {
//        String text = mWordET.getText().toString();
//        if(text==null||"".equals(text))
//        {
//            //text = "Content not available";
//            //tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
//        }else
//            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
//    }

}



