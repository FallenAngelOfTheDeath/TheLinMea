package com.fallenangel.linmea._modulus.grammar.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.grammar.adapter.FullGramAdapter;
import com.fallenangel.linmea._modulus.grammar.db.GrammarTDBHelperForSet;
import com.fallenangel.linmea._modulus.grammar.model.FavoriteGrammarWrapper;
import com.fallenangel.linmea._modulus.grammar.model.FullGrammarModel;
import com.fallenangel.linmea._modulus.grammar.model.GrammarCategory;
import com.fallenangel.linmea._modulus.grammar.model.GrammarCategoryWrapper;
import com.fallenangel.linmea._modulus.grammar.model.GrammarListWrapper;
import com.fallenangel.linmea._modulus.grammar.model.GrammarsList;
import com.fallenangel.linmea._modulus.grammar.model.UserDataGrammar;
import com.fallenangel.linmea._modulus.grammar.model.UserDataGrammarWrapper;
import com.fallenangel.linmea._modulus.non.interfaces.OnSimpleChildEventListener;
import com.fallenangel.linmea._modulus.non.view.GridAutofitLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by NineB on 1/18/2018.
 */

public class OnlyFavoriteGrammarFragment extends Fragment implements OnRecyclerViewClickListener {

    private String TAG = "OnlyFavoriteGrammar";

    private RecyclerView mRecyclerView;

    private PopupMenu mPopupMenu;

    private FullGramAdapter mAdapter;

    private GrammarTDBHelperForSet<GrammarCategory> gramCategoryLoader;
    private Set<GrammarCategory> mCategories = new HashSet<>();
    private GrammarCategoryWrapper categoryWrapper = new GrammarCategoryWrapper();


    private GrammarTDBHelperForSet<GrammarsList> grammarLoader;
    private Set<GrammarsList> mGrammarList = new HashSet<>();
    private GrammarListWrapper grammarListWrapper = new GrammarListWrapper();

    private GrammarTDBHelperForSet<UserDataGrammar> userDataLoader;
    private Set<UserDataGrammar> mUserData = new HashSet<>();
    private UserDataGrammarWrapper userDataWrapper = new UserDataGrammarWrapper();


    //private Set<FullGrammarModel> fullGrammar = new HashSet<>();


    private List<GrammarsList> mItems = new ArrayList<>();
    private FavoriteGrammarWrapper favoriteGrammarWrapper = new FavoriteGrammarWrapper();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadGrammarCategories();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.recycler_view, container, false);

        implementUI(rootView);


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void implementUI(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_ans);
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActivity(), 300, true));

        mAdapter = new FullGramAdapter(getActivity(), combineData(mGrammarList, mUserData), this);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadGrammarCategories( ){
        String path = "/grammar/categories/";
        gramCategoryLoader = new GrammarTDBHelperForSet<GrammarCategory>(mCategories, new OnSimpleChildEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, " mCategories-M.size: " + mCategories.size() + "________________________________________");
                for (GrammarCategory category:mCategories) {
                    Log.i(TAG, "Categories-M: " + category.getCategory());
                    loadGrammarsByCategory(category.getCategory());
                    loadUserDataByCategory(category.getCategory());
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public Object getItem(DataSnapshot dataSnapshot) {
                return categoryWrapper.getCategory(dataSnapshot);
            }
        });
        gramCategoryLoader.loadData(path);
    }

    private void loadGrammarsByCategory(String category){
        String path = "/grammar/categories/" + category;
        grammarLoader = new GrammarTDBHelperForSet<GrammarsList>(mGrammarList, new OnSimpleChildEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "GrammarList_Categories:_________________________________________");
                for (GrammarsList gram:mGrammarList) {
                    Log.i(TAG, "GrammarList_Categories: " + gram.getGrammarCategory());
                    Log.i(TAG, "GrammarList_gram_name: " + gram.getGrammarName());
                    //combineData();
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public Object getItem(DataSnapshot dataSnapshot) {
                return grammarListWrapper.getGrammar(dataSnapshot, category);
            }
        });
        grammarLoader.loadData(path);
    }

    private void loadUserDataByCategory(String category){
        String path = "/grammar/categories/";
        userDataLoader = new GrammarTDBHelperForSet<UserDataGrammar>(mUserData, new OnSimpleChildEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public Object getItem(DataSnapshot dataSnapshot) {
                return userDataWrapper.getUserDataGrammar(dataSnapshot);
            }
        });
        userDataLoader.loadData(path + category);
    }


    private Set<FullGrammarModel> combineData(Set<GrammarsList> mGrammarList,  Set<UserDataGrammar> mUserData){
        FullGrammarModel gram = new FullGrammarModel();
        Set<FullGrammarModel> fullGrammar = new HashSet<>();
        List<GrammarsList> gList = new ArrayList<>();
        List<UserDataGrammar> udList = new ArrayList<>();

        gList.addAll(mGrammarList);
        udList.addAll(mUserData);

        for (int i = 0; i < gList.size(); i++) {
            gram.setCategory(gList.get(i).getGrammarCategory());
            gram.setGrammar(gList.get(i).getGrammarName());
            for (int j = 0; j < mUserData.size(); j++) {
                if (gList.get(i).getGrammarName().equals(udList.get(j).getGrammarName())){
                    gram.setFavorite(udList.get(j).getFavorite());
                    gram.setLearned(udList.get(j).getLearned());
                }
            }
//            Boolean fav = gram.getFavorite();
//            if (fav == null) fav = false;
//            if (fav != false)
            fullGrammar.add(gram);
            //mAdapter.notifyDataSetChanged();
        }
        Log.i(TAG, "combineData:______________________________________________");
        for (FullGrammarModel item:fullGrammar) {
            Log.i(TAG, "combineData: " + item.toString());
        }
        Log.i(TAG, "size: " + fullGrammar.size());
        return fullGrammar;
    }



    @Override
    public void onItemClicked(View view, int position) {

    }

    @Override
    public void onOptionsClicked(View view, int position) {

    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        return false;
    }
}
