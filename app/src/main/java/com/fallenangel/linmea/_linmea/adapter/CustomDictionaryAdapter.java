package com.fallenangel.linmea._linmea.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._linmea.interfaces.OnStartDragListener;
import com.fallenangel.linmea._linmea.model.CustomDictionaryModel;
import com.fallenangel.linmea._linmea.model.DictionaryCustomizer;
import com.fallenangel.linmea.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea.interfaces.OnItemTouchHelperViewHolder;

import java.util.List;

/**
 * Created by NineB on 9/18/2017.
 */

public class CustomDictionaryAdapter extends AbstractRecyclerViewAdapter<CustomDictionaryModel, CustomDictionaryAdapter.ViewHolder> {

    private Boolean mDragAndDrop;
    private int mDisplayType, mFountSizeWord, mFountSizeTranslation, mOptionsMenu, mLearnedColor, mSelectedColor;

//    public static final String CUSTOM_DICTIONARY_PAGE_1 = "CUSTOM_DICTIONARY_PAGE_1";
//    public static final String CUSTOM_DICTIONARY_PAGE_2 = "CUSTOM_DICTIONARY_PAGE_2";
//    public static final String DRAG_AND_DROP = "DRAG_AND_DROP";

    private DictionaryCustomizer mDictionaryCustomizer;

    public CustomDictionaryAdapter(Context context, List<CustomDictionaryModel> items, OnRecyclerViewClickListener clickListener, OnStartDragListener dragStartListener, OnItemTouchHelper onItemTouchHelper, DictionaryCustomizer dictionaryCustomizer) {
        super(context, items, clickListener, dragStartListener, onItemTouchHelper);
       // this.mDictionaryCustomizer = dictionaryCustomizer;
        this.mDictionaryCustomizer = dictionaryCustomizer;
        //mDictionaryCustomizer = new DictionaryCustomizer(mContext, mMode);
        mDisplayType = mDictionaryCustomizer.getDisplayType();
        mFountSizeWord = mDictionaryCustomizer.getFountSizeWord();
        mFountSizeTranslation = mDictionaryCustomizer.getFountSizeTranslation();
        mDragAndDrop = mDictionaryCustomizer.getDragAndDrop();
        mOptionsMenu = mDictionaryCustomizer.getOptionsMenu();
        mLearnedColor = mDictionaryCustomizer.getLearnedColor();
        mSelectedColor = mDictionaryCustomizer.getColorOfSelected();
    }
//    String displayTypeKey = "displayType";
//    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
//    String displayTypeN = sharedPref.getString(displayTypeKey, "0");
//    int displayType = Integer.parseInt(displayTypeN);
//
//    int fountSizeWord = sharedPref.getInt("fountSizeWord", 18);
//    int fountSizeTranslation = sharedPref.getInt("fountSizeTranslation", 16);

//    int displayType = Integer.parseInt(getFromSharedPreferences(mContext, mMode, DISPLAY_TYPE));

    //int fountSizeWord = mDictionaryCustomizer.getFountSizeWord();
        //Integer.parseInt(getFromSharedPreferences(mContext, mMode, FOUNT_SIZE_WORD));
   // int fountSizeTranslation = mDictionaryCustomizer.getFountSizeTranslation();
                //Integer.parseInt(getFromSharedPreferences(mContext, mMode, FOUNT_SIZE_TRANSLATION));


    @Override
    protected void bindItemData(final ViewHolder viewHolder, CustomDictionaryModel data, int position) {
        //int displayType = Integer.parseInt(getFromSharedPreferences(mContext, mMode, DISPLAY_TYPE));
       // CustomDictionaryModel item = mItems.get(position);
        switch (mDisplayType){
            case 0:
                viewHolder.mWord.setText(data.getWord());
                viewHolder.mTranslation.setText(data.getTranslationString());
                viewHolder.mWord.setTextSize(mFountSizeWord);
                viewHolder.mTranslation.setTextSize(mFountSizeTranslation);
                break;
            case 1:
                viewHolder.mWord.setText(data.getWord());
                viewHolder.mWord.setTextSize(mFountSizeWord);
                break;
            case 2:
                viewHolder.mTranslation.setText(data.getTranslationString());
                viewHolder.mTranslation.setTextSize(mFountSizeTranslation);
                break;
            case 3:
                viewHolder.mWord.setText(data.getWord());
                viewHolder.mTranslation.setText(data.getTranslationString());
                viewHolder.mWord.setTextSize(mFountSizeWord);
                viewHolder.tvS.setTextSize(mFountSizeWord);
                viewHolder.mTranslation.setTextSize(mFountSizeTranslation);
                break;
        }


        viewHolder.mHandle.setOnTouchListener((v, event) -> {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
//                    ClipData data = ClipData.newPlainText("", "");
//                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
//                            v);
//                    v.startDrag(data, shadowBuilder, v, 0);
                mDragStartListener.onStartDrag(viewHolder);
            }
            return false;
        });

        if (data.getFavorite() == true) {
            viewHolder.mStatus.setImageResource(R.drawable.ic_favorite);
        } else {
            viewHolder.mStatus.setImageResource(R.drawable.ic_school);

        }
        changingBackgroundColors(viewHolder, data, position);
    }

    private void changingBackgroundColors(ViewHolder viewHolder, CustomDictionaryModel data, int position){
        if (mSelectedItems.size() > 0){

            viewHolder.mCardView
                    .setBackgroundColor(mSelectedItems.get(position) ? mSelectedColor//0x9934B5E4
                            : Color.WHITE);


            if (data.getStatus() == true && !mSelectedItems.get(position)){
                viewHolder.mCardView
                        .setBackgroundColor(mItems.get(position).getStatus() ? mLearnedColor
                                : Color.WHITE);

//                viewHolder.itemView.setBackgroundColor(mItems.get(position).getStatus() ? mContext.getResources().getColor(R.color.background_recyclerview)
//                        : Color.WHITE);
            }
        } else {
            viewHolder.mCardView
                    .setBackgroundColor(mItems.get(position).getStatus() ? mLearnedColor
                            : Color.WHITE);

//            viewHolder.itemView.setBackgroundColor(mItems.get(position).getStatus() ? mContext.getResources().getColor(R.color.background_recyclerview)
//                    : Color.WHITE);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.extended_list_item;
        switch (mDisplayType){
            case 0:
                layout = R.layout.extended_list_item;
                break;
            case 1:
                layout = R.layout.word_list_item;
                break;
            case 2:
                layout = R.layout.translation_list_item;
                break;
            case 3:
                layout = R.layout.simple_item;
                break;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view, mClickListener, mContext);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, OnItemTouchHelperViewHolder {

        public TextView mWord, mTranslation, tvS;
        public ImageView mOptions, mStatus, mHandle;
        public CardView mCardView;
        private Context mContext;
        private OnRecyclerViewClickListener mClickListener;

        public ViewHolder(View view, OnRecyclerViewClickListener clickListener, Context context) {
            super(view);
            this.mClickListener = clickListener;
            this.mContext = context;
//            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
//            String displayTypeN = sharedPref.getString("displayType", "0");
//            int displayType = Integer.parseInt(displayTypeN);

            switch (mDisplayType){
                case 0:
                    mWord = (TextView) itemView.findViewById(R.id.extended_list_item_word_text_view);
                    mTranslation = (TextView) itemView.findViewById(R.id.extended_list_item_translation_text_view);
                    break;
                case 1:
                    mWord = (TextView) itemView.findViewById(R.id.extended_list_item_word_text_view);
                    break;
                case 2:
                    mTranslation = (TextView) itemView.findViewById(R.id.extended_list_item_translation_text_view);
                    break;
                case 3:
                    tvS = (TextView) itemView.findViewById(R.id.s);
                    mWord = (TextView) itemView.findViewById(R.id.extended_list_item_word_text_view);
                    mTranslation = (TextView) itemView.findViewById(R.id.extended_list_item_translation_text_view);
                    break;
            }

            mStatus = (ImageView) itemView.findViewById(R.id.extended_list_item_status_image_view);
            mOptions = (ImageView) itemView.findViewById(R.id.extended_list_item_options_menu);
            mHandle = (ImageView) itemView.findViewById(R.id.extended_list_item_handle);
            mCardView = (CardView) itemView.findViewById(R.id.extended_list_item_card_view);


            if (mDragAndDrop){
                mHandle.setVisibility(View.VISIBLE);
                mHandle.setClickable(true);
            } else {
                mHandle.setVisibility(View.GONE);
                mHandle.setClickable(false);
            }


            switch (mOptionsMenu){
                case 0:
                    mOptions.setVisibility(View.VISIBLE);
                    mOptions.setOnClickListener(this);
                    mOptions.setClickable(true);
                    break;
                case 1:
                    mOptions.setVisibility(View.GONE);
                    mOptions.setClickable(false);
                    break;
            }



            mCardView.setOnClickListener(this);
            mCardView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.extended_list_item_options_menu:
                    if (mClickListener != null) {
                        mClickListener.onOptionsClicked(v, getAdapterPosition ());
                    }
                    break;
                case R.id.extended_list_item_card_view:
                    if (mClickListener != null) {
                        mClickListener.onItemClicked(v, getAdapterPosition ());
                    }
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mClickListener != null) {
                return mClickListener.onItemLongClicked(v, getAdapterPosition ());
            }
            return true;
        }

        @Override
        public void onItemSelected(RecyclerView.ViewHolder viewHolder) {
//            //itemView.setBackgroundColor(Color.LTGRAY);
            mCardView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            //itemView.setBackgroundColor(mContext.getResources().getColor(R.color.background_recyclerview));
//            if (mSelectedItems.get(viewHolder.getAdapterPosition())){
//                mCardView.setBackgroundColor(Color.WHITE);
//            } else {
//                mCardView.setBackgroundColor(Color.WHITE);
//            }
            final CustomDictionaryModel item = getItem(viewHolder.getAdapterPosition());
            changingBackgroundColors((ViewHolder) viewHolder, item, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public boolean onMove(int fromPosition, int toPosition) {
        super.onMove(fromPosition, toPosition);
        return mOnItemTouchHelper.onMove(fromPosition, toPosition);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
       // mOnItemTouchHelper.onSwiped(viewHolder, direction, position);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

    }

    @Override
    public void onItemMoveComplete(RecyclerView.ViewHolder viewHolder) {
        super.onItemMoveComplete(viewHolder);
        mOnItemTouchHelper.onItemMoveComplete(viewHolder);
    }
}
