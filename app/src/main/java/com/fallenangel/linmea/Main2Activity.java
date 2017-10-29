package com.fallenangel.linmea;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fallenangel.linmea.database.FirebaseWrapper;
import com.fallenangel.linmea.model.CustomDictionaryModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

//    String userUid, String dictionary,
//    final List<CustomDictionaryModel> mItems;
//    final OnChildListener onChildListener;
private List<CustomDictionaryModel> mItems = new ArrayList<CustomDictionaryModel>();
    private FirebaseWrapper mFirebaseWrapper = new FirebaseWrapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);



        DatabaseReference bd = FirebaseDatabase.getInstance().getReference().child("custom_dictionary")
                .child("musd33qW0sdE7paVwLLRvOUB7Q43")
                .child("dictionaries")
                .child("Dictionary for vasya");

      //  bd.child("-KwaCt6_H3qIn2yYeCn8").removeValue();

                bd.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        CustomDictionaryModel item = mFirebaseWrapper.getCustomDictionaryWord(dataSnapshot);
                        mItems.add(item);
                        Log.i("0000", "onChildAdded: " + item.getUID());
                        //onChildListener.onChildAdded(dataSnapshot, s, mItems);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                        CustomDictionaryModel item = mFirebaseWrapper.getCustomDictionaryWord(dataSnapshot);
//                        int index = getItemIndex(item, mItems);
//                        mItems.set(index, item);
//                        onChildListener.onChildChanged(dataSnapshot, s, mItems);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        CustomDictionaryModel item = mFirebaseWrapper.getCustomDictionaryWord(dataSnapshot);
                        int index = getItemIndex(item, mItems);
                        Log.i("0000", "onChildRemoved: " + item.getUID());
                        mItems.remove(index);
//                        onChildListener.onChildRemoved(dataSnapshot, mItems);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                       // onChildListener.onChildMoved(dataSnapshot, s, mItems);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                      //  onChildListener.onCancelled(databaseError);
                    }
                });




    }



    private int getItemIndex (CustomDictionaryModel model, List<CustomDictionaryModel> items){
        int index = -1;

        for (int i = 0; i < items.size(); i++) {
            Log.i("0000", "getItemIndex: for  " + i);
            if (items.get(i).getUID().equals(model.getUID())){
                Log.i("0000", "getItemIndex: if  " + i);
                index = i;
                break;
            }
        }
        return index;
    }
}
