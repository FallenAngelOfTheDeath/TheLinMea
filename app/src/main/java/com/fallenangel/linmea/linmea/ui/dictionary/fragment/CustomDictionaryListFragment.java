package com.fallenangel.linmea.linmea.ui.dictionary.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.adapters.MyDictionariesAdapter;
import com.fallenangel.linmea.database.FirebaseWrapper;
import com.fallenangel.linmea.dictionaries.activity.DictionaryActivity;
import com.fallenangel.linmea.firebase.FBhelper;
import com.fallenangel.linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea.linmea.user.authentication.user;
import com.fallenangel.linmea.model.MyDictionaryModel;
import com.fallenangel.linmea.profile.UserMetaData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.fallenangel.linmea.linmea.user.utils.SharedPreferencesUtils.putToSharedPreferences;
import static com.fallenangel.linmea.profile.UserMetaData.getUserUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomDictionaryListFragment extends Fragment implements OnRecyclerViewClickListener {
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private MyDictionariesAdapter mAdapter;

    private FBhelper mFireBaseHelper;

    ArrayList<MyDictionaryModel> mDictionary = new ArrayList<MyDictionaryModel>();
    private FirebaseWrapper mFirebaseWrapper = new FirebaseWrapper();


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    public CustomDictionaryListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom_dictionary_list, container, false);

        implementUI(rootView);
      //  if (user.hasLoggedIn(mAuthStateListener) == true){
          //  implementRecyclerViewData();

      //  }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter = new MyDictionariesAdapter(getActivity(), mDictionary, this, null, null);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        if (user.getCurrentUser() != null){
            implementRecyclerViewData();
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
       // updateUI(mDictionary);
      //  mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
      //  updateUI(mDictionary);
       // mAdapter.notifyDataSetChanged();
    }

    private void implementUI (View rootView){
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.custom_dictionary_floating_action_button);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.custom_dictionary_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void implementRecyclerViewData () {

        databaseReference.child("custom_dictionary")
                         .child(UserMetaData.getUserUID())
                         .child("meta_data")
                         .orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyDictionaryModel item = mFirebaseWrapper.getDictionaryItem(dataSnapshot);

                mDictionary.add(item);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                MyDictionaryModel item = mFirebaseWrapper.getDictionaryItem(dataSnapshot);

                int index = getItemIndex(item, mDictionary);
                mDictionary.set(index, item);
                mAdapter.notifyItemRangeChanged(0, mDictionary.size());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                MyDictionaryModel item = mFirebaseWrapper.getDictionaryItem(dataSnapshot);

                int index = getItemIndex(item, mDictionary);
                mDictionary.remove(index);
                mAdapter.notifyItemRangeChanged(0, mDictionary.size());
                mAdapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




//
//        mFireBaseHelper = new FBhelper(databaseReference, mDictionary);
//
//        if (mAdapter == null) {
//            mAdapter = new MyDictionariesAdapter(getActivity(), mFireBaseHelper.getFirebaseData(new OnChildListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//               }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            }), this);
//            mRecyclerView.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();
//        } else {
//            mAdapter.setItems(mFireBaseHelper.getFirebaseData(null));
//            mAdapter.notifyDataSetChanged();
//        }
    }

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
            mAdapter = new MyDictionariesAdapter(getActivity(), mItems, this, null, null);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setItems(mItems);
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
                        popupMenuDeleteDictionary(position);
                        break;
                    case R.id.popup_menu_share:
                        Toast.makeText(getActivity(), "popup menu/share", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.popup_menu_default_dict:
                        popupMenuDefaultDict(position);
                        break;

                }
                return true;
            }

        });
        popupMenu.show();
    }

    private void popupMenuDeleteDictionary(final int position){
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Delete");
        adb.setMessage("you are sure that you want to remove this a word, it can't be cancelled");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child("custom_dictionary")
                        .child(getUserUID())
                        .child("meta_data")
                        .child(mDictionary.get(position).getName())
                        .removeValue();
                databaseReference.child("custom_dictionary")
                        .child(getUserUID())
                        .child("dictionaries")
                        .child(mDictionary.get(position).getName())
                        .removeValue();
            }
        });
        adb.setNegativeButton("No", null);
        adb.show();
    }

    private void popupMenuDefaultDict (final int position) {
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
