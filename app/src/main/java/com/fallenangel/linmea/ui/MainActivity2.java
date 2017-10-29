package com.fallenangel.linmea.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.adapters.ViewPagerAdapter;
import com.fallenangel.linmea.dictionaries.activity.AddCustomDictionaryActivity;
import com.fallenangel.linmea.linmea.ui.dictionary.fragment.CustomDictionaryListFragment;
import com.fallenangel.linmea.linmea.ui.dictionary.fragment.CustomDictionaryFragment;
import com.fallenangel.linmea.dictionaries.fragment.TestFragment;
import com.fallenangel.linmea.firebase.AddWordCustomDictionaryActivity;
import com.fallenangel.linmea.model.PageID;

import static com.fallenangel.linmea.profile.UserMetaData.getUserUID;
import static com.fallenangel.linmea.linmea.user.utils.SharedPreferencesUtils.getFromSharedPreferences;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private NavigationView mNavDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private FrameLayout mContainer;
    private FloatingActionButton mFloatingActionButton;

    private ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


    public PageID mPageID = new PageID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        implementUI();

        setDefaultScreen(CustomDictionaryFragment.class, null, View.VISIBLE, View.VISIBLE, View.GONE, "custom_dictionary");
    }

    public void implementUI (){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

       // mFloatingActionButton = (FloatingActionButton) findViewById(R.id.main_floating_action_button);
       // mFloatingActionButton.setOnClickListener(this);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(mNavDrawer);

        mDrawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(mDrawerToggle);

        mContainer = (FrameLayout) findViewById(R.id.main_container);

       // mViewPager = (ViewPager) findViewById(R.id.viewpager);
       // setupViewPager(mViewPager);

       // mTabLayout = (TabLayout) findViewById(R.id.tabs_bar);
      //  mTabLayout.setupWithViewPager(mViewPager);

        setupTabIcons();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.main_dictionaries_item_nd:
                fragmentCommit(TestFragment.class, null, menuItem, View.GONE, View.GONE, View.VISIBLE, "main_dictionaries");
                break;
            case R.id.custom_dictionary_item_nd:
                fragmentCommit(CustomDictionaryFragment.class, null, menuItem, View.VISIBLE, View.VISIBLE, View.GONE, "custom_dictionary");
                break;
            case R.id.shared_dictionaries_item_nd:
                fragmentCommit(TestFragment.class, null, menuItem, View.GONE, View.GONE, View.VISIBLE, "shared_dictionaries");
                break;
            default:
                fragmentCommit(TestFragment.class, null, menuItem, View.GONE, View.GONE, View.VISIBLE, "default");
                break;
        }
    }

    private void fragmentCommit (Class fragmentClass,Fragment fragment, MenuItem menuItem,
                                 int visibilityTab, int visibilityPager, int visibilityContent, String id){
        mPageID.setPageID(id);

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();

        mTabLayout.setVisibility(visibilityTab);
        mViewPager.setVisibility(visibilityPager);
        mContainer.setVisibility(visibilityContent);
    }

    private void setDefaultScreen (Class fragmentClass,Fragment fragment,
                                 int visibilityTab, int visibilityPager, int visibilityContent, String id){
        mPageID.setPageID(id);

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();
        mTabLayout.setVisibility(visibilityTab);
        mViewPager.setVisibility(visibilityPager);
        mContainer.setVisibility(visibilityContent);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new CustomDictionaryFragment(), getResources().getString(R.string.default_custom_dictionary));
        adapter.addFragment(new CustomDictionaryListFragment(), getResources().getString(R.string.custom_dictionary_list));
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText(R.string.default_custom_dictionary);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pets_black_24dp, 0, 0, 0);
        mTabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(R.string.custom_dictionary_list);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pets_black_24dp, 0, 0, 0);
        mTabLayout.getTabAt(1).setCustomView(tabTwo);
    }

    @Override
    public void onClick(View v) {
        switch (mPageID.getPageID()){
            case "main_dictionaries":
                Log.i("trash", "onClick: " + mPageID.getPageID());
                break;
            case "custom_dictionary":
                Log.i("trash", "onClick: " + mPageID.getPageID());
                switch (mTabLayout.getSelectedTabPosition()){
                    case 0:
                        Intent addWordIntent = new Intent(this, AddWordCustomDictionaryActivity.class);
                        addWordIntent.putExtra("DictionaryName", getFromSharedPreferences(this, "DefaultDictionaryName", getUserUID()));
                        startActivity(addWordIntent);
                    Log.i("trash", "onClick: " + mTabLayout.getSelectedTabPosition());
                        break;
                    case 1:
                        Intent addDictionary = new Intent(this, AddCustomDictionaryActivity.class);
                        startActivity(addDictionary);
                    Log.i("trash", "onClick: " + mTabLayout.getSelectedTabPosition());
                        break;
                }
                break;
            case "shared_dictionaries":
                Log.i("trash", "onClick: " + mPageID.getPageID());
                break;
        }
    }
}
