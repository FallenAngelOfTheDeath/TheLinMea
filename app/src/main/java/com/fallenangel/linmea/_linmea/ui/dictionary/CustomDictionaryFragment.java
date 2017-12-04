package com.fallenangel.linmea._linmea.ui.dictionary;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.adapter.CustomDictionaryAdapter;
import com.fallenangel.linmea._linmea.adapter.ItemSwipeHelperCallback;
import com.fallenangel.linmea._linmea.data.firebase.FirebaseDictionaryWrapper;
import com.fallenangel.linmea._linmea.data.firebase.FirebaseHelper;
import com.fallenangel.linmea._linmea.interfaces.OnChildListener;
import com.fallenangel.linmea._linmea.interfaces.OnStartDragListener;
import com.fallenangel.linmea._linmea.interfaces.UnderlayButtonClickListener;
import com.fallenangel.linmea._linmea.model.CustomDictionaryModel;
import com.fallenangel.linmea._linmea.model.DictionaryCustomizer;
import com.fallenangel.linmea._linmea.ui.preference.DictionaryCustomizerActivity;
import com.fallenangel.linmea._linmea.ui.preference.MainPreferenceActivity;
import com.fallenangel.linmea._linmea.util.SharedPreferencesUtils;
import com.fallenangel.linmea._linmea.view.UnderlayButton;
import com.fallenangel.linmea.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea.linmea.user.authentication.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.DICTIONARY;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.DRAG_AND_DROP;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.MAIN_GLOBAL_SETTINGS;
import static com.fallenangel.linmea._linmea.util.SharedPreferencesUtils.getFromSharedPreferences;
import static com.fallenangel.linmea.profile.UserMetaData.getCurrentUser;
import static com.fallenangel.linmea.profile.UserMetaData.getUserUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomDictionaryFragment extends Fragment implements OnRecyclerViewClickListener, View.OnClickListener, OnStartDragListener, OnItemTouchHelper, CompoundButton.OnCheckedChangeListener, UnderlayButtonClickListener {

    //View
    private RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton, mFloatingActionButtonAdd, mFloatingActionButtonFilter, mFloatingActionButtonSort;
    private SearchView mSearchView;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private PopupMenu mPopupMenu;
    private CoordinatorLayout mCoordinatorLayout;
    private BottomSheetDialog mBottomSheetDialog;
    private ViewGroup viewGroup;
    private Switch mBottomSheetDragAndDropSwitch;
    private Toolbar mToolbar;

    //Animation
    private Animation mFabOpen, mFabClose, mRotateForward, mRotateBackward;

    //Adapter
    private CustomDictionaryAdapter mAdapter;

    //Wrapper
    private FirebaseDictionaryWrapper mFirebaseDictionaryWrapper;

    //Helper
    private ItemTouchHelper mItemTouchHelper;
    private ItemSwipeHelperCallback mItemSwipeHelperCallback;
    private FirebaseHelper<CustomDictionaryModel> mFirebaseHelper;
    private ActionMode mActionMode;
    private SharedPreferences mSharedPreferences;
    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;
    private DictionaryCustomizer mDictionaryCustomizer;

    //DataBase
    private DatabaseReference mDatabaseReference;

    //Other
    private Thread mThread;
    private Paint mPaint;

    //Collection
    private List<CustomDictionaryModel> mItems;
    private List<Integer> mToPosIds;
    private List<Integer> mFromPosIds;

    //Boolean
    private Boolean isFabOpen = false;

    //
    private int mDictionarySize;
    private String mMode;
    private int mFavorite;
    private int mLearned;
    private String mName;
    private int mDragAndDrop;
    private String mSortedStringOfUIDS;
    //public PageID mPageID;
    private int mCheckedItemOfAlertDialog;
    private int mPickedFilterValue;
    private Boolean mDragAndDropMode;
    private int mDisplayType;
    //
    private static final String TAG = "CDF";

    public CustomDictionaryFragment() {
        // Required empty public constructor
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
        viewGroup = (ViewGroup) rootView.findViewById(R.id.coordinator_layout);
        mDictionaryCustomizer = new DictionaryCustomizer(getActivity(), mMode);
        //mDisplayType = Integer.parseInt(getFromSharedPreferences(getActivity(), mMode, DISPLAY_TYPE));
        implementView(rootView);
        implementRecyclerView(rootView);
        implementFloatingActionButton(rootView);
        implementAnimation();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //to onCreate
        mMode = getDictionaryMod();
        mDisplayType = mDictionaryCustomizer.getDisplayType();
        mSortedStringOfUIDS = getSortedStringOfUIDS();
        mItems = new ArrayList<CustomDictionaryModel>();
        mToPosIds = new ArrayList<Integer>();
        mFromPosIds = new ArrayList<Integer>();
        mFirebaseDictionaryWrapper = new FirebaseDictionaryWrapper();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mPaint = new Paint();
        //mPageID = new PageID();
        Log.i(TAG, "onStart 0: " + mMode);
        //putTestPref();
        if (User.getCurrentUser() != null) {
            getDictionarySettings();
            implementRecyclerViewAdapter();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        implementOnRecyclerScrollListener();
        implementItemTouchHelper();
        //  mDisplayType = mDictionaryCustomizer.getDisplayType();
    }

    @Override
    public void onPause() {
        super.onPause();
        resetActionMode();
    }

    private void implementRecyclerViewAdapter() {
        //mItems = getSortedList();
        DictionaryCustomizer dc = new DictionaryCustomizer(getActivity(), mMode);
        mAdapter = new CustomDictionaryAdapter(getActivity(), mMode, mItems, this, this, this, dc);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        //final ItemTouchHelperCallback itemTouchCallBack = new ItemTouchHelperCallback(getActivity(), mRecyclerView, mAdapter);
        //mItemTouchHelper = new ItemTouchHelper(itemTouchCallBack);
        //mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        //ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallBack);

                                    //                        implementItemTouchHelper();
        //  updateData();

        if (getDictionary() != null && getCurrentUser() != null) {
            //getFirebaseData();
            updateData();
        }
        mAdapter.notifyDataSetChanged();
    }

//    private void implementItemTouchHelper(){
//        SwipeHelper swipeHelper = new SwipeHelper(getActivity(), SwipeHelper.BOTH_SIDE, mRecyclerView) {
//
//            @Override
//            public void onCreateRightUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
//                UnderlayButton mDelete = new UnderlayButton(getActivity(), R.drawable.ic_action_delete);
//                if (mDictionaryCustomizer.getDisplayType() == 0)
//                    mDelete.setText("Delete");
//                mDelete.setTextColor(Color.WHITE);
//                mDelete.setBackgroundColor(Color.parseColor("#FF3C30"));
//                mDelete.setId(0);
//                mDelete.setOnClickListener(CustomDictionaryFragment.this);
//                underlayButtons.add(mDelete);
//
//
//                UnderlayButton mEdit = new UnderlayButton(getActivity(), R.drawable.ic_action_edit);
//                if (mDictionaryCustomizer.getDisplayType() == 0)
//                    mEdit.setText("Edit");
//                mEdit.setTextColor(Color.WHITE);
//                mEdit.setBackgroundColor(Color.parseColor("#FF9502"));
//                mEdit.setId(1);
//                mEdit.setOnClickListener(CustomDictionaryFragment.this);
//                underlayButtons.add(mEdit);
//            }
//
//            @Override
//            public void onCreateLeftUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
//                UnderlayButton mFavorite = new UnderlayButton(getActivity(), R.drawable.ic_action_favorite);
//                if (mDictionaryCustomizer.getDisplayType() == 0)
//                    mFavorite.setText("Favorite");
//                mFavorite.setTextColor(Color.WHITE);
//                mFavorite.setBackgroundColor(getResources().getColor(R.color.favorite));
//                mFavorite.setId(2);
//                mFavorite.setOnClickListener(CustomDictionaryFragment.this);
//                underlayButtons.add(mFavorite);
//
//                UnderlayButton mLearned = new UnderlayButton(getContext(), R.drawable.ic_action_learned);
//                if (mDictionaryCustomizer.getDisplayType() == 0)
//                    mLearned.setText("Learned");
//                mLearned.setTextColor(Color.WHITE);
//                mLearned.setBackgroundColor(Color.parseColor("#C7C7CB"));
//                mLearned.setId(3);
//                mLearned.setOnClickListener(CustomDictionaryFragment.this);
//                underlayButtons.add(mLearned);
//
//            }
//        };
//
//    }

//    private void implementItemTouchHelper() {
//        SwipeHelper swipeHelper = new SwipeHelper(getActivity(), SwipeHelper.BOTH_SIDE, mRecyclerView) {
//
//            @Override
//            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
//                UnderlayButton mDelete = new UnderlayButton(getActivity(), R.drawable.ic_action_delete);
//                if (mDictionaryCustomizer.getDisplayType() == 0)
//                    mDelete.setText("Delete");
//                mDelete.setTextColor(Color.WHITE);
//                mDelete.setBackgroundColor(Color.parseColor("#FF3C30"));
//                mDelete.setId(0);
//                mDelete.setOnClickListener(CustomDictionaryFragment.this);
//                underlayButtons.add(mDelete);
//
//
//                UnderlayButton mEdit = new UnderlayButton(getActivity(), R.drawable.ic_action_edit);
//                if (mDictionaryCustomizer.getDisplayType() == 0)
//                    mEdit.setText("Edit");
//                mEdit.setTextColor(Color.WHITE);
//                mEdit.setBackgroundColor(Color.parseColor("#FF9502"));
//                mEdit.setId(1);
//                mEdit.setOnClickListener(CustomDictionaryFragment.this);
//                underlayButtons.add(mEdit);
//            }
//        };
//    }
    private void implementItemTouchHelper() {

        mItemTouchHelper = new ItemTouchHelper(new ItemSwipeHelperCallback(getActivity(), ItemSwipeHelperCallback.BOTH_SIDE, mRecyclerView, mAdapter) {
            @Override
            public void onCreateRightUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                UnderlayButton mDelete = new UnderlayButton(getActivity(), R.drawable.ic_action_delete);
                if (mDictionaryCustomizer.getDisplayType() == 0)
                    mDelete.setText("Delete");
                mDelete.setTextColor(Color.WHITE);
                mDelete.setBackgroundColor(Color.parseColor("#FF3C30"));
                mDelete.setId(0);
                //mDelete.setOnClickListener(CustomDictionaryFragment.this);
                mDelete.setOnClickListener(CustomDictionaryFragment.this);
                underlayButtons.add(mDelete);


                UnderlayButton mEdit = new UnderlayButton(getActivity(), R.drawable.ic_action_edit);
                if (mDictionaryCustomizer.getDisplayType() == 0)
                    mEdit.setText("Edit");
                mEdit.setTextColor(Color.WHITE);
                mEdit.setBackgroundColor(Color.parseColor("#FF9502"));
                mEdit.setId(1);
                mEdit.setOnClickListener(CustomDictionaryFragment.this);
                underlayButtons.add(mEdit);
            }

            @Override
            public void onCreateLeftUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                UnderlayButton mFavorite = new UnderlayButton(getActivity(), R.drawable.ic_action_favorite);
                if (mDictionaryCustomizer.getDisplayType() == 0)
                    mFavorite.setText("Favorite");
                mFavorite.setTextColor(Color.WHITE);
                mFavorite.setBackgroundColor(getResources().getColor(R.color.favorite));
                mFavorite.setId(2);
                mFavorite.setOnClickListener(CustomDictionaryFragment.this);
                underlayButtons.add(mFavorite);

                UnderlayButton mLearned = new UnderlayButton(getContext(), R.drawable.ic_action_learned);
                if (mDictionaryCustomizer.getDisplayType() == 0)
                    mLearned.setText("Learned");
                mLearned.setTextColor(Color.WHITE);
                mLearned.setBackgroundColor(Color.parseColor("#C7C7CB"));
                mLearned.setId(3);
                mLearned.setOnClickListener(CustomDictionaryFragment.this);
                underlayButtons.add(mLearned);

            }
        });
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void implementFloatingActionButton(View rootView) {
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button);
        mFloatingActionButtonAdd = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button_add);
        mFloatingActionButtonFilter = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button_filter);
        mFloatingActionButtonSort = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button_sort);

        mFloatingActionButton.setOnClickListener(this);
        mFloatingActionButtonAdd.setOnClickListener(this);
        mFloatingActionButtonFilter.setOnClickListener(this);
        mFloatingActionButtonSort.setOnClickListener(this);
    }

    private void implementRecyclerView(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void implementView(View rootView) {
        mRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.relative_layout);
        mCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinator_layout);
        mTextView = new TextView(getActivity());
        mProgressBar = new ProgressBar(getActivity());
        mProgressBar.setId(Integer.valueOf(1));
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mDictionaryCustomizer.getDictionaryName());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        RelativeLayout.LayoutParams layoutParamsText = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsText.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParamsText.addRule(RelativeLayout.BELOW, mProgressBar.getId());

        if (getDictionary() != null) {
            mTextView.setText("DICTIONARY IS EMPTY");
        } else {
            mTextView.setText("DICTIONARY NOT SELECTED");
        }
        mTextView.setTextSize(18);

        mRelativeLayout.addView(mProgressBar, layoutParams);
        mRelativeLayout.addView(mTextView, layoutParamsText);


    }

    private void openBottomSheetDialog(){
        View view = getLayoutInflater(getArguments()).inflate(R.layout.bottom_sheet, null);
        mBottomSheetDialog = new BottomSheetDialog(getActivity());
        mBottomSheetDialog.setContentView(view);
        //TextView camera_sel = (TextView) view.findViewById(R.id.camera);
        LinearLayout lnFavorite = (LinearLayout) view.findViewById(R.id.bottom_sheet_favorite_status_container);
        LinearLayout lnLearned = (LinearLayout) view.findViewById(R.id.bottom_sheet_learned_status_container);
        LinearLayout lnResort = (LinearLayout) view.findViewById(R.id.bottom_sheet_rearrangement_container);
        mBottomSheetDragAndDropSwitch = (Switch) view.findViewById(R.id.bottom_sheet_drag_and_drop_switch);

        TextView textViewFavorite = (TextView) view.findViewById(R.id.bottom_sheet_description_of_favorite);
        TextView textViewLearned = (TextView) view.findViewById(R.id.bottom_sheet_description_of_learned);
        TextView textViewSorting = (TextView) view.findViewById(R.id.bottom_sheet_description_of_auto_rearrangement);


        textViewFavorite.setText(switchHelper(getFavorite(), R.array.hide_favorite));
        textViewLearned.setText(switchHelper(getLearned(), R.array.hide_learned));

        lnFavorite.setOnClickListener(this);
        lnLearned.setOnClickListener(this);
        lnResort.setOnClickListener(this);
        mBottomSheetDragAndDropSwitch.setOnCheckedChangeListener(this);

        TextView dictName = (TextView) view.findViewById(R.id.bottom_sheet_dictionary_name);
        dictName.setText(getName());
        dictName.setOnClickListener(this);

//        lnFavorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAdapter.notifyDataSetChanged();
//                dialog.dismiss();
//            }
//        });
        mBottomSheetDialog.show();
//        Intent addDictionary = new Intent(getActivity(), AddCustomDictionaryActivity.class);
//        startActivity(addDictionary);
    }


    private void implementAnimation() {
        mFabOpen = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        mFabClose = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        mRotateForward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_forward);
        mRotateBackward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_backward);
    }

    private void implementOnRecyclerScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mFloatingActionButton.getVisibility() == View.VISIBLE) {
                    mFloatingActionButton.hide();
                    mFloatingActionButton.setClickable(false);
                    if (isFabOpen) {
                        hideFAB();
                    }
                } else if (dy < 0 && mFloatingActionButton.getVisibility() != View.VISIBLE) {
                    mFloatingActionButton.show();
                    mFloatingActionButton.setClickable(true);

                }
            }
        });
    }

    public List<CustomDictionaryModel> updateData() {
        String path = "custom_dictionary/" + User.getCurrentUserUID() + "/dictionaries/" + getDictionary();
        mFirebaseHelper = new FirebaseHelper<>(getActivity(), path, mItems, mAdapter);

        mItems = mFirebaseHelper.getItemsList(FirebaseHelper.ORDER_BY_CHILD, new OnChildListener<CustomDictionaryModel>() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s, int index) {
                //favFilter(mItems);
                // mAdapter.setItems(mItems);
                //  mAdapter.notifyDataSetChanged();
//                //mAdapter.orderBy(getActivity(), mItems);
                //loadDictionarySetting(mItems, mFavorite);
                mTextView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                //mDictionarySize = mAdapter.getItemCount();
                mDictionarySize = mAdapter.getNotFilteredSize();
                Log.i(TAG, "onChildAdded: " + s);
                
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s, int index) {
                // mAdapter.setItems(mItems);
                //  mAdapter.notifyItemRangeChanged(0, mItems.size());
                //  mAdapter.notifyDataSetChanged();
                mTextView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
//                mDictionarySize = mAdapter.getItemCount();
                mDictionarySize = mAdapter.getNotFilteredSize();
                Log.i(TAG, "onChildChanged: ");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot, int index) {
//                CustomDictionaryModel item = mFirebaseWrapper.getCustomDictionaryWord(dataSnapshot);
//                for (int i = 0; i < mItems.size(); i++) {
//                    if (mItems.get(i).getUID().equals(item.getUID())){
//                        mAdapter.removeItem(i);
//                        break;
//                    }
//                }


                if (mItems.isEmpty()) {
                    mTextView.setVisibility(View.VISIBLE);
                } else {
                    mTextView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                }
//                mDictionarySize = mAdapter.getItemCount();
                mDictionarySize = mAdapter.getNotFilteredSize();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s, int index) {
                //mAdapter.notifyDataSetChanged();
                Log.i(TAG, "onChildMoved: ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressBar.setVisibility(View.GONE);
                Log.i(TAG, "onCancelled: ");
            }

            @Override
            public CustomDictionaryModel getItem(DataSnapshot dataSnapshot) {
                //return filter(mFirebaseDictionaryWrapper.getCustomDictionaryWord(dataSnapshot));
                return filter(mFirebaseDictionaryWrapper.getCustomDictionaryWord(dataSnapshot));
            }

            @Override
            public String getSortedString() {
                return mSortedStringOfUIDS;
            }

        });

        return mItems;
    }


    public void animateFAB() {
        if (isFabOpen) {
            hideFAB();
        } else {
            showFAB();
        }
    }

    private void hideFAB() {
        mFloatingActionButton.startAnimation(mRotateBackward);
        mFloatingActionButtonAdd.startAnimation(mFabClose);
        mFloatingActionButtonSort.startAnimation(mFabClose);
        mFloatingActionButtonFilter.startAnimation(mFabClose);
        mFloatingActionButtonAdd.setClickable(false);
        mFloatingActionButtonSort.setClickable(false);
        mFloatingActionButtonFilter.setClickable(false);
        isFabOpen = false;
    }

    private void showFAB() {
        mFloatingActionButton.startAnimation(mRotateForward);
        mFloatingActionButtonAdd.startAnimation(mFabOpen);
        mFloatingActionButtonSort.startAnimation(mFabOpen);
        mFloatingActionButtonFilter.startAnimation(mFabOpen);
        mFloatingActionButtonAdd.setClickable(true);
        mFloatingActionButtonSort.setClickable(true);
        mFloatingActionButtonFilter.setClickable(true);
        isFabOpen = true;
    }

    private void resetActionMode() {
        if (mActionMode != null) {
            mAdapter.removeSelection();
            mActionMode.finish();
            setNullToActionMode();
        }
    }

    public String getDictionary() {
        String dictionary;
        Intent intent = getActivity().getIntent();
        if (intent.getStringExtra("DictionaryName") != null) {
            dictionary = intent.getStringExtra("DictionaryName");
            return dictionary;
        }
        if (intent.getStringExtra("DictionaryName") == null && getCurrentUser() != null) {
            //dictionary = getFromSharedPreferences(getActivity(), "DefaultDictionaryName", getUserUID());
            dictionary = getFromSharedPreferences(getActivity(), mMode, DICTIONARY);
            return dictionary;
        } else {
            return null;
        }
    }

    private String getDictionaryMod(){
        String dictMod = null;
        if (this.getArguments() != null){
            dictMod = getArguments().getString("DictionaryMode");
        }
        if (dictMod == null) {
            dictMod = MAIN_GLOBAL_SETTINGS;
        }
        return dictMod;
    }

    /*
    *   Dictionary mode
     */

    private void getDictionarySettings(){
        getLearned();
        getFavorite();
        getName();
        getDragAndDropMode();

//        mFavorite = Integer.parseInt(SharedPreferencesUtils.getFromSharedPreferences(getActivity(), mMode,"FAVORITE"));
//        mLearned = Integer.parseInt(SharedPreferencesUtils.getFromSharedPreferences(getActivity(), mMode,"LEARNED"));
//        mName = SharedPreferencesUtils.getFromSharedPreferences(getActivity(), mMode,"CUSTOM_NAME");
        Log.i(TAG, "getDictionarySettings: " +
                mFavorite + " : " +
                mLearned  + " : " +
                mName);
    }

    public String getMode() {
        return mMode;
    }

    public Boolean getDragAndDropMode() {
        mDragAndDropMode = Boolean.valueOf(SharedPreferencesUtils.getFromSharedPreferences(getActivity(), mMode, DRAG_AND_DROP));
        return mDragAndDropMode;
    }

    public String getName() {
        mName = SharedPreferencesUtils.getFromSharedPreferences(getActivity(), mMode,"CUSTOM_NAME");
        return mName;
    }

    public void setName(String name) {
        SharedPreferencesUtils.putToSharedPreferences(getActivity(), mMode,"CUSTOM_NAME",name);
        mName = name;
    }

    public int getFavorite() {
        mFavorite = Integer.parseInt(SharedPreferencesUtils.getFromSharedPreferences(getActivity(), mMode,"FAVORITE"));
        return mFavorite;
    }

    public int getLearned() {
       // SharedPreferencesUtils.putToSharedPreferences(getActivity(), mMode,"LEARNED", String.valueOf(1));

        mLearned = Integer.parseInt(SharedPreferencesUtils.getFromSharedPreferences(getActivity(), mMode,"LEARNED"));
        Log.i(TAG, "getLearned: " + mLearned);
        return mLearned;
    }

    public void setMode(String mode) {
        SharedPreferencesUtils.putToSharedPreferences(getActivity(), mMode,"CUSTOM_NAME", mode);
        mMode = mode;
    }

    public void setFavorite(int favorite) {
        SharedPreferencesUtils.putToSharedPreferences(getActivity(), mMode,"FAVORITE", String.valueOf(favorite));
        Log.i(TAG, "setFavorite: " + favorite + " :Str: " + String.valueOf(favorite));
        mFavorite = favorite;
    }

    public void setLearned(int learned) {
        SharedPreferencesUtils.putToSharedPreferences(getActivity(), mMode,"LEARNED", String.valueOf(learned));
        Log.i(TAG, "setLearned: " + learned + " :Str: " + String.valueOf(learned));
        mLearned = learned;
    }

    private CustomDictionaryModel filter(CustomDictionaryModel item){
        CustomDictionaryModel filteredItem = null;
//        switch (mMode){
//            case CUSTOM_DICTIONARY_PAGE_1:
//                filteredItem = learnedItemFilter(item);
//                filteredItem = favItemFilter(filteredItem);
//                return filteredItem;
          //  case CUSTOM_DICTIONARY_PAGE_2:

                filteredItem = learnedItemFilter(item);
                if (filteredItem != null)
                filteredItem = favItemFilter(filteredItem);
                return filteredItem;
        //}
       // return null;
    }


    private CustomDictionaryModel learnedItemFilter (CustomDictionaryModel item){
        Log.i(TAG, "favItemFilter: " + getFavorite());
        switch (getLearned()){
            case 0:
                Log.i(TAG, "learnedItemFilter: 0 : " + item.getUID() + " : " + item.getStatus() );
                //if (item.getStatus() || !item.getStatus())
                return item;
                //break;
            case 1:
                Log.i(TAG, "learnedItemFilter: 1 : " + item.getUID() + " : " + item.getStatus() );
                if (item.getStatus())
                    return item;
                break;
            case 2:
                Log.i(TAG, "learnedItemFilter: 2 : " + item.getUID() + " : " + item.getStatus() );
                if (!item.getStatus())
                    return item;
                break;
        }
        return null;
    }


    private CustomDictionaryModel favItemFilter (CustomDictionaryModel item){
        Log.i(TAG, "favItemFilter: " + getFavorite());
        switch (getFavorite()){
            case 0:
                Log.i(TAG, "favItemFilter: 0 : " + item.getUID() + " : " + item.getFavorite() );
                //if (item.getFavorite() || !item.getFavorite())
                return item;
                //break;
            case 1:
                Log.i(TAG, "favItemFilter: 3 : " + item.getUID() + " : " + item.getFavorite() );
                if (item.getFavorite())
                    return item;
                break;
            case 2:
                Log.i(TAG, "favItemFilter: 2 : " + item.getUID() + " : " + item.getFavorite() );
                if (!item.getFavorite())
                    return item;
                break;
        }
        return null;
    }

    private String getSortedStringOfUIDS (){
        mSortedStringOfUIDS = SharedPreferencesUtils.getFromSharedPreferences(getActivity(), mMode, "STRING_OF_SORTED_UID");
        if (mSortedStringOfUIDS == null){
            mSortedStringOfUIDS = "";
        }
        return mSortedStringOfUIDS;
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
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new CDFToolbarActionMode(getActivity(), mAdapter, this));
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
//                Intent settings = new Intent(getActivity(), Preference.CustomDictionaryPreference.class);
//                startActivity(settings);
                Intent settings = new Intent(getActivity(), MainPreferenceActivity.class);
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
        addWordIntent.putExtra("DictionaryName", getFromSharedPreferences(getActivity(), mMode, DictionaryCustomizer.DICTIONARY));
        //addWordIntent.putExtra("DictionaryName", getFromSharedPreferences(getActivity(), "DefaultDictionaryName", getUserUID()));
        addWordIntent.putExtra("DictionarySize", mDictionarySize);
        addWordIntent.putExtra("Mod", "AddMOD");
        startActivity(addWordIntent);
    }

    private String switchHelper(int value, int arrayRes){
        String[] arrayStr = getResources().getStringArray(arrayRes);
        switch (value){
            case 0:
                return arrayStr[0];
            case 1:
                return arrayStr[1];
            case 2:
                return arrayStr[2];
        }
        return null;
    }

    private void actionSort(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        Switch sw = new Switch(getActivity());
//        sw.setTextOn("start");
//        sw.setTextOff("close");
//        sw.setGravity(Gravity.CENTER_HORIZONTAL);
//        builder.setTitle(R.string.sorting_elements)
//                .setItems(R.array.order_entries, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                        SP.edit().putString("orderBy", String.valueOf(which)).commit();
//                    }
//                });
//        builder.show();

        Intent dictionaryCustomizerIntent = new Intent(getActivity(), DictionaryCustomizerActivity.class);
        dictionaryCustomizerIntent.putExtra(DictionaryCustomizerActivity.DICTIONARY_PAGE, mMode);
        dictionaryCustomizerIntent.putExtra(DictionaryCustomizerActivity.DICTIONARY, getDictionary());
        startActivity(dictionaryCustomizerIntent);

    }

    public void itemSelect(View view, int position) {
        mAdapter.toggleSelection(position);
        boolean hasCheckedItems = mAdapter.getSelectedCount() > 0;
        if (hasCheckedItems && mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new CDFToolbarActionMode(getActivity(), mAdapter, this));
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
        changeIntent.putExtra("DictionarySize", mDictionarySize);
        changeIntent.putExtra("Mod", "EditMod");
        startActivity(changeIntent);
    }

    private void editItem(int position){
        Intent changeIntent = new Intent(getActivity(), BaseDetailActivity.class);
        changeIntent.putExtra("WordUID", mItems.get(position).getUID());
        changeIntent.putExtra("DictionaryName", getDictionary());
        changeIntent.putExtra("DictionarySize", mDictionarySize);
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
        Log.i(TAG, "deleteItem: " + position + " : " + item.getId() + " : " + item.getUID() + " : " + item.getWord());
        mDatabaseReference.child("custom_dictionary")
                .child(getUserUID())
                .child("dictionaries")
                .child(getDictionary())
                .child(item.getUID()).removeValue();

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mDatabaseReference
                        .child("custom_dictionary")
                        .child(getUserUID())
                        .child("dictionaries")
                        .child(getDictionary())
                        .updateChildren(mAdapter.updateId());
            }
        });
        mThread.start();

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

            // Floating Action Buttons
            case R.id.floating_action_button:
                animateFAB();
                break;
            case R.id.floating_action_button_add:
                actionAdd();
                break;
            case R.id.floating_action_button_filter:
                openBottomSheetDialog();
                animateFAB();
                break;
            case R.id.floating_action_button_sort:
                actionSort();
                animateFAB();
                break;

           // Bottom Sheet Dialog
            case R.id.bottom_sheet_favorite_status_container:
                mAlertDialogBuilder = new AlertDialog.Builder(getActivity());
                mAlertDialogBuilder.setTitle(R.string.favorite_title);

                mCheckedItemOfAlertDialog = getFavorite();
                mAlertDialogBuilder.setSingleChoiceItems(R.array.hide_favorite, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPickedFilterValue = which;
                    }
                });
                mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setFavorite(mPickedFilterValue);
                        mAdapter.clear();
                        updateData();
                    }
                });
                mAlertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
                mAlertDialog = mAlertDialogBuilder.create();
                mAlertDialog.show();
                break;
            case R.id.bottom_sheet_learned_status_container:
                AlertDialog.Builder adbL = new AlertDialog.Builder(getActivity());
                adbL.setTitle(R.string.learned_title);

                mCheckedItemOfAlertDialog = getLearned();
                adbL.setSingleChoiceItems(R.array.hide_learned, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPickedFilterValue = which;
                    }
                });
                adbL.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setLearned(mPickedFilterValue);
                         mAdapter.clear();
                         updateData();
                    }
                });
                adbL.setNegativeButton(getString(R.string.cancel), null);
                AlertDialog mAlertDialog = adbL.create();
                mAlertDialog.show();

//                mAdapter.notifyDataSetChanged();
//                mBottomSheetDialog.cancel();
                break;
            case R.id.bottom_sheet_rearrangement_container:
                mAdapter.notifyDataSetChanged();
                mBottomSheetDialog.cancel();
                break;
            case R.id.bottom_sheet_dictionary_name:
                final EditText dictNameET = new EditText(getActivity());

                mAlertDialogBuilder = new AlertDialog.Builder(getActivity());
                mAlertDialogBuilder.setTitle(R.string.enter_name_of_dictionary);
                mAlertDialogBuilder.setView(dictNameET);
                mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setName(dictNameET.getText().toString());
                    }
                });
                mAlertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
                mAlertDialogBuilder.show();
                break;
        }
    }

    @Override
    public boolean onMove(int fromPosition, int toPosition) {
        mToPosIds.add(toPosition);
        mFromPosIds.add(fromPosition);
          //  mAdapter.clearSortedString();
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        //if (direction == ItemTouchHelper.LEFT){
         //   mAdapter.removeItem(position);
        //} else {

        //}
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

    }

    @Override
    public void onItemMoveComplete(RecyclerView.ViewHolder viewHolder) {
        Gson gson = new Gson();
        String sortedJson = gson.toJson(mAdapter.getSortedString());
        SharedPreferencesUtils.putToSharedPreferences(getActivity(), mMode, "STRING_OF_SORTED_UID", sortedJson);
        Log.i(TAG, "onMove: " + sortedJson + " : " + mAdapter.getSortedString());
                    //        mThread = new Thread(new Runnable() {
                    //            @Override
                    //            public void run() {
                    //                Map<String, Object> updateMap = mAdapter.getMovedItems();
                    //                mDatabaseReference
                    //                        .child("custom_dictionary")
                    //                        .child(getUserUID())
                    //                        .child("dictionaries")
                    //                        .child(getDictionary())
                    //                        .updateChildren(updateMap);
                    //                mAdapter.clearMoved();
                    //            }
                    //        });
                    //        mThread.setDaemon(true);
                    //        mThread.start();
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
            //intent.putExtra("Word", mItems.get(position).getWord());
            intent.putExtra("WordUID", mItems.get(position).getUID());
            intent.putExtra("DictionaryName", getDictionary());
            intent.putExtra("DictionarySize", mDictionarySize);
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
        switch (mDictionaryCustomizer.getOptionsMenu()){
            case 0:
                itemSelect(view, position);
                break;
            case 1:
                popupMenu(view, position);
                break;
        }
        return false;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void popupMenu(final View view, final int position){
        final String path = "custom_dictionary/" + User.getCurrentUserUID() + "/dictionaries/" + getDictionary();
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
                                mAdapter.removeItem(position);
                                //mAdapter.updateId(false);
                               // mDatabaseReference.child(path).setValue(mAdapter.getIds());

                            }
                        });
                        adb.setNegativeButton("No", null);
                        adb.show();
                        break;
                    case R.id.popup_menu_select:
                        itemSelect(view, position);
                        break;
                    case R.id.popup_menu_move_to:
                        final CustomDictionaryModel item0 = mItems.get(position);
                        Log.i(TAG, "deleteItem: " + position + " : " + item0.getId() + " : " + item0.getUID() + " : " + item0.getWord());

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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.bottom_sheet_drag_and_drop_switch:
                mBottomSheetDragAndDropSwitch.setChecked(getDragAndDropMode());
                mDictionaryCustomizer.setDragAndDrop(isChecked);
                //mAdapter.setDragAndDropMode(isChecked);


                //mAdapter.notifyDataSetChanged();
                //mBottomSheetDialog.cancel();
                break;
        }
    }

    @Override
    public void onClickUnderlayButton(int pos, int id) {
        switch (id){
            case 0:
               // deleteItem(pos);
               // mAdapter.removeItem(pos);
                Toast.makeText(getActivity(), "DELETE", Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(getActivity(), "EDIT", Toast.LENGTH_LONG).show();

             //   editItem(pos);
                break;
            case 2:
                Toast.makeText(getActivity(), "FAV", Toast.LENGTH_LONG).show();

                //changeFavoriteStatus(pos);
                break;
            case 3:
                Toast.makeText(getActivity(), "LEARNED", Toast.LENGTH_LONG).show();

                //changeLearnedStatus(pos);
                break;
        }
    }
}
