/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.non.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;

import com.fallenangel.linmea._modulus.non.data.BaseModel;
import com.fallenangel.linmea._modulus.non.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.non.interfaces.OnStartDragListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by NineB on 9/18/2017.
 */

public abstract class AbstractRecyclerViewAdapter<T extends BaseModel, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements OnItemTouchHelper {

    private static final String TAG = "AbstractRVAdapter";
    protected List<T> mItems;
    protected Context mContext;
    protected OnRecyclerViewClickListener mClickListener;
    protected OnItemTouchHelper mOnItemTouchHelper;
    protected final OnStartDragListener mDragStartListener;
    protected SparseBooleanArray mSelectedItems;
    protected LayoutInflater mInflater;
    protected Map<String, Object> mMovedItems;
    protected int mNotFilteredSize = 0;
    protected List<String> mSortedString;

    protected AbstractRecyclerViewAdapter(Context context, List<T> items, OnRecyclerViewClickListener clickListener, OnStartDragListener dragStartListener, OnItemTouchHelper onItemTouchHelper){
        this.mContext = context;
        this.mItems = items;
        this.mClickListener = clickListener;
        this.mDragStartListener = dragStartListener;
        this.mOnItemTouchHelper = onItemTouchHelper;
        mMovedItems = new HashMap<String, Object>();
        mSelectedItems = new SparseBooleanArray();
        mInflater = LayoutInflater.from(context);
        mSortedString = new ArrayList<>();
    }

    ///____________________________________VIEW HOLDER_____________________________________
    @Override
    public void onBindViewHolder(VH holder, int position){
        final T item = getItem(position);
        bindItemData(holder, item, position);
    }

    protected abstract void bindItemData(VH viewHolder, T data, int position);


    ///_______________________________________UTILS_______________________________________
       public void addData(List<T> newItems) {
        if (newItems != null) {
            mItems.addAll(newItems);
            notifyDataSetChanged();
        }
    }

    public void setItems(List<T> items) {
        mItems = items;
    }

    public void setItems(List<T> items, String sortedString) {
                        Log.i("ttttr", "setItems: " + sortedString);
                           if (sortedString.length() != 0) {
                               Gson gson = new Gson();
                               Set<T> tmprl = new HashSet<>();
                               List<T> newSortedList = new ArrayList<>();
                              Observable
                                       .just(sortedString)
                                       .map(new Func1<String, List<String>>() {
                                           @Override
                                           public List<String> call(String s) {
                                               return gson.fromJson(s, new TypeToken<List<String>>() {}.getType());
                                           }
                                       })
                                       .flatMap(Observable::from)
                                       .doOnNext(new Action1<String>() {
                                           @Override
                                           public void call(String s) {
                                               Observable
                                                       .from(items)
                                                       .filter(new Func1<T, Boolean>() {
                                                           @Override
                                                           public Boolean call(T t) {
                                                               if (s.equals(t.getUID())) {
                                                                   return true;
                                                               } else {
                                                                   return false;
                                                               }
                                                           }
                                                       })
                                                       .doOnNext(new Action1<T>() {
                                                           @Override
                                                           public void call(T t) {
                                                               newSortedList.add(t);

                                                           }
                                                       })
                                                       .doOnCompleted(new Action0() {
                                                           @Override
                                                           public void call() {

                                                           }
                                                       })
                                                       .subscribe();
                                           }
                                       })
                                      .subscribeOn(Schedulers.computation())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .doOnCompleted(new Action0() {
                                            @Override
                                            public void call() {
                                                Observable.from(newSortedList)
                                                        .toList()
                                                        .doOnNext(new Action1<List<T>>() {
                                                            @Override
                                                            public void call(List<T> ts) {
                                                                Observable
                                                                        .from(items)
                                                                        .filter(new Func1<T, Boolean>() {
                                                                            @Override
                                                                            public Boolean call(T t2) {
                                                                                if (ts.contains(t2))
                                                                                    return false;
                                                                                else
                                                                                    return true;
                                                                            }
                                                                        })
                                                                        .doOnNext(new Action1<T>() {
                                                                            @Override
                                                                            public void call(T t2) {
                                                                                tmprl.add(t2);
                                                                            }
                                                                        })
                                                                        .subscribe();
                                                            }
                                                        })
                                                        .subscribeOn(Schedulers.computation())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .doOnCompleted(new Action0() {
                                                            @Override
                                                            public void call() {
                                                                mItems.clear();
                                                                mItems.addAll(newSortedList);
                                                                mItems.addAll(tmprl);
                                                                notifyDataSetChanged();

                                                            }
                                                        })
                                                        .subscribe();
                                            }
                                        }).subscribe();

                           } else {
                               mItems = items;
                           }
    }


    public T getItem(int position) {
        position = Math.max(0, position);
        return mItems.get(position);
    }

    public void addItem(T i){
           mItems.add(i);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public int getNotFilteredSize() {
        return mNotFilteredSize;
    }

    public void setNotFilteredSize(int notFilteredSize) {
        mNotFilteredSize = notFilteredSize;
    }

    public T getItems (int position){
        final T itemList = mItems.get(position);
        return itemList;
    }

    public List<T> getItemss(){
        return mItems;
    }

    public void setItem(int index, T item){
        mItems.set(index, item);
    }

    public void clear (){
        mItems.clear();
    }


    ////______________________________________ACTIONS_______________________________________
    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mItems.size());
    }

    public void setFilter(List<T> item) {
        mItems = new ArrayList<T>();
        mItems.addAll(item);
        notifyDataSetChanged();
    }

    public void resort (Boolean reverse){
        Collections.sort(mItems, new IdCompare());
        if (reverse){
            Collections.reverse(mItems);
        }
    }

    public Map<String, Object> updateId(){
        Map<String, Object> ids = new HashMap<>();
        for (int i = 0; i < mItems.size(); i++) {
            //int now = i; int next = i+1;
            if (i != mItems.get(i).getId()){
                ids.put(mItems.get(i).getUID()+"/id", i);
            }
        }
        return ids;
    }

    public static class IdCompare implements Comparator<BaseModel> {
        @Override
        public int compare(BaseModel o1, BaseModel o2) {
            return ((Integer) o1.getId()).compareTo(o2.getId());
        }
    }

    public void restoreItem(T item, int position) {
        mItems.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    ////__________________________________MULTI_SELECTION___________________________________

    //Put or delete selected position into SparseBooleanArray
    public void selectItem(int position, boolean value) {
        if (value) {
            mSelectedItems.put(position, value);
            notifyItemChanged(position);
        } else {
            mSelectedItems.delete(position);
            notifyItemChanged(position);
        }
    }

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectItem(position, !mSelectedItems.get(position));
    }

    //Remove selected selections
    public void removeSelection() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItems.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItems;
    }

    public Boolean getSlectionStatus(){
        if (getSelectedCount()<1){
            return true;
        }
        return false;
    }

    public void removeSelectedItems() {
        SparseBooleanArray selected = getSelectedIds();
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                mItems.remove(selected.keyAt(i));
                notifyDataSetChanged();
            }
        }
    }


    ////____________________________________DRAG & DROP/SWIPE_______________________________________
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
    }

    public void clearSortedString() {
        mSortedString.clear();
    }

    @Override
    public boolean onMove(int fromPosition, int toPosition) {
        moveItem(fromPosition, toPosition);
        mSortedString.clear();
        for (T item: mItems){
            mSortedString.add(item.getUID());
            //Log.i("testTAG", "===========================================================================================================");
            //Log.i("testTAG", "onMove sorted string: " + item.getUID());
            //Log.i("testTAG", "===========================================================================================================");
        }

        if (mSelectedItems.size()>0){
            mSelectedItems.delete(fromPosition);
            mSelectedItems.put(toPosition, !mSelectedItems.get(toPosition));
        }

        mMovedItems.put(mItems.get(toPosition).getUID() + "/id", toPosition);
        mMovedItems.put(mItems.get(fromPosition).getUID() + "/id", fromPosition);
        return true;
    }

    protected void moveItem(int fromPosition, int toPosition){
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    private void moveSelectedItems(int fromPosition, int toPosition){
        for (int i = 0; i < mSelectedItems.size(); i++) {
            moveItem(mItems.get(i).getId(), toPosition);
            if (mItems.get(i).getId() < toPosition) {
                toPosition = toPosition + 1;
            } else {
                toPosition = toPosition - 1;
            }
        }
    }

    public Map<String, Object> getMovedItems (){
        return mMovedItems;
    }

    public void clearMoved(){
        mMovedItems.clear();
    }

    public int getMovedSize (){
        return mMovedItems.size();
    }

    @Override
    public void onItemMoveComplete(RecyclerView.ViewHolder viewHolder) {

    }

    public List<String> getSortedString() {
        return mSortedString;
    }
}
