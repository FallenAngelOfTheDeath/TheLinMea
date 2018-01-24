package com.fallenangel.linmea._linmea.data.firebase;

import android.content.Context;

import com.fallenangel.linmea._linmea.adapter.AbstractRecyclerViewAdapter;
import com.fallenangel.linmea._linmea.interfaces.OnChildListener;
import com.fallenangel.linmea._linmea.interfaces.OnValueEventListener;
import com.fallenangel.linmea._linmea.model.BaseModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by NineB on 9/30/2017.
 */

public class FirebaseHelper<T extends BaseModel> {

    //Constants
    private static final String TAG = "FirebaseHelper";
    private static final int ORDER_DEFAULT = -1;
    public static final int ORDER_BY_KEY = 0;
    public static final int ORDER_BY_CHILD = 1;
    public static final int ORDER_DATA = 2;

    int orderBy = ORDER_DEFAULT;

    //CollectionConverter and data
    private List<T> mItems;
    private String mPath;

    //Listeners
    //private OnChildListener onChildListener;

    //Other
    private Context mContext;
    private AbstractRecyclerViewAdapter mAdapter;
    private DatabaseReference mDatabaseReference;
    private Query mQuery;

    private ChildEventListener childEventListener;

    String sortedString;

    //private String jsonListOfSortedCustomerId;


    public FirebaseHelper (Context context, String path, List<T> items, AbstractRecyclerViewAdapter adapter) {
        this.mContext = context;
        this.mPath = path;
        this.mItems = items;
        this.mAdapter = adapter;
        this.sortedString = "";
        this.mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public List<T> getItemsList(int orderBy, final OnChildListener<T> onChildListener){
        final int[] sizeCounter = {0};
        switch (orderBy){
            case ORDER_DEFAULT:
                mQuery = mDatabaseReference.child(mPath);
                break;
            case ORDER_BY_KEY:
                mQuery = mDatabaseReference.child(mPath).orderByKey();
                break;
            case ORDER_BY_CHILD:
                mQuery = mDatabaseReference.child(mPath).orderByKey();
                break;
            case ORDER_DATA:
                mQuery = mDatabaseReference.child(mPath)
                        //.orderByChild("data")
                ;
                break;
            default:
                mQuery = mDatabaseReference.child(mPath);
                break;
        }


            mQuery.addChildEventListener(childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                        rx.Observable
//                                .just(onChildListener.getItem(dataSnapshot))
//                                .map(contactItem -> mItems.add(contactItem))
//                                .observeOn(Schedulers.io())
//                        .subscribeOn(AndroidSchedulers.mainThread());
                                //.subscribe(onNext -> mItems.add(item));
                                //.doOnCompleted(mItems.add())


                     //   Log.i(TAG, "onChildAdded: " + Thread.currentThread().getName());
                        T item = onChildListener.getItem(dataSnapshot);
                        sortedString = "";
                        sortedString = onChildListener.getSortedString(); ///<- remove from here
                        if (sortedString == null) sortedString = "";
                     //   Log.i(TAG, "onChildAdded: " + sortedString);
                        sizeCounter[0]++;
                        mAdapter.setNotFilteredSize(sizeCounter[0]);

                                                    if (item != null)
                                                        mItems.add(item);

                        //mItems = getSort();
                        mAdapter.setItems(mItems, sortedString);
                    //    Log.i(TAG, "onChildAdded: size" + mAdapter.getItemCount() + " : " + mItems.size() + " : " + mItems);
                        //mAdapter.setSortedItems(mItems, sortedString);
                        //mAdapter.orderBy(getActivity(), mItems);
                        //+sorting & filters
                        onChildListener.onChildAdded(dataSnapshot, s, -1);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        T item = onChildListener.getItem(dataSnapshot);
                        int index = getItemIndex(item, mItems);
                        sizeCounter[0]++;
                        mAdapter.setNotFilteredSize(sizeCounter[0]);
                        //mItems.set(index, item);
                        if (item != null)
                            mItems.set(index, item);

                       // mItems = getSort(mItems);
                        mAdapter.setItems(mItems, "");
                        mAdapter.notifyItemRangeChanged(0, mItems.size());
                        mAdapter.notifyDataSetChanged();
                        //+sorting & filters
                        onChildListener.onChildChanged(dataSnapshot, s, item.getId());
                       // onChildListener.onChildChanged(dataSnapshot, s, index);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        T item = onChildListener.getItem(dataSnapshot);
                        for (int i = 0; i < mItems.size(); i++) {
                            if (mItems.get(i).getUID().equals(item.getUID())){
                                sizeCounter[0]--;
                                mAdapter.setNotFilteredSize(sizeCounter[0]);
                                mItems.remove(i);
                                break;
                            }
                        }

                        onChildListener.onChildRemoved(dataSnapshot,  -1);
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
        mQuery.keepSynced(true);

        return mItems;
    }

//    public Query getDatabaseReference(){
//        return mQuery;
//    }
//
//
//    public ChildEventListener getChildEventListener(){
//        return childEventListener;
//    }
//
//    private List<T> getSort ()  {
//
//        List<T> sortedList = new ArrayList<>();
//        if (!sortedString.isEmpty()){
//            Gson gson = new Gson();
//            List<String> listOfSortedUIDS = gson.fromJson(sortedString, new TypeToken<List<String>>(){}.getType());
//
//            Log.i(TAG, "getSort listOfSortedUIDS: " + listOfSortedUIDS);
//            Log.i(TAG, "getSort: mItems" + mItems);
//            if (listOfSortedUIDS != null && listOfSortedUIDS.size() > 0){
//                for (int i = 0; i < listOfSortedUIDS.size(); i++) {
//                    Log.i(TAG, "getSort listOfSortedUIDS 1 by 1: " + listOfSortedUIDS.get(i));
//
//                    for (int j = 0; j < mItems.size(); j++) {
//                        Log.i(TAG, "getSort: mItems 1 by 1" + mItems.get(j).getUID());
//                        if (listOfSortedUIDS.get(i).equals(mItems.get(j).getUID())){
//                            sortedList.add(mItems.get(j));
//                            //mItems.remove(j);
//                        }
//                    }
//                }
//
//
//            }
//            Log.i(TAG, "getSort sortedList: " + sortedList);
//
//            for (int i = 0; i < sortedList.size(); i++) {
//                Log.i(TAG, "getSort sortedList 1 by 1: " + i + " : " + sortedList.get(i).getUID());
//            }
//
////            if (mItems.size() > 0){
////                sortedList = mItems;
////            }
//            mItems.clear();
//            mItems = sortedList;
//            Log.i(TAG, "getSort: " + mItems.size() + " SIZE " + sortedList.size());
//            return mItems;
//        } else {
//            return  mItems;
//        }
//
////        List<T> sortedList = new ArrayList<>();
////        if (!sortedString.isEmpty()){
////            Gson gson = new Gson();
////            List<String> listOfSortedUIDS = gson.fromJson(sortedString, new TypeToken<List<String>>(){}.getType());
////            if (listOfSortedUIDS != null && listOfSortedUIDS.size() > 0){
////                Log.i(TAG, "getSort: ghjghjgh " + listOfSortedUIDS);
////                //for (String id: listOfSortedUIDS) {
////                for (int id = 0; id < listOfSortedUIDS.size(); id++) {
////                    Log.d(TAG, "getSort id: " + id);
////                    for (int i = 0; i < mItems.size(); i++) {
////                        Log.d(TAG, "getSort i: " + i);
////
////                        Log.i(TAG, "getSort mItems: "+ i + " : "  + mItems.get(i).getUID());
////                        if(mItems.get(i).getUID().equals(listOfSortedUIDS.get(id))){
////                            //sortedList.add(mItems.get(i));
////                            sortedList.add(id, mItems.get(i));
////                            mItems.remove(i);
////                            //Log.i(TAG, "getSort sortedList: " + id + " : "  + sortedList.get(id).getUID());
////                            break;
////                        }
////                    }
////                }
////
////                for (int i = 0; i < sortedList.size(); i++) {
////                    Log.i(TAG, "getSort sortedList: " + sortedList.get(i).getUID());
////                }
////
////
//////                for (String id: listOfSortedUIDS){
//////                    for (T item: mItems){
//////                        if (item.getUID().equals(id)){
//////                            sortedList.add(item);
//////                            mItems.remove(item);
//////                            break;
//////                        }
//////                    }
//////                }
////
////
////            }
////            if (mItems.size() > 0){
////                sortedList = mItems;
////           }
//////            Log.i(TAG, "getSort sortedList0: " + sortedList.get(0).getUID());
//////            Log.i(TAG, "getSort sortedList1: " + sortedList.get(1).getUID());
//////            Log.i(TAG, "getSort sortedList2: " + sortedList.get(2).getUID());
//////            Log.i(TAG, "getSort sortedList3: " + sortedList.get(3).getUID());
//////            Log.i(TAG, "getSort sortedList4: " + sortedList.get(4).getUID());
//////            Log.i(TAG, "getSort sortedList5: " + sortedList.get(5).getUID());
//////            Log.i(TAG, "getSort sortedList6: " + sortedList.get(6).getUID());
////            return sortedList;
////        }else {
////            return mItems;
////        }
//    }


    public void getItem (String UID, final OnValueEventListener<T> onValueEventListener){

        mDatabaseReference
                .child(mPath)
                .child(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        T item = onValueEventListener.getItem(dataSnapshot);
                    //    Log.i(TAG, "onDataChange: " + item);
                        onValueEventListener.onDataChange(dataSnapshot, item);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        onValueEventListener.onCancelled(databaseError, null);
                    }
                });
    }

    private int getItemIndex (T model, List<T> items){
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getUID().equals(model.getUID())) {
                return i;
            }
        }
        return index;
    }

}
