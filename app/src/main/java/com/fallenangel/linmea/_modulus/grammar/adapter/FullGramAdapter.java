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
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._linmea.model.DictionaryCustomizer;
import com.fallenangel.linmea._modulus.grammar.model.FullGrammarModel;
import com.fallenangel.linmea._modulus.non.adapter.SimpleAbstractAdapter;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by NineB on 1/18/2018.
 */

public class FullGramAdapter extends SimpleAbstractAdapter<FullGrammarModel, FullGramAdapter.ViewHolder> {

    private int mLearnedColor;
    private Context mContext;
    private String TAG = "GrammarsListAdapter";
    private Set<FullGrammarModel> mItemSet;

    public FullGramAdapter(Context context, Set<FullGrammarModel> itemSet,  OnRecyclerViewClickListener clickListener) {
        super(new ArrayList<>(), clickListener);
        this.mContext = context;
        this.mLearnedColor = DictionaryCustomizer.getLearnedColorStatic(mContext);
        this.mItemSet = itemSet;
        convertSetToList();
    }

    @Override
    protected void bindItemData(ViewHolder viewHolder, FullGrammarModel data, int position) {
            viewHolder.nameCategory.setText(data.getGrammar());
            viewHolder.shortName.setText(shortName(data.getGrammar()));

            viewHolder.option.setVisibility(View.VISIBLE);
            //if (Constant.DEBUG == 1) Log.i(TAG, "bindItemData: " + mUserData.size());

            Boolean learned = data.getLearned();
            Boolean favorite = data.getFavorite();
            if (favorite == null)
                favorite = false;
            if (learned == null)
                learned = false;

            if (learned){
                viewHolder.cardView
                        .setBackgroundColor(learned ? mLearnedColor
                                : Color.WHITE);
            } else {
                viewHolder.cardView
                        .setBackgroundColor(learned ? mLearnedColor
                                : Color.WHITE);
            }

            if(favorite){
                viewHolder.fav
                        .setVisibility(favorite ? View.VISIBLE
                                : View.GONE);
                //viewHolder.shortName
                //.setVisibility(mUserData.get(position).getLearned() ? View.GONE
                // : View.VISIBLE);
                viewHolder.shortName
                        .setTextColor(favorite ? Color.WHITE
                                : Color.BLACK);
            } else {
                viewHolder.fav
                        .setVisibility(favorite ? View.VISIBLE
                                : View.GONE);
                viewHolder.shortName
                        .setTextColor(favorite ? Color.WHITE
                                : Color.BLACK);
            }
    }

    private void convertSetToList(){
        if (mItemsList != null) mItemsList.clear();
        for (FullGrammarModel item:mItemSet) {
            mItemsList.add(item);
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grammar_category, parent, false);
        return new ViewHolder(itemView, mClickListener);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {

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

            this.cardView.setOnClickListener(this);
            this.option.setOnClickListener(this);
            this.itemView.setOnLongClickListener(this);
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
                clickListener.onItemLongClicked(view, getAdapterPosition ());
            }
            return false;
        }
    }
}
