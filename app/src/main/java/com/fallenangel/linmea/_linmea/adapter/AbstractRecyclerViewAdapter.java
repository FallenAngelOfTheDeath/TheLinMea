package com.fallenangel.linmea._linmea.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;

import com.fallenangel.linmea._linmea.interfaces.OnStartDragListener;
import com.fallenangel.linmea._linmea.model.BaseModel;
import com.fallenangel.linmea.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private List<String> mSortedString;


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

        /// rewrite add sorted uid to string

        String TAG = "testTAG";
        Log.i(TAG, "_______________________________________________________________________________________________________________________________________________________________________");
        Log.i(TAG, "entered sorted string: " + sortedString);
        List<T> sortedList = new ArrayList<>();
        if (!sortedString.isEmpty()) {
            Gson gson = new Gson();
            List<String> listOfSortedUIDS = gson.fromJson(sortedString, new TypeToken<List<String>>() {}.getType());
            Log.i(TAG, "list of sorted uids: " + listOfSortedUIDS);
Log.i(TAG, "------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            Log.i(TAG, "sorted string is not empty");
            int sortingListSize = listOfSortedUIDS.size(); Log.i(TAG, "sorting List Size: " + sortingListSize);
Log.i(TAG, "------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
             if (listOfSortedUIDS != null && listOfSortedUIDS.size() > 0) {
                 Log.i(TAG, "sorting list not empty");
                 for (int i = 0; i < sortingListSize; i++) {
                     int enteredItemsSize = items.size();
                     Log.i(TAG, "entered items size: " + enteredItemsSize);
                     for (int j = 0; j < enteredItemsSize; j++) {
                         if (listOfSortedUIDS.get(i).equals(items.get(j).getUID())) {
                             sortedList.add(items.get(j));
                             Log.i(TAG, "item: " + items.get(j).getUID() + " added to sorted list");
                             break;
                         }
                     }
                 }
                 Log.i(TAG, "----------final array --------------: " + sortedList);
                // sortedList.clear();
                 sortedString = "";
                 mItems = sortedList;
             } else {
                 Log.i(TAG, "sorting list is empty");
                 mItems = items;
             }

        } else {
            Log.i(TAG, "sorted string is empty");
            mItems = items;
        }
        Log.i("testTAG", "_______________________________________________________________________________________________________________________________________________________________________");










//
//
//
//        List<T> sortedList = new ArrayList<>();
//        Gson gson = new Gson();
//        List<String> listOfSortedUIDS = gson.fromJson(sortedString, new TypeToken<List<String>>() {}.getType());
//        Log.i("testTAG", "listOfSortedUIDS: " + listOfSortedUIDS);
//        Log.i("testTAG", "items: " + items);
//        Log.i("testTAG", "mItems: " + mItems);
//        int sizeSorted = 0;
//        if (listOfSortedUIDS != null) {sizeSorted = listOfSortedUIDS.size();}
//        int sizeItems = items.size();
//        int sizeMitem = mItems.size();
//        Log.i("testTAG", "1 - setItems: sorted " + sizeSorted + " sizeItems: " + sizeItems + " sizeMitem: " + sizeMitem );
//
//
//
//        if (!sortedString.isEmpty() || sortedString == null) {
//            mItems.clear();
//
//            List<T> tmp = new ArrayList<>();
//            tmp = items;
////            Gson gson = new Gson();
////            List<String> listOfSortedUIDS = gson.fromJson(sortedString, new TypeToken<List<String>>() {}.getType());
////            Log.i(TAG, "listOfSortedUIDS: " + listOfSortedUIDS);
//           // int i = 0;
//
//            if (listOfSortedUIDS != null && listOfSortedUIDS.size() > 0){
//               // for (String id: listOfSortedUIDS){
//                for (int j = 0; j < listOfSortedUIDS.size(); j++) {
//                    Log.i("testTAG", "setItems: -----1st--------------------------------------------------");
//                    Log.i("testTAG", "2 - setItems: sorted " + sizeSorted + " sizeItems: " + sizeItems + " sizeMitem: " + sizeMitem );
//
//                    //for (T item: items){
//                    for (int i = 0; i < items.size(); i++) {
//                        Log.i("testTAG", "setItems: -----2st--------------------------------------------------");
//                        Log.i("testTAG", "3 - setItems: sorted " + sizeSorted + " sizeItems: " + sizeItems + " sizeMitem: " + sizeMitem );
//
//
//                        if (items.get(i).getUID().equals(listOfSortedUIDS.get(j))){
//                            sortedList.add(items.get(i));
//                            Log.i("testTAG", "setItem to: " + j + " / " + i + " : " + items.get(i));
//                            Log.i("testTAG", "4 - setItems: sorted " + sizeSorted + " sizeItems: " + sizeItems + " sizeMitem: " + sizeMitem );
//
//                          //  items.remove(items.get(i));
//                          //  i++;
//                            break;
//                        }
//                    }
//                }
//            }
//            Log.i(TAG, "setItems sortedList: " + sortedList);
//            Log.i("testTAG", "5 - setItems: sorted " + sizeSorted + " sizeItems: " + sizeItems + " sizeMitem: " + sizeMitem );
//
//            //  Log.i(TAG, "setItems items: " + items);
//
//
//
//
//
////            Gson gson = new Gson();
////            List<String> listOfSortedUIDS = gson.fromJson(sortedString, new TypeToken<List<String>>() {}.getType());
////            Log.i(TAG, "setItems: " + items);
////            if (listOfSortedUIDS != null && listOfSortedUIDS.size() > 0) {
////                for (int i = 0; i < listOfSortedUIDS.size(); i++) {
////                    Log.i(TAG, "setItems listOfSortedUIDS: " + i + " : " + listOfSortedUIDS.get(i));
////                    for (int j = 0; j < items.size(); j++) {
////                        Log.i(TAG, "setItems: items" + j + " : " + items.get(j).getUID());
////                        if (listOfSortedUIDS.get(i).equals(items.get(j).getUID())) {
////                            sortedList.add(items.get(j));
////                            Log.i(TAG, "setItems: break" + items.get(j));
////                            Log.i(TAG, "setItems: if " + sortedString);
////                            items.remove(j);
////                            break;
////                        }
////                    }
////                }
////                if (items.size() > 0){
////                    sortedList.addAll(items);
////                }
//                //mItems.clear();
//                mItems = sortedList;
//            } else {
//                mItems = items;
//            }
//        Log.i("testTAG", "setItems: finale array" + mItems);
//        Log.i("testTAG", "_______________________________________________________________________________________________________________________________________________________________________");
        }


    public T getItem(int position) {
        position = Math.max(0, position);
        return mItems.get(position);
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

    public void clear (){
        mItems.clear();
    }

//    public void setSortedItems (List<T> items, String stringOfSortedUID)  {
//        List<T> sortedList = new ArrayList<>();
//        if (!stringOfSortedUID.isEmpty()){
//            Gson gson = new Gson();
//            List<String> listOfSortedUIDS = gson.fromJson(stringOfSortedUID, new TypeToken<List<String>>(){}.getType());
//            if (listOfSortedUIDS != null && listOfSortedUIDS.size() > 0){
//
//                for (String id: listOfSortedUIDS) {
//                    for (int i = 0; i < items.size(); i++) {
//                        Log.i("lllllllllllll", "getSort mItems: "+ i + " : "  + items.get(i).getUID() + " : " + id);
//                        if(items.get(i).getUID().equals(id)){
//                            sortedList.add(items.get(i));
//                            items.remove(i);
//                            Log.i("lllllllllllll", "getSort sortedList: " + i + " : "  + sortedList.get(i).getUID());
//                            break;
//                        }
//                    }
//                }
//
//            }
//            if (items.size() > 0){
//                sortedList.addAll(items);
//            }
//            mItems = sortedList;
//        }
//    }

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
            Log.i("testTAG", "===========================================================================================================");
            Log.i("testTAG", "onMove sorted string: " + item.getUID());
            Log.i("testTAG", "===========================================================================================================");
        }

        if (mSelectedItems.size()>0){
            mSelectedItems.delete(fromPosition);
            mSelectedItems.put(toPosition, !mSelectedItems.get(toPosition));
        }

        mMovedItems.put(mItems.get(toPosition).getUID() + "/id", toPosition);
        mMovedItems.put(mItems.get(fromPosition).getUID() + "/id", fromPosition);
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

    public List<String> getSortedString() {
        return mSortedString;
    }
}
