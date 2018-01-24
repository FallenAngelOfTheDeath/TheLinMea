package com.fallenangel.linmea.linmea;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fallenangel.linmea._linmea.data.firebase.FirebaseDictionaryWrapper;
import com.fallenangel.linmea._linmea.interfaces.OnChildListener;
import com.fallenangel.linmea._linmea.interfaces.OnValueEventListener;
import com.fallenangel.linmea._linmea.data.collection.DictionaryCompare;
import com.fallenangel.linmea._linmea.util.SharedPreferencesUtils;
import com.fallenangel.linmea._linmea.model.CustomDictionaryModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by NineB on 10/8/2017.
 */

public class Singleton {

    private static Singleton sSingleton;
    private Context mContext;
    private String mMode;
    private DatabaseReference mDatabaseReference;

    private FirebaseDictionaryWrapper mFirebaseDictionaryWrapper = new FirebaseDictionaryWrapper();

    public static Singleton getInstance(Context context) {
        if (sSingleton == null){
            sSingleton = new Singleton(context);
        }
        return sSingleton;
    }

    private Singleton(Context context) {
        mContext = context.getApplicationContext();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // mDatabase = new LinmeaDataBaseHelper(mContext).getWritableDatabase();
    }

  //  final List<CustomDictionaryModel> mItems = new ArrayList<CustomDictionaryModel>();

    public List<CustomDictionaryModel> getItemsList(String userUid, String dictionary, final List<CustomDictionaryModel> mItems, final OnChildListener onChildListener){

        mDatabaseReference.child("custom_dictionary")
                .child(userUid)
                .child("dictionaries")
                .child(dictionary)
                .orderByChild("id")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        CustomDictionaryModel item = mFirebaseDictionaryWrapper.getCustomDictionaryWord(dataSnapshot);
                        mItems.add(item);
                        filter(mContext, mItems);
                        filterFavorite(mContext, mItems);
                        //orderBy(mContext, mItems);
                        onChildListener.onChildAdded(dataSnapshot, s, -1);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        CustomDictionaryModel item = mFirebaseDictionaryWrapper.getCustomDictionaryWord(dataSnapshot);
                        int index = getItemIndex(item, mItems);
                        mItems.set(index, item);
                        filter(mContext, mItems);
                        filterFavorite(mContext, mItems);
                        //orderBy(mContext, mItems);
                        onChildListener.onChildChanged(dataSnapshot, s, index);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                //        int index = -1;
                        CustomDictionaryModel item = mFirebaseDictionaryWrapper.getCustomDictionaryWord(dataSnapshot);
                        for (int i = 0; i < mItems.size(); i++) {
                            if (mItems.get(i).getUID().equals(item.getUID())){
                               // mItems.remove(i);
                                break;
                            }
                        }



//                        CustomDictionaryModel item = mFirebaseWrapper.getCustomDictionaryWord(dataSnapshot);
//                        int index = getItemIndex(item, mItems);
//// //                       Log.i("0000", "Singleton onChildRemoved: " + index + "                                       " + item.getUID());
//                        mItems.remove(index);
                        filter(mContext, mItems);
                        filterFavorite(mContext, mItems);
                        //orderBy(mContext, mItems);
                        onChildListener.onChildRemoved(dataSnapshot, -1);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        onChildListener.onChildMoved(dataSnapshot, s, -1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        onChildListener.onCancelled(databaseError);
                    }
                });

        return mItems;
    }

    private int getItemIndex (CustomDictionaryModel model, List<CustomDictionaryModel> items){
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getUID().equals(model.getUID())) {
                return i;
            }
        }
        return index;
    }

    private void orderBy (Context context, List<CustomDictionaryModel> mItems) {

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        final String orderBy = SP.getString("orderBy", "0");
        int orderByN = Integer.parseInt(orderBy);


        switch (orderByN) {

            case 0:
                break;
            case 1:
                Collections.reverse(mItems);
                break;
            case 2:
                Collections.sort(mItems, new DictionaryCompare.WordCompare());
                break;
            case 3:
                Collections.sort(mItems, new DictionaryCompare.WordCompare());
                Collections.reverse(mItems);
                break;
            case 4:
                Collections.sort(mItems, new DictionaryCompare.TranslationCompare());
                break;
            case 5:
                Collections.sort(mItems, new DictionaryCompare.TranslationCompare());
                Collections.reverse(mItems);
                break;
            default:
                break;
        }
    }

    private void filter (Context context, List<CustomDictionaryModel> mItems) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        final String filter = SP.getString("filter", "0");
        int filterN = Integer.parseInt(filter);

        for(CustomDictionaryModel item : mItems){

            if(item.getStatus() != null ){

                switch (filterN){

                    case 0:
                        break;
                    case 1:
                        if (item.getStatus().equals(false))
                            mItems.remove(item);
                        break;
                    case 2:
                        if (item.getStatus().equals(true))
                            mItems.remove(item);
                        break;
                }
            }
        }
    }

    private void filterFavorite (Context context, List<CustomDictionaryModel> mItems) {
        mMode = SharedPreferencesUtils.getFromSharedPreferences(context, "customDict", "dictionaryMode");
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        final String filter = SP.getString("filterFavorite", "0");
        int filterN = Integer.parseInt(filter);

        for(CustomDictionaryModel item : mItems){
                if(item.getFavorite() != null ){

                    switch (filterN){

                        case 0:
                            break;
                        case 1:
                            if (item.getFavorite().equals(false))
                                mItems.remove(item);
                            break;
                        case 2:
                            if (item.getFavorite().equals(true))
                                mItems.remove(item);
                            break;
                    }
                    if (mMode != null && mMode.equals("favOnly")){
                        if (item.getFavorite().equals(false)){
                            mItems.remove(item);
                        }
                    }
                }
        }
    }

    public CustomDictionaryModel getItem (String userUid, String dictionary, String wordUid, final OnValueEventListener onValueEventListener){
        final CustomDictionaryModel[] item = {null};
        mDatabaseReference.child("custom_dictionary")
                .child(userUid)
                .child("dictionaries")
                .child(dictionary)
                .child(wordUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                item[0] = mFirebaseDictionaryWrapper.getCustomDictionaryWord(dataSnapshot);
                onValueEventListener.onDataChange(dataSnapshot, item[0]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onValueEventListener.onCancelled(databaseError, null);
            }
        });

//                .addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                item[0] = mFirebaseWrapper.getCustomDictionaryWord(dataSnapshot);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        return item[0];



//        CustomDictionaryModel newItem = null;
//
//        for(CustomDictionaryModel items : mItems){
//            if(items.getNewWord() != null && items.getUID().contains(wordUid)){
//                newItem.setUID(items.getUID());
//                newItem.setNewWord(items.getNewWord());
//                newItem.setTranslationString(items.getTranslationString());
//                newItem.setTranslation(items.getTranslation());
//                newItem.setDescription(items.getDescription());
//                newItem.setStatus(items.getStatus());
//                newItem.setFavorite(items.getFavorite());
//            }
//
//        }
//
//        return newItem;
    }

}
