package com.fallenangel.linmea.linmea.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;

import com.fallenangel.linmea.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea.interfaces.OnStartDragListener;
import com.fallenangel.linmea.model.BaseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NineB on 9/18/2017.
 */

public abstract class AbstractRecyclerViewAdapter<T extends BaseModel, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements OnItemTouchHelper {

    protected List<T> mItems;
    protected Context mContext;
    protected OnRecyclerViewClickListener mClickListener;
    protected OnItemTouchHelper mOnItemTouchHelper;
    protected final OnStartDragListener mDragStartListener;
    protected SparseBooleanArray mSelectedItems;
    protected LayoutInflater mInflater;
    protected Map<String, Object> mMovedItems;

    private Thread mThread;

    protected AbstractRecyclerViewAdapter(Context context, List<T> items, OnRecyclerViewClickListener clickListener, OnStartDragListener dragStartListener, OnItemTouchHelper onItemTouchHelper){
        this.mContext = context;
        this.mItems = items;
        this.mClickListener = clickListener;
        this.mDragStartListener = dragStartListener;
        this.mOnItemTouchHelper = onItemTouchHelper;
        mMovedItems = new HashMap<String, Object>();
        mSelectedItems = new SparseBooleanArray();
        mInflater = LayoutInflater.from(context);
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

    public void setItems(List<T> words) {
        mItems = words;
    }

    public T getItem(int position) {
        position = Math.max(0, position);
        return mItems.get(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public T getItems (int position){
        final T itemList = mItems.get(position);
        return itemList;
    }

    public void clear (){
        mItems.clear();
    }

    ////______________________________________ACTIONS_______________________________________
    //Remove
    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
        //notifyItemRangeChanged(position,mItems.size());
    }

    public void setFilter(List<T> item) {
        mItems = new ArrayList<T>();
        mItems.addAll(item);
        notifyDataSetChanged();
    }

    public void updateId (Boolean reverse){
        Collections.sort(mItems, new IdCompare());
        if (reverse){
            Collections.reverse(mItems);
        }
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
       // removeItem(position);
        //mItems.remove(position);
        //notifyItemRemoved(position);
     //   notifyItemRangeChanged(position, mItems.size());
       // mOnItemTouchHelper.onSwiped(viewHolder, direction, position);

    }

    @Override
    public boolean onMove(int fromPosition, int toPosition) {
//        if(mSelectedItems.size()>0){
//            moveSelectedItems(fromPosition, toPosition);
//        } else {
            moveItem(fromPosition, toPosition);
//        }

        if (mSelectedItems.size()>0){
            mSelectedItems.delete(fromPosition);
            mSelectedItems.put(toPosition, !mSelectedItems.get(toPosition));
        }
        mMovedItems.put(mItems.get(toPosition).getUID() + "/id", toPosition);
        mMovedItems.put(mItems.get(fromPosition).getUID() + "/id", fromPosition);


        //mOnItemTouchHelper.onMove(fromPosition, toPosition);
        return true;
    }

    private void moveItem(int fromPosition, int toPosition){
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

    //    @Override
//    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//    }

}
