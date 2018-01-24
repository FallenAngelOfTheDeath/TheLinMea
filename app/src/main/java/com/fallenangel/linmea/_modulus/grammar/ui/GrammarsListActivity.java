package com.fallenangel.linmea._modulus.grammar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.grammar.adapter.GrammarsListAdapter;
import com.fallenangel.linmea._modulus.grammar.db.GrammarTDBHelper;
import com.fallenangel.linmea._modulus.grammar.model.GrammarListWrapper;
import com.fallenangel.linmea._modulus.grammar.model.GrammarsList;
import com.fallenangel.linmea._modulus.grammar.model.UserDataGrammar;
import com.fallenangel.linmea._modulus.grammar.model.UserDataGrammarWrapper;
import com.fallenangel.linmea._modulus.non.Constant;
import com.fallenangel.linmea._modulus.non.interfaces.OnSimpleChildEventListener;
import com.fallenangel.linmea._modulus.non.view.GridAutofitLayoutManager;
import com.fallenangel.linmea._modulus.auth.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class GrammarsListActivity extends AppCompatActivity implements OnRecyclerViewClickListener, OnSimpleChildEventListener, View.OnClickListener {

    public static final String TAG = "GrammarsListActivity";


    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;

    private PopupMenu mPopupMenu;

    private GrammarsListAdapter mAdapter;

    private List<GrammarsList> mItems = new ArrayList<>();
    private List<UserDataGrammar> mUserData = new ArrayList<>();

    private GrammarTDBHelper<GrammarsList> dbHelper;
    private GrammarTDBHelper<UserDataGrammar> userDataDBHelper;

    private GrammarListWrapper wrapper = new GrammarListWrapper();
    private UserDataGrammarWrapper userDataWrapper = new UserDataGrammarWrapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammars_list);
        implementUI();
        loadGrammars();
        loadUserData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        implementAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();
        dbHelper.removeChildEventListener();
    }

    private String getGrammarCategory(){
        Intent intent = getIntent();
        return intent.getStringExtra(Constant.GRAMMAR_CATEGORY);
    }

    private void implementUI(){

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setTitle(getGrammarCategory());
        setSupportActionBar(mToolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_ans);
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(this, 300, true));
    }

    private void implementAdapter(){
        mAdapter = new GrammarsListAdapter(this, mItems, mUserData, this);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadGrammars() {
        String path = "/grammar/categories/" + getGrammarCategory();
        dbHelper = new GrammarTDBHelper<GrammarsList>(mItems, this);
        dbHelper.loadData(path);
        dbHelper.keepSynced(true);
    }



    private void loadUserData(){
        String path = "/grammar/user_data/" + User.getCurrentUserUID() + "/" + getGrammarCategory();
        userDataDBHelper = new GrammarTDBHelper<UserDataGrammar>(mUserData, new OnSimpleChildEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDataGrammar item = userDataWrapper.getUserDataGrammar(dataSnapshot);

                if (Constant.DEBUG == 1) Log.i(TAG, "onDataChange: " + item.getFavorite());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mAdapter.notifyItemRangeChanged(0, mItems.size());
                mAdapter.notifyDataSetChanged();

                //mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public UserDataGrammar getItem(DataSnapshot dataSnapshot) {
                return userDataWrapper.getUserDataGrammar(dataSnapshot);
            }
        });
        userDataDBHelper.loadData(path);
        userDataDBHelper.keepSynced(true);
    }


    @Override
    public void onItemClicked(View view, int position) {
        openDetailView(view, position);
    }

    @Override
    public void onOptionsClicked(View view, int position) {
        openPopupMenu(view, position);
    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        return false;
    }



    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public Object getItem(DataSnapshot dataSnapshot) {
        return wrapper.getGrammar(dataSnapshot, getGrammarCategory());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case -1:
                finish();
                break;
        }
    }


    public void changeLearned (final int position){
        String to = User.getCurrentUserUID() + "/" + getGrammarCategory() + "/" + mItems.get(position).getGrammarName();
        if (mUserData.isEmpty()) {
            UserDataGrammar.changeLearned(to, true);
        } else {
            Boolean learned = mUserData.get(position).getLearned();
            if (learned == null)
                learned = false;
            if (learned){
                UserDataGrammar.changeLearned(to, false);
            } else {
                UserDataGrammar.changeLearned(to, true);
            }
        }
    }

    public void changeFavorite (final int position){
        String to = User.getCurrentUserUID() + "/" + getGrammarCategory() + "/" + mItems.get(position).getGrammarName();
        if (mUserData.isEmpty()) {
            UserDataGrammar.changeLearned(to, true);
        } else {
            Boolean favorite = (mUserData.get(position).getFavorite());
            if (favorite == null)
                favorite = false;
            if (favorite){
                UserDataGrammar.changeFavorite(to, false);
            } else {
                UserDataGrammar.changeFavorite(to, true);
            }
        }
    }


    private void openDetailView(View view, int position) {
        for (UserDataGrammar item:mUserData) {
            Log.i(TAG, "openDetailView: " + item.getFavorite() + " : size = " + mUserData.size());
        }
    }

    private void openPopupMenu(final View view, final int position){
        mPopupMenu = new PopupMenu(this, view);
        mPopupMenu.inflate(R.menu.popum_grammar);
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.popup_detail_view:
                        openDetailView(view, position);
                        break;
                    case R.id.popup_favorite:
                        changeFavorite(position);
                        break;
                    case R.id.popup_learned:
                        changeLearned(position);
                        break;
                }
                return false;
            }
        });
        mPopupMenu.show();
    }

}
