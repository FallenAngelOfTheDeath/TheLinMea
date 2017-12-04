package com.fallenangel.linmea._linmea.ui.dictionary;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.adapter.DictionariesAdapter;
import com.fallenangel.linmea._linmea.adapter.ItemSwipeHelperCallback;
import com.fallenangel.linmea._linmea.data.firebase.FirebaseDictionaryWrapper;
import com.fallenangel.linmea._linmea.data.firebase.FirebaseHelper;
import com.fallenangel.linmea._linmea.interfaces.OnChildListener;
import com.fallenangel.linmea._linmea.model.MyDictionaryModel;
import com.fallenangel.linmea._linmea.view.UnderlayButton;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea.linmea.user.authentication.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.fallenangel.linmea._linmea.util.SharedPreferencesUtils.putToSharedPreferences;
import static com.fallenangel.linmea.profile.UserMetaData.getUserUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomDictionaryListFragment extends Fragment implements OnRecyclerViewClickListener, View.OnClickListener {


    private static final String TAG = "CDLF";
    //Helper
    private ItemTouchHelper mItemTouchHelper;
    private FirebaseHelper<MyDictionaryModel> mFirebaseHelper;


    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private DictionariesAdapter mAdapter;

    private List<MyDictionaryModel> mItems = new ArrayList<>();
    private FirebaseDictionaryWrapper mFirebaseDictionaryWrapper = new FirebaseDictionaryWrapper();


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    public CustomDictionaryListFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom_dictionary_list, container, false);
        implementUI(rootView);
        implementRecyclerView(rootView);
        implementRecyclerViewAdapter();
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
    }

    //------------------------------------------------------------------------------------------

    private void implementUI (View rootView){
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button);
        mFloatingActionButton.setOnClickListener(this);
    }

    private void implementRecyclerView(View rootView){
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void implementRecyclerViewAdapter(){
        mAdapter = new DictionariesAdapter(getActivity(), mItems, this, null, null);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        implementItemTouchHelper();
        if (User.getCurrentUser() != null){
            implementRecyclerViewData();
        }
        mAdapter.notifyDataSetChanged();
    }

    private void implementItemTouchHelper () {
        mItemTouchHelper = new ItemTouchHelper(new ItemSwipeHelperCallback(getActivity(), ItemSwipeHelperCallback.BOTH_SIDE, mRecyclerView, mAdapter) {
            @Override
            public void onCreateRightUnderlayButton(final RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                UnderlayButton mDelete = new UnderlayButton(getActivity(), R.drawable.ic_action_delete);
                mDelete.setText("Delete");
                mDelete.setTextColor(Color.WHITE);
                mDelete.setBackgroundColor(Color.parseColor("#FF3C30"));
                //mDelete.setImageResId(getActivity().getResources(), R.drawable.ic_delete);
//                mDelete.setOnClickListener(new UnderlayButtonClickListener() {
//                    @Override
//                    public void onClick(int pos) {
//                        deleteDictionary(pos);
//                    }
//                });
                underlayButtons.add(mDelete);


                UnderlayButton mEdit = new UnderlayButton(getActivity(), R.drawable.ic_action_delete);
                mEdit.setText("Edit");
                mEdit.setTextColor(Color.WHITE);
                mEdit.setBackgroundColor(Color.parseColor("#FF9502"));
                //mEdit.setSide(UnderlayButton.RIGHT);
//                mEdit.setOnClickListener(new UnderlayButtonClickListener() {
//                    @Override
//                    public void onClick(int pos) {
//
//                    }
//                });
                underlayButtons.add(mEdit);


            }

            @Override
            public void onCreateLeftUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                UnderlayButton mFavorite = new UnderlayButton(getActivity(), R.drawable.ic_action_delete);
                mFavorite.setText("as default");
                mFavorite.setTextColor(Color.WHITE);
                mFavorite.setBackgroundColor(getResources().getColor(R.color.favorite));
                //mFavorite.setSide(UnderlayButton.LEFT);
//                mFavorite.setOnClickListener(new UnderlayButtonClickListener() {
//                    @Override
//                    public void onClick(int pos) {
//                        setAsDefaultDict(pos);
//                    }
//                });
                underlayButtons.add(mFavorite);
            }
        });
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }


    private void implementOnRecyclerScrollListener(){
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mFloatingActionButton.getVisibility() == View.VISIBLE) {
                    mFloatingActionButton.hide();
                    mFloatingActionButton.setClickable(false);
                } else if (dy < 0 && mFloatingActionButton.getVisibility() != View.VISIBLE) {
                    mFloatingActionButton.show();
                    mFloatingActionButton.setClickable(true);

                }
            }
        });
    }

    private void implementRecyclerViewData () {
        String path = "custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/";
        mFirebaseHelper = new FirebaseHelper<>(getActivity(), path, mItems, mAdapter);

        mItems = mFirebaseHelper.getItemsList(FirebaseHelper.ORDER_BY_KEY, new OnChildListener<MyDictionaryModel>() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s, int index) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s, int index) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot, int index) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s, int index) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public MyDictionaryModel getItem(DataSnapshot dataSnapshot) {
                return mFirebaseDictionaryWrapper.getDictionaryItem(dataSnapshot);
            }

            @Override
            public String getSortedString() {
                return null;
            }
        });
        Log.i(TAG, "implementRecyclerViewData: " + mItems);
    }


//        databaseReference.child("custom_dictionary")
//                         .child(UserMetaData.getUserUID())
//                         .child("meta_data")
//                         .orderByKey().addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                MyDictionaryModel item = mFirebaseWrapper.getDictionaryItem(dataSnapshot);
//
//                mDictionary.add(item);
//                mAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                MyDictionaryModel item = mFirebaseWrapper.getDictionaryItem(dataSnapshot);
//
//                int index = getItemIndex(item, mDictionary);
//                mDictionary.set(index, item);
//                mAdapter.notifyItemRangeChanged(0, mDictionary.size());
//                mAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                MyDictionaryModel item = mFirebaseWrapper.getDictionaryItem(dataSnapshot);
//
//                int index = getItemIndex(item, mDictionary);
//                mDictionary.remove(index);
//                mAdapter.notifyItemRangeChanged(0, mDictionary.size());
//                mAdapter.notifyItemRemoved(index);
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    //}


    //------------------------------------------------------------------------------------------



    private int getItemIndex (MyDictionaryModel model, ArrayList<MyDictionaryModel> items){
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(model.getName())){
                index = i;
                break;
            }
        }
        return index;
    }

    public void updateUI (ArrayList<MyDictionaryModel> mItems) {
        if (mAdapter == null) {
            mAdapter = new DictionariesAdapter(getActivity(), mItems, this, null, null);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setItems(mItems, "");
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClicked(View view, int position) {
        final MyDictionaryModel items = mAdapter.getItems(position);
        Intent userDictionaryIntent = new Intent(getActivity(), DictionaryActivity.class);
        userDictionaryIntent.putExtra("DictionaryName", items.getName());
        startActivity(userDictionaryIntent);
    }


    @Override
    public void onOptionsClicked(View view, final int position) {
        popupMenu(position, view);
    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        Toast.makeText(getActivity(),"On Item Long Clicked " + position, Toast.LENGTH_LONG).show();
        return false;
    }


    @Override
    public void onClick(View v) {
        Intent addDictionary = new Intent(v.getContext(), AddCustomDictionaryActivity.class);
        startActivity(addDictionary);
    }

    public void popupMenu (final int position, final View view){
        //final CustomDictionaryModel itemList = mAdapter.getItemList(position);
        final PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.custom_dictionary_list_popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.popup_menu_edit:
                        Toast.makeText(getActivity(), "popup menu/editor", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.popup_menu_delete:
                        deleteDictionary(position);
                        break;
                    case R.id.popup_menu_share:
                        Toast.makeText(getActivity(), "popup menu/share", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.popup_menu_default_dict:
                        setAsDefaultDict(position);
                        break;

                }
                return true;
            }

        });
        popupMenu.show();
    }

    private void deleteDictionary(final int position){
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Delete");
        adb.setMessage("you are sure that you want to remove this a word, it can't be cancelled");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child("custom_dictionary")
                        .child(getUserUID())
                        .child("meta_data")
                        .child(mItems.get(position).getName())
                        .removeValue();
                databaseReference.child("custom_dictionary")
                        .child(getUserUID())
                        .child("dictionaries")
                        .child(mItems.get(position).getName())
                        .removeValue();
            }
        });
        adb.setNegativeButton("No", null);
        adb.show();
    }

    private void setAsDefaultDict (final int position) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Set dictionary as default");
        adb.setMessage("you are want to set dictionary as default");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final MyDictionaryModel items = mAdapter.getItems(position);
                putToSharedPreferences(getActivity(), "DefaultDictionaryName", getUserUID(), items.getName());
                Toast.makeText(getActivity(), items.getName() + " has been saved as default", Toast.LENGTH_LONG).show();
            }
        });
        adb.setNegativeButton("No", null);
        adb.show();
    }


}
