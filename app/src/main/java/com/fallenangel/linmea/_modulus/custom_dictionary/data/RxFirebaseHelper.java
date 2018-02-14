/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 2/7/18 12:15 AM
 */

package com.fallenangel.linmea._modulus.custom_dictionary.data;

import com.fallenangel.linmea._modulus.grammar.db.OnFillListListener;
import com.fallenangel.linmea._modulus.non.data.BaseModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by NineB on 2/7/2018.
 */

public class RxFirebaseHelper<T extends BaseModel> {

    private DatabaseReference mDBR;
    private List<T> mItems;
    private String mSizePath, mPath;

    public RxFirebaseHelper(DatabaseReference dbr, List<T> items, String metaDataPath, String path) {
        this.mDBR = dbr;
        this.mItems = items;
        this.mSizePath = metaDataPath;
        this.mPath = path;
    }

    public RxFirebaseHelper(DatabaseReference dbr, List<T> items, String path) {
        this.mDBR = dbr;
        this.mItems = items;
        this.mPath = path;
    }

    private rx.Observable<Integer> sizeLoader(String path){
        PublishSubject<Integer> sizeSubject = PublishSubject.create();
        mDBR.child(path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        sizeSubject.onNext((int) (long) dataSnapshot.child("size").getValue());
                        sizeSubject.onCompleted();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        sizeSubject.onError(databaseError.toException());
                    }
                });
        return sizeSubject;
    }

    private rx.Observable<DataSnapshot> loader(String path, Integer count, Boolean removeListener){
        final int[] counter = {0};
        PublishSubject<DataSnapshot> grammarSubjectLoader = PublishSubject.create();
        Query queryGrammar = mDBR.child(path).orderByKey();
        queryGrammar
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        counter[0]++;
                        grammarSubjectLoader.onNext(dataSnapshot);
                        if (counter[0] >= count){
                            grammarSubjectLoader.onCompleted();
                            if (removeListener)
                                queryGrammar.removeEventListener(this);
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
                });
        return grammarSubjectLoader;
    }

    public void singleListFiller(OnFillListListener<T> onFillListListener){
        sizeLoader(mSizePath)
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        loader(mPath, integer, true)
                                .onBackpressureBuffer()
                                .doOnNext(new Action1<DataSnapshot>() {
                                    @Override
                                    public void call(DataSnapshot dataSnapshot) {
                                        T item = onFillListListener.itemWrapper(dataSnapshot);
                                        mItems.add(item);
                                        onFillListListener.doOnNext(item);
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnCompleted(new Action0() {
                                    @Override
                                    public void call() {
                                        onFillListListener.doOnCompleted();
                                    }
                                })
                                .subscribe();
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Observable<DataSnapshot> loadSingleItem(){
        PublishSubject<DataSnapshot> subject = PublishSubject.create();
        mDBR.child(mPath)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        subject.onNext(dataSnapshot);
                        subject.onCompleted();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        subject.onError(databaseError.toException());
                    }
                });
        return subject;
    }
}
