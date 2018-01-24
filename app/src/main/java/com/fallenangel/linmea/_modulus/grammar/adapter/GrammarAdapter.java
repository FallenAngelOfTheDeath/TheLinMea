package com.fallenangel.linmea._modulus.grammar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.grammar.model.Grammar;
import com.fallenangel.linmea._modulus.non.adapter.SimpleAbstractAdapter;
import com.fallenangel.linmea._modulus.non.interfaces.OnShortRecyclerViewClickListener;

import java.util.List;

/**
 * Created by NineB on 1/17/2018.
 */

public class GrammarAdapter extends SimpleAbstractAdapter<Grammar, GrammarAdapter.ViewHolder> {

    public GrammarAdapter(List<Grammar> itemList, OnRecyclerViewClickListener clickListener) {
        super(itemList, clickListener);
    }

    @Override
    protected void bindItemData(ViewHolder viewHolder, Grammar data, int position) {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnShortRecyclerViewClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onItemClicked(View view, int position) {

        }
    }
}
