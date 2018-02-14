/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.grammar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.grammar.model.GrammarsList;
import com.fallenangel.linmea._modulus.grammar.model.UserDataGrammar;
import com.fallenangel.linmea._modulus.non.adapter.SimpleAbstractAdapter;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.prferences.DictionaryCustomizer;

import java.util.List;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by NineB on 1/17/2018.
 */

public class GrammarsListAdapter extends SimpleAbstractAdapter<GrammarsList, GrammarsListAdapter.ViewHolder> {

    private List<UserDataGrammar> mUserData;
    private int mLearnedColor, mOptionsMenu;
    private DictionaryCustomizer mDictionaryCustomizer;
    private Context mContext;
    private String TAG = "GrammarsListAdapter";

    public GrammarsListAdapter(Context context, List<GrammarsList> itemList, List<UserDataGrammar> userData, OnRecyclerViewClickListener clickListener, DictionaryCustomizer dictionaryCustomizer) {
        super(itemList, clickListener);
        this.mContext = context;
        this.mUserData = userData;
        this.mDictionaryCustomizer = dictionaryCustomizer;
        this.mOptionsMenu = mDictionaryCustomizer.getOptionsMenu();
        this.mLearnedColor = mDictionaryCustomizer.getLearnedColor();
    }

    @Override
    protected void bindItemData(ViewHolder viewHolder, GrammarsList data, int position) {
        viewHolder.nameCategory.setText(data.getGrammarName());
        viewHolder.shortName.setText(shortName(data.getGrammarName()));
        try {
            reDrawItems(viewHolder.cardView, viewHolder.fav, viewHolder.shortName, data, position);
        } catch (Exception e){
            throw e;
        }
    }

    private void reDrawItems(CardView cardView, ImageView fav, TextView shortName, GrammarsList data, int position) throws IndexOutOfBoundsException{
        for (UserDataGrammar item : mUserData) {
                if (item.getGrammarName().equals(data.getGrammarName())) {
                    if (!mUserData.isEmpty()) {
                        Boolean learned = null;
                        Boolean favorite = null;
                        learned = item.getLearned();
                        favorite = item.getFavorite();
                        if (favorite == null) favorite = false;
                        if (learned == null) learned = false;
                        if (learned) {
                            cardView.setBackgroundColor(learned ? mLearnedColor : Color.WHITE);
                        } else {
                            cardView.setBackgroundColor(learned ? mLearnedColor : Color.WHITE);
                        }
                        if (favorite) {
                            fav.setVisibility(favorite ? View.VISIBLE : View.GONE);
                            shortName.setTextColor(favorite ? Color.WHITE : Color.BLACK);
                        } else {
                            fav.setVisibility(favorite ? View.VISIBLE : View.GONE);
                            shortName.setTextColor(favorite ? Color.WHITE : Color.BLACK);
                        }
                    }

                }
        }
    }

    private String shortName(String originStr){
        String[] splitedStr = splitString(originStr);
        if (splitedStr.length > 1){
            return getShortName(splitedStr);
        } else {
            return shortenUpToTwoCharacters(originStr);
        }
    }

    private String[] splitString(String originStr){
        return originStr.split(" ");
    }

    private String shortenUpToTwoCharacters(String originStr){
        return originStr.subSequence(0,2).toString().toUpperCase();
    }

    private String getShortName(String[] arrayStr){
        String shortName = "";
        for (int i = 0; i < 2; i++) {
            shortName = shortName + arrayStr[i].charAt(0);
        }
        return shortName.toUpperCase();
    }

    @Override
    public GrammarsList getItem(int position) {
        return mItemsList.get(position);
    }

    public UserDataGrammar getItemData(String gName) {
        for (GrammarsList i:mItemsList) {
            for (UserDataGrammar u:mUserData) {
                if (i.getGrammarName().equals(u.getGrammarName()))
                    return u;
            }
        }
            return null;
    }

    public List<UserDataGrammar> getItemsData() {
        return mUserData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grammar_category, parent, false);
        return new ViewHolder(itemView, mClickListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private CardView cardView;
        private TextView shortName, nameCategory;
        private OnRecyclerViewClickListener clickListener;
        private ImageView option, fav;

        public ViewHolder(View itemView, OnRecyclerViewClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;

            option = (ImageView) itemView.findViewById(R.id.options_menu);
            fav = (ImageView) itemView.findViewById(R.id.fav);

            cardView = (CardView) itemView.findViewById(R.id.card_view_grammar_category);
            shortName = (TextView) itemView.findViewById(R.id.short_name);
            nameCategory = (TextView) itemView.findViewById(R.id.name_category);

            switch (mOptionsMenu){
                case 0:
                    option.setVisibility(View.VISIBLE);
                    option.setOnClickListener(this);
                    option.setClickable(true);
                    break;
                case 1:
                    option.setVisibility(View.GONE);
                    option.setClickable(false);
                    break;
            }

            this.cardView.setOnClickListener(this);
            this.cardView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.options_menu:
                    if (clickListener != null) {
                        clickListener.onOptionsClicked(view, getAdapterPosition ());
                    }
                    break;
                case R.id.card_view_grammar_category:
                    if (clickListener != null) {
                        clickListener.onItemClicked(view, getAdapterPosition ());
                    }
                    break;
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (clickListener != null) {
                return clickListener.onItemLongClicked(view, getAdapterPosition ());
            }
            return false;
        }

    }

    public void changeLearned(int pos){
        String to = User.getCurrentUserUID() + "/" + mItemsList.get(pos).getGrammarCategory() + "/" + mItemsList.get(pos).getGrammarName();
        final Boolean[] scl = {false};
        Observable
                .from(mUserData)
                .filter(new Func1<UserDataGrammar, Boolean>() {
                    @Override
                    public Boolean call(UserDataGrammar data) {
                        if (data.getGrammarName().equals(mItemsList.get(pos).getGrammarName())){
                            scl[0] = true;
                            return true;
                        } else {
                            return false;
                        }
                    }
                })
                .doOnNext(new Action1<UserDataGrammar>() {
                    @Override
                    public void call(UserDataGrammar data) {
                        Boolean bool;
                        if (data.getLearned() == null)
                            bool = true;
                        else
                            bool = data.getLearned() ? false : true;
                        data.setLearned(bool);
                        UserDataGrammar.changeLearned(to, bool);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        if (scl[0] == false) {
                            UserDataGrammar udg = new UserDataGrammar();
                            udg.setGrammarName(mItemsList.get(pos).getGrammarName());
                            udg.setCategory(mItemsList.get(pos).getGrammarCategory());
                            udg.setFavorite(false);
                            udg.setLearned(true);
                            UserDataGrammar.changeLearned(to, true);
                        }
                        notifyDataSetChanged();
                    }
                })
        .subscribe();
    }

    public void changeFavorite(int pos){
        String to = User.getCurrentUserUID() + "/" + mItemsList.get(pos).getGrammarCategory() + "/" + mItemsList.get(pos).getGrammarName();
        final Boolean[] scl = {false};
        rx.Observable
                .from(mUserData)
                .filter(new Func1<UserDataGrammar, Boolean>() {
                    @Override
                    public Boolean call(UserDataGrammar data) {
                        if (data.getGrammarName().equals(mItemsList.get(pos).getGrammarName())){
                            scl[0] = true;
                            return true;
                        } else {
                            return false;
                        }
                    }
                })
                .doOnNext(new Action1<UserDataGrammar>() {
                    @Override
                    public void call(UserDataGrammar data) {
                        Boolean bool;
                        if (data.getFavorite() == null)
                            bool = true;
                        else
                            bool = data.getFavorite() ? false : true;
                        data.setFavorite(bool);
                        UserDataGrammar.changeFavorite(to, bool);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        if (scl[0] == false) {
                            UserDataGrammar.changeFavorite(to, true);
                        }
                        notifyDataSetChanged();
                    }
                })
                .subscribe();
    }
}
