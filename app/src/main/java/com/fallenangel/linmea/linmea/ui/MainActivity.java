package com.fallenangel.linmea.linmea.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.authentication.LoginActivity;
import com.fallenangel.linmea.authentication.SignUpActivity;
import com.fallenangel.linmea.dictionaries.fragment.TestFragment;
import com.fallenangel.linmea.interfaces.OnFABClickListener;
import com.fallenangel.linmea.linmea.ui.dictionary.fragment.CustomDictionaryFragment;
import com.fallenangel.linmea.linmea.ui.dictionary.fragment.CustomDictionaryListFragment;
import com.fallenangel.linmea.linmea.user.authentication.user;
import com.fallenangel.linmea.linmea.user.profile.ProfileActivity;
import com.fallenangel.linmea.linmea.user.utils.SharedPreferencesUtils;
import com.fallenangel.linmea.linmea.utils.image.Blur;
import com.fallenangel.linmea.linmea.utils.image.ImageFactoryUtils;
import com.fallenangel.linmea.model.PageID;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.fallenangel.linmea.linmea.utils.image.ImageFileUtils.getImageFileFromCache;
import static com.fallenangel.linmea.profile.UserMetaData.getCurrentUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, OnFABClickListener {

    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private NavigationView mNavDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private BottomNavigationView mBottomNavigationView;

    private ImageView avatarImg, logOut;
    private TextView signUpTVheader, orTVheader, signInTVheader, email;

    private FrameLayout mContainer;

    public PageID mPageID = new PageID();
    private Boolean userStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        implementUI();
        implementNavDrawerHeader();

        if (user.getCurrentUser() != null){
            Log.i("user", "onCreate: user true");
        }    else        Log.i("user", "onCreate: user false");

        setDefaultScreen(CustomDictionaryFragment.class, null, "custom_dictionary");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void implementNavDrawerHeader () {
        //getting header view
        View headerView = mNavDrawer.getHeaderView(0);
        //set background
        headerView.setBackgroundResource(R.drawable.header_02);
        //set bluer for background
        Blur.applyBlur(headerView, MainActivity.this);


        avatarImg = (ImageView) headerView.findViewById(R.id.nv_header_image_view);
        logOut = (ImageView) headerView.findViewById(R.id.log_out_header_image_view);
        signUpTVheader = (TextView) headerView.findViewById(R.id.sign_up_header_text_view);
        orTVheader = (TextView) headerView.findViewById(R.id.or_header_text_view);
        signInTVheader = (TextView) headerView.findViewById(R.id.sign_in_text_view_header);
        email = (TextView) headerView.findViewById(R.id.email_header_text_view);

        Bitmap defAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
        avatarImg.setImageBitmap(ImageFactoryUtils.getCircleBitmap(defAvatar, 300));

        if(getCurrentUser() != null){
            userStatus = true;
            signUpTVheader.setText(user.getCurrentUser().getDisplayName());
            email.setText(user.getCurrentUser().getEmail());
            signInTVheader.setVisibility(View.GONE);
            orTVheader.setVisibility(View.GONE);

            getAvatarImage(avatarImg);
        } else {
            userStatus = false;
            signUpTVheader.setText(getString(R.string.singup));
            signInTVheader.setText(getString(R.string.singin));
            email.setVisibility(View.GONE);
            logOut.setVisibility(View.GONE);
        }

        avatarImg.setOnClickListener(this);
        signInTVheader.setOnClickListener(this);
        signUpTVheader.setOnClickListener(this);
        logOut.setOnClickListener(this);
    }

    private void getAvatarImage(final ImageView avatarImg){
        InputStream imageStream = null;

        File image = getImageFileFromCache(MainActivity.this, com.fallenangel.linmea.linmea.user.authentication.user.getCurrentUserUID());
        Log.i("trash", "getAvatarImage: " + image);
        if (image != null){
                    try {
                        imageStream = getContentResolver().openInputStream(Uri.fromFile(image));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    avatarImg.setImageBitmap(ImageFactoryUtils.getCircleBitmap(bitmap, 300));
        } else {
            Bitmap defAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
            avatarImg.setImageBitmap(ImageFactoryUtils.getCircleBitmap(defAvatar, 300));
        }
    }



    private void implementUI (){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavDrawer = (NavigationView) findViewById(R.id.nvView);
        mContainer = (FrameLayout) findViewById(R.id.main_container);

        setSupportActionBar(mToolbar);
        setupDrawerContent(mNavDrawer);
        mDrawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(mDrawerToggle);

        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
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
                fragmentCommit(TestFragment.class, null, menuItem, "main_dictionaries");
                break;
            case R.id.custom_dictionary_item_nd:
                fragmentCommit(CustomDictionaryFragment.class, null, menuItem,"custom_dictionary");
                break;
            case R.id.shared_dictionaries_item_nd:
                fragmentCommit(TestFragment.class, null, menuItem, "shared_dictionaries");
                break;
            default:
                fragmentCommit(TestFragment.class, null, menuItem, "default");
                break;
        }
    }

    private void fragmentCommit (Class fragmentClass, Fragment fragment, MenuItem menuItem, String id){
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

    }

    private void setDefaultScreen (Class fragmentClass,Fragment fragment, String id){
        mPageID.setPageID(id);
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open,  R.string.drawer_close);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //--------------------------------------------------------------------------------------
            case R.id.nv_header_image_view:
                if (userStatus == true){
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(v.getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.sign_in_text_view_header:
                if (userStatus == false){
                    Intent intent = new Intent(v.getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.sign_up_header_text_view:
                if (userStatus == true){
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(v.getContext(), SignUpActivity.class);
                    startActivity(intent);
                }
                break;
            //--------------------------------------------------------------------------------------
            case R.id.log_out_header_image_view:
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Log OUT");
                adb.setMessage("you are sure?");
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userStatus = false;

                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signOut();

                        Bitmap defAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
                        avatarImg.setImageBitmap(ImageFactoryUtils.getCircleBitmap(defAvatar, 300));

                        logOut.setVisibility(View.GONE);
                        signUpTVheader.setText(R.string.singup);
                        orTVheader.setVisibility(View.VISIBLE);
                        signInTVheader.setVisibility(View.VISIBLE);
                        signInTVheader.setText(R.string.singin);
                        email.setVisibility(View.GONE);
                    }
                });
                adb.setNegativeButton("No", null);
                adb.show();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.custom_dictionary_bottom_bar:
                fragmentCommit(CustomDictionaryFragment.class, null, item, "custom_dictionary");
                SharedPreferencesUtils.putToSharedPreferences(this, "customDict", "dictionaryMode", "0");
                break;
            case R.id.all_dictionaries_bottom_bar:
                fragmentCommit(CustomDictionaryListFragment.class, null, item, "list_of_all_custom_dictionaries");
                break;
            case R.id.favorite_words_bottom_bar:
                fragmentCommit(CustomDictionaryFragment.class, null, item, "custom_dictionary_favorite");
                SharedPreferencesUtils.putToSharedPreferences(this, "customDict", "dictionaryMode", "favOnly");
                break;
            default:
                fragmentCommit(CustomDictionaryFragment.class, null, null, "custom_dictionary_bottom");
                SharedPreferencesUtils.putToSharedPreferences(this, "customDict", "dictionaryMode", "0");
                break;
        }
        return false;
    }
}
