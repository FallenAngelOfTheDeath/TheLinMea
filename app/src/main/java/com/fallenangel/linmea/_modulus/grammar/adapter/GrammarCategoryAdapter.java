/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.grammar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.grammar.model.GrammarCategory;
import com.fallenangel.linmea._modulus.non.adapter.SimpleAbstractAdapter;

import java.util.List;

/**
 * Created by NineB on 1/17/2018.
 */

public class GrammarCategoryAdapter extends SimpleAbstractAdapter<GrammarCategory, GrammarCategoryAdapter.ViewHolder> {

    public GrammarCategoryAdapter(List<GrammarCategory> itemList, OnRecyclerViewClickListener clickListener) {
        super(itemList, clickListener);
    }

    @Override
    protected void bindItemData(ViewHolder viewHolder, GrammarCategory data, int position) {
        viewHolder.nameCategory.setText(data.getCategory());
        //viewHolder.shortName.setText(getShortName(false, data.getCategory()));
        //viewHolder.shortName.setText(shortenUpToTwoCharacters(data.getCategory()));
        viewHolder.shortName.setText(shortName(data.getCategory()));

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grammar_category, parent, false);
        return new ViewHolder(itemView, mClickListener);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {

        private TextView shortName, nameCategory;
        private OnRecyclerViewClickListener clickListener;

        public ViewHolder(View itemView, OnRecyclerViewClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;

            shortName = (TextView) itemView.findViewById(R.id.short_name);
            nameCategory = (TextView) itemView.findViewById(R.id.name_category);

            this.itemView.setOnClickListener(this);
            this.itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClicked(view, getAdapterPosition ());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (clickListener != null) {
                clickListener.onItemLongClicked(view, getAdapterPosition ());
            }
            return false;
        }
    }
}
