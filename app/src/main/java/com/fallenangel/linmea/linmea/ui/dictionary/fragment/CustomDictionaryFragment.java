package com.fallenangel.linmea.linmea.ui.dictionary.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.database.FirebaseWrapper;
import com.fallenangel.linmea.interfaces.OnChildListener;
import com.fallenangel.linmea.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea.interfaces.OnStartDragListener;
import com.fallenangel.linmea.linmea.adapter.CustomDictionaryAdapter;
import com.fallenangel.linmea.linmea.database.FirebaseHelper;
import com.fallenangel.linmea.linmea.preference.CustomDictionaryPreference;
import com.fallenangel.linmea.linmea.ui.dictionary.BaseDetailActivity;
import com.fallenangel.linmea.linmea.user.authentication.user;
import com.fallenangel.linmea.linmea.utils.callback.ItemTouchHelperCallback;
import com.fallenangel.linmea.linmea.utils.callback.ToolbarActionMode;
import com.fallenangel.linmea.model.CustomDictionaryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fallenangel.linmea.linmea.user.utils.SharedPreferencesUtils.getFromSharedPreferences;
import static com.fallenangel.linmea.profile.UserMetaData.getCurrentUser;
import static com.fallenangel.linmea.profile.UserMetaData.getUserUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomDictionaryFragment extends Fragment implements OnRecyclerViewClickListener, View.OnClickListener, OnStartDragListener, OnItemTouchHelper {

    //View
    private RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton, mFloatingActionButtonAdd, mFloatingActionButtonFilter, mFloatingActionButtonSort;
    private SearchView mSearchView;
    private TextView mTextView;
    private PopupMenu mPopupMenu;

    //Animation
    private Animation mFabOpen,mFabClose,mRotateForward,mRotateBackward;

    //Adapter
    private CustomDictionaryAdapter mAdapter;

    //Wrapper
    private FirebaseWrapper mFirebaseWrapper;

    //Helper
    private ItemTouchHelper mItemTouchHelper;
    private FirebaseHelper mFirebaseHelper;
    private ActionMode mActionMode;
    private SharedPreferences mSharedPreferences;

    //DataBase
    private DatabaseReference mDatabaseReference;

    //Other
    private  Thread mThread;
    private Paint mPaint;

    //Collection
    private List<CustomDictionaryModel> mItems;
    private List<Integer> mToPosIds;
    private List<Integer> mFromPosIds;

    //Boolean
    private Boolean isFabOpen = false;

    //
    private int mDictionarySize;

    public CustomDictionaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        mItems = new ArrayList<CustomDictionaryModel>();
        mToPosIds = new ArrayList<Integer>();
        mFromPosIds = new ArrayList<Integer>();
        mFirebaseWrapper = new FirebaseWrapper();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mPaint = new Paint();
        implementRecyclerViewAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        implementView(rootView);
        implementRecyclerView(rootView);
        implementFloatingActionButton(rootView);
        implementAnimation();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        implementOnRecyclerScrollListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        resetActionMode();
    }

    private void implementRecyclerViewAdapter(){
        mAdapter = new CustomDictionaryAdapter(getActivity(), mItems, this, this, this);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback itemTouchCallBack = new ItemTouchHelperCallback(getActivity(), mAdapter);
        mItemTouchHelper = new ItemTouchHelper(itemTouchCallBack);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        if (getDictionary()!= null && getCurrentUser() != null){
            updateData();
        }
        mAdapter.notifyDataSetChanged();
    }

    private void implementFloatingActionButton(View rootView){
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button);
        mFloatingActionButtonAdd = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button_add);
        mFloatingActionButtonFilter = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button_filter);
        mFloatingActionButtonSort = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button_sort);

        mFloatingActionButton.setOnClickListener(this);
        mFloatingActionButtonAdd.setOnClickListener(this);
        mFloatingActionButtonFilter.setOnClickListener(this);
        mFloatingActionButtonSort.setOnClickListener(this);
    }

    private void implementRecyclerView(View rootView){
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void implementView(View rootView){
        mRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.relative_layout);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mTextView = new TextView(getActivity());

        if (getDictionary()!= null){
            mTextView.setText("DICTIONARY IS EMPTY");
        } else {
            mTextView.setText("DICTIONARY NOT SELECTED");
        }

        mTextView.setTextSize(18);
        mRelativeLayout.addView(mTextView, layoutParams);
    }

    private void implementAnimation(){
        mFabOpen = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        mFabClose = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        mRotateForward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_forward);
        mRotateBackward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_backward);
    }

    private void implementOnRecyclerScrollListener(){
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mFloatingActionButton.getVisibility() == View.VISIBLE) {
                    mFloatingActionButton.hide();
                    mFloatingActionButton.setClickable(false);
                    if (isFabOpen){
                        hideFAB();
                    }
                } else if (dy < 0 && mFloatingActionButton.getVisibility() != View.VISIBLE) {
                    mFloatingActionButton.show();
                    mFloatingActionButton.setClickable(true);

                }
            }
        });
    }

    public List<CustomDictionaryModel> updateData(){
        String path = "custom_dictionary/" + user.getCurrentUserUID() + "/dictionaries/" + getDictionary();

        mFirebaseHelper = new FirebaseHelper(getActivity(), mItems, path);
        mItems = mFirebaseHelper.getItemsList(new OnChildListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s, int index) {
                mAdapter.setItems(mItems);
                mAdapter.notifyDataSetChanged();
                //mAdapter.orderBy(getActivity(), mItems);
                mTextView.setVisibility(View.GONE);
                mDictionarySize = mAdapter.getItemCount();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s, int index) {
                mAdapter.setItems(mItems);
                mAdapter.notifyItemRangeChanged(0, mItems.size());
                mAdapter.notifyDataSetChanged();
                mTextView.setVisibility(View.GONE);
                mDictionarySize = mAdapter.getItemCount();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot, int index) {
                CustomDictionaryModel item = mFirebaseWrapper.getCustomDictionaryWord(dataSnapshot);
                for (int i = 0; i < mItems.size(); i++) {
                    if (mItems.get(i).getUID().equals(item.getUID())){
                        mAdapter.removeItem(i);
                        break;
                    }
                }


                if (mItems.isEmpty()){
                    mTextView.setVisibility(View.VISIBLE);
                } else {
                    mTextView.setVisibility(View.GONE);
                }
                mDictionarySize = mAdapter.getItemCount();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s, int index) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return mItems;
    }

    public void animateFAB(){
        if(isFabOpen){
            hideFAB();
        } else {
            showFAB();
        }
    }

    private void hideFAB(){
        mFloatingActionButton.startAnimation(mRotateBackward);
        mFloatingActionButtonAdd.startAnimation(mFabClose);
        mFloatingActionButtonSort.startAnimation(mFabClose);
        mFloatingActionButtonFilter.startAnimation(mFabClose);
        mFloatingActionButtonAdd.setClickable(false);
        mFloatingActionButtonSort.setClickable(false);
        mFloatingActionButtonFilter.setClickable(false);
        isFabOpen = false;
    }

    private void showFAB(){
        mFloatingActionButton.startAnimation(mRotateForward);
        mFloatingActionButtonAdd.startAnimation(mFabOpen);
        mFloatingActionButtonSort.startAnimation(mFabOpen);
        mFloatingActionButtonFilter.startAnimation(mFabOpen);
        mFloatingActionButtonAdd.setClickable(true);
        mFloatingActionButtonSort.setClickable(true);
        mFloatingActionButtonFilter.setClickable(true);
        isFabOpen = true;
    }

    private void resetActionMode(){
        if (mActionMode != null){
            mAdapter.removeSelection();
            mActionMode.finish();
            setNullToActionMode();
        }
    }

    private String getDictionary(){
        String dictionary;
        Intent intent = getActivity().getIntent();
        if (intent.getStringExtra("DictionaryName") != null) {
            dictionary = intent.getStringExtra("DictionaryName");
            return  dictionary;
        }
        if (intent.getStringExtra("DictionaryName") == null && getCurrentUser() != null) {
            dictionary = getFromSharedPreferences(getActivity(), "DefaultDictionaryName", getUserUID());
            return dictionary;
        }  else {
            return null;
        }
    }



    /*
    *SelectionMode
    */
    public void setNullToActionMode() {
        if (mActionMode != null){
            mActionMode = null;
        }
    }

    public void selectionMode() {
        if (mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new ToolbarActionMode(getActivity(), mAdapter, this));
        }else if (mActionMode != null) {
            mActionMode.finish();
        }
        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(mAdapter.getSelectedCount()) + " selected");
    }


    /*
    *       Options Menu
    */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<CustomDictionaryModel> searchResult = searchFilter(mItems, newText);
                mAdapter.setFilter(searchResult);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.cd_settings:
                Intent settings = new Intent(getActivity(), CustomDictionaryPreference.class);
                startActivity(settings);
                break;
            case R.id.cd_selection_toggle:
                selectionMode();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<CustomDictionaryModel> searchFilter(List<CustomDictionaryModel> items, String query) {
        mSharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        int searchBy = Integer.parseInt(mSharedPreferences.getString("searchBy", "0"));
        String text;
        query = query.toLowerCase();
        final List<CustomDictionaryModel> filteredList = new ArrayList<>();
        for (CustomDictionaryModel item : items) {
            switch (searchBy){
                case 0:
                    text = item.getWord().toLowerCase();
                    if (text.contains(query)) {
                        filteredList.add(item);
                    }
                    break;
                case 1:
                    text = item.getTranslationString().toLowerCase();
                    if (text.contains(query)) {
                        filteredList.add(item);
                    }
                    break;
            }
        }
        return filteredList;
    }


    /*
    *   Event Actions
    */

    private void actionAdd(){
        Intent addWordIntent = new Intent(getActivity(), BaseDetailActivity.class);
        addWordIntent.putExtra("DictionaryName", getFromSharedPreferences(getActivity(), "DefaultDictionaryName", getUserUID()));
        addWordIntent.putExtra("Mod", "AddMOD");
        startActivity(addWordIntent);
    }
    private void actionFilter(){
//        Intent addDictionary = new Intent(getActivity(), AddCustomDictionaryActivity.class);
//        startActivity(addDictionary);
    }

    private void actionSort(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Switch sw = new Switch(getActivity());
        sw.setTextOn("start");
        sw.setTextOff("close");
        sw.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setTitle(R.string.sorting_elements)
                .setItems(R.array.order_entries, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SP.edit().putString("orderBy", String.valueOf(which)).commit();
                    }
                });
        builder.show();
    }

    public void itemSelect(View view, int position) {
        mAdapter.toggleSelection(position);
        boolean hasCheckedItems = mAdapter.getSelectedCount() > 0;
        if (hasCheckedItems && mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new ToolbarActionMode(getActivity(), mAdapter, this));
        }else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
        }
        if (mActionMode != null){
            mActionMode.setTitle(String.valueOf(mAdapter.getSelectedCount()) + " selected");
        }
    }

    /*
    *   Popup Single Action Events
    */
    private void detailView (int position){
        Intent changeIntent = new Intent(getActivity(), BaseDetailActivity.class);
        changeIntent.putExtra("WordUID", mItems.get(position).getUID());
        changeIntent.putExtra("DictionaryName", getDictionary());
        changeIntent.putExtra("Mod", "EditMod");
        startActivity(changeIntent);
    }

    private void editItem(int position){
        Intent changeIntent = new Intent(getActivity(), BaseDetailActivity.class);
        changeIntent.putExtra("WordUID", mItems.get(position).getUID());
        changeIntent.putExtra("DictionaryName", getDictionary());
        changeIntent.putExtra("Mod", "EditMod");
        startActivity(changeIntent);
    }

    public void doShare(int position) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Word: " + mItems.get(position).getWord()  +
                        "\nTranslation: " + mItems.get(position).getTranslationString());
        startActivity(intent);    }

    public void deleteItem (final int position){
        final CustomDictionaryModel item = mItems.get(position);
        mDatabaseReference.child("custom_dictionary")
                .child(getUserUID())
                .child("dictionaries")
                .child(getDictionary())
                .child(item.getUID()).removeValue();
    }

    public void changeLearnedStatus (final int position){
        DatabaseReference dbLearned = mDatabaseReference.child("custom_dictionary")
                .child(getUserUID())
                .child("dictionaries")
                .child(getDictionary())
                .child(mItems.get(position).getUID())
                .child("status");

        if (mItems.get(position).getStatus() == true){
            dbLearned.setValue(false);
        } else {
            dbLearned.setValue(true);
        }
    }

    public void changeFavoriteStatus (final int position){
        DatabaseReference dbFavorite = mDatabaseReference.child("custom_dictionary")
                .child(getUserUID())
                .child("dictionaries")
                .child(getDictionary())
                .child(mItems.get(position).getUID())
                .child("favorite");
        if (mItems.get(position).getFavorite() == true){
            dbFavorite.setValue(false);
        } else {
            dbFavorite.setValue(true);
        }
    }



    /*
    *   Popup Multi Selected Action Events
    */
    public void deleteSelectedItems(){
        SparseBooleanArray selected = mAdapter.getSelectedIds();
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //  deleteItem(i);
                final CustomDictionaryModel item = mItems.get(selected.keyAt(i));
                mDatabaseReference.child("custom_dictionary")
                        .child(getUserUID())
                        .child("dictionaries")
                        .child(getDictionary())
                        .child(item.getUID()).removeValue();
            }
        }
    }

    public void changeSelectedItemsLearnedStatus (){
        SparseBooleanArray selected = mAdapter.getSelectedIds();
        for (int i = 0; i < selected.size(); i++) {
            if (selected.valueAt(i)) {

                final CustomDictionaryModel item = mItems.get(selected.keyAt(i));
                DatabaseReference databaseReferenceLearned = mDatabaseReference.child("custom_dictionary")
                        .child(getUserUID())
                        .child("dictionaries")
                        .child(getDictionary())
                        .child(item.getUID())
                        .child("status");
                if (item.getStatus() == true){
                    databaseReferenceLearned.setValue(false);
                } else {
                    databaseReferenceLearned.setValue(true);
                }
            }
        }
    }

    public void changeSelectedItemsFavoriteStatus (){
        SparseBooleanArray selected = mAdapter.getSelectedIds();
        for (int i = (selected.size()); i >= 0; i--) {
            if (selected.valueAt(i)) {
                final CustomDictionaryModel item = mItems.get(selected.keyAt(i));
                DatabaseReference databaseReferenceFavorite = mDatabaseReference.child("custom_dictionary")
                        .child(getUserUID())
                        .child("dictionaries")
                        .child(getDictionary())
                        .child(item.getUID())
                        .child("favorite");

                if (item.getFavorite() == true){
                    databaseReferenceFavorite.setValue(false);
                } else {
                    databaseReferenceFavorite.setValue(true);
                }
            }
        }
    }



    /*
    *   Event Listeners
    */
    @Override
    public void onClick(View v) {
        // FloatingActionButton OnClick
        switch (v.getId()){
            case R.id.floating_action_button:
                animateFAB();
                break;
            case R.id.floating_action_button_add:
                actionAdd();
                break;
            case R.id.floating_action_button_filter:
                actionFilter();
                animateFAB();
                break;
            case R.id.floating_action_button_sort:
                actionSort();
                animateFAB();
                break;
        }
    }

    @Override
    public boolean onMove(int fromPosition, int toPosition) {
        mToPosIds.add(toPosition);
        mFromPosIds.add(fromPosition);
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        //if (direction == ItemTouchHelper.LEFT){
            mAdapter.removeItem(position);
        //} else {

        //}
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

    }

    @Override
    public void onItemMoveComplete(RecyclerView.ViewHolder viewHolder) {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> updateMap = mAdapter.getMovedItems();
                mDatabaseReference
                        .child("custom_dictionary")
                        .child(getUserUID())
                        .child("dictionaries")
                        .child(getDictionary())
                        .updateChildren(updateMap);
                mAdapter.clearMoved();
            }
        });
        mThread.setDaemon(true);
        mThread.start();
    }

    //    @Override
//    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
////        Bitmap icon;
////        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
////
////            View itemView = viewHolder.itemView;
////            float height = (float) itemView.getBottom() - (float) itemView.getTop();
////            float width = height / 3;
////
////            if(dX > 0){
////                mPaint.setColor(Color.parseColor("#388E3C"));
////                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
////                c.drawRect(background, mPaint);
////                icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit);
////                RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
////                c.drawBitmap(icon,null,icon_dest, mPaint);
////            } else {
////                mPaint.setColor(Color.parseColor("#D32F2F"));
////                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
////                c.drawRect(background, mPaint);
////                icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
////                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
////                c.drawBitmap(icon,null,icon_dest, mPaint);
////            }
////        }
//    }
//
//    @Override
//    public void onClearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//
//    }
//
//    @Override
//    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
//        return 0;
//    }
//
//    @Override
//    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
////        final View foregroundView = ((CustomDictionaryAdapter.ViewHolder) viewHolder).mBackCardView;
////        getDefaultUIUtil().clearView(foregroundView);
//    }
//
//    @Override
//    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        final View foregroundView = ((CustomDictionaryAdapter.ViewHolder) viewHolder).mBackCardView;
//        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
//    }

                //    @Override
                //    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                //        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && actionState != ItemTouchHelper.ACTION_STATE_DRAG) {
                //            mThread = new Thread(new Runnable() {
                //                @Override
                //                public void run() {
                //
                //                    Map<String, Object> updateMap = mAdapter.getMovedItems();
                //                    mDatabaseReference
                //                            .child("custom_dictionary")
                //                            .child(getUserUID())
                //                            .child("dictionaries")
                //                            .child(getDictionary())
                //                            .updateChildren(updateMap);
                //                    mAdapter.clearMoved();
                //                }
                //            });
                //            mThread.setDaemon(true);
                //            mThread.start();
                //        }
                //        if (viewHolder != null) {
                //            final View foregroundView = ((CustomDictionaryAdapter.ViewHolder) viewHolder).mBackCardView;
                //
                //            getDefaultUIUtil().onSelected(foregroundView);
                //        }
                //    }

    @Override
    public void onItemClicked(View view, int position) {
        if (mActionMode != null){
            itemSelect(view, position);
        }else {
            Intent intent = new Intent(getActivity(), BaseDetailActivity.class);
            intent.putExtra("WordUID", mItems.get(position).getUID());
            intent.putExtra("DictionaryName", getDictionary());
            intent.putExtra("Mod", "DetailViewMod");
            startActivity(intent);
        }
    }

    @Override
    public void onOptionsClicked(View view, int position) {
        popupMenu(view, position);
    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        itemSelect(view, position);
        return false;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }



    private void popupMenu(final View view, final int position){
        mPopupMenu = new PopupMenu(getActivity(), view);
        mPopupMenu.inflate(R.menu.custom_dictionary_popup_menu);
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.popup_menu_favorite:
                        changeFavoriteStatus(position);
                        break;
                    case R.id.popup_menu_learned:
                        changeLearnedStatus(position);
                        break;
                    case R.id.popup_menu_edit:
                        editItem(position);
                        break;
                    case R.id.popup_menu_detail_view:
                        detailView(position);
                        break;
                    case R.id.popup_menu_delete:
                        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                        adb.setTitle(getResources().getString(R.string.delete));
                        adb.setMessage(getResources().getString(R.string.delete_messege));
                        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteItem(position);
                            }
                        });
                        adb.setNegativeButton("No", null);
                        adb.show();
                        break;
                    case R.id.popup_menu_select:
                        itemSelect(view, position);
                        break;
                    case R.id.popup_menu_move_to:

                        break;
                    case R.id.popup_menu_share:
                        doShare(position);
                        break;

                }
                return true;
            }

        });
        mPopupMenu.show();
    }


    public int getDictionarySize(){
        return mDictionarySize;
    }
}
