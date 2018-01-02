package com.fallenangel.linmea._linmea.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import com.fallenangel.linmea._linmea.model.DictionaryCustomizer;
import com.fallenangel.linmea._linmea.ui.dictionary.CustomDictionaryFragment;
import com.fallenangel.linmea._linmea.ui.dictionary.CustomDictionaryListFragment;
import com.fallenangel.linmea._linmea.ui.dictionary.MainDictionaryFragment;
import com.fallenangel.linmea._linmea.ui.society.FriendsFragment;
import com.fallenangel.linmea._linmea.ui.translator.TranslatorFragment;
import com.fallenangel.linmea._test.TestFragment;
import com.fallenangel.linmea.authentication.LoginActivity;
import com.fallenangel.linmea.authentication.SignUpActivity;
import com.fallenangel.linmea.interfaces.OnFABClickListener;
import com.fallenangel.linmea.linmea.user.authentication.User;
import com.fallenangel.linmea.linmea.user.profile.ProfileActivity;
import com.fallenangel.linmea.linmea.utils.image.Blur;
import com.fallenangel.linmea.linmea.utils.image.ImageFactoryUtils;
import com.fallenangel.linmea.utils.CheckPermissionReadExternalStorage;
import com.fallenangel.linmea.utils.CheckPermissionWriteExternalStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

    // PageID mPageID = new PageID();
    private Boolean userStatus;
    private String mPageId;

    public static final String HOME_PAGE = "HOME_PAGE";
    public static final String MAIN_DICTIONARY = "MAIN_DICTIONARY";
    public static final String CUSTOM_DICTIONARY = "CUSTOM_DICTIONARY";
    public static final String SHARED_DICTIONARY = "SHARED_DICTIONARY";
    public static final String SOCIETY = "SOCIETY";

    public static final String HOME_PAGE_1 = "HOME_PAGE_1";

    public static final String MAIN_DICTIONARY_PAGE_1 = "MAIN_DICTIONARY_PAGE_1";
    public static final String MAIN_DICTIONARY_PAGE_2 = "MAIN_DICTIONARY_PAGE_2";
    public static final String MAIN_DICTIONARY_PAGE_3 = "MAIN_DICTIONARY_PAGE_3";
    public static final String CUSTOM_DICTIONARY_PAGE_1 = "CUSTOM_DICTIONARY_PAGE_1";
    public static final String CUSTOM_DICTIONARY_PAGE_2 = "CUSTOM_DICTIONARY_PAGE_2";
    public static final String CUSTOM_DICTIONARY_LIST = "CUSTOM_DICTIONARY_LIST";
    public static final String SHARED_DICTIONARY_PAGE_1 = "SHARED_DICTIONARY_PAGE_1";
    public static final String SHARED_DICTIONARY_PAGE_2 = "SHARED_DICTIONARY_PAGE_2";
    public static final String SOCIETY_FRIENDS_LIST = "SOCIETY_FRIENDS_LIST";
    public static final String SOCIETY_GROUP_LIST = "SOCIETY_GROUP_LIST";



    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        implementUI();
        implementNavDrawerHeader();

        if (User.getCurrentUser() != null){
            Log.i("user", "onCreate: user true");
        }    else        Log.i("user", "onCreate: user false");

        setDefaultScreen(CustomDictionaryFragment.class, null, CUSTOM_DICTIONARY_PAGE_1, CUSTOM_DICTIONARY);
//
//        try {
//            parsingTxt();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
            //mainDictToLogFromFire();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            Blur.applyBlur(headerView, MainActivity.this);


        avatarImg = (ImageView) headerView.findViewById(R.id.nv_header_image_view);
        logOut = (ImageView) headerView.findViewById(R.id.log_out_header_image_view);
        signUpTVheader = (TextView) headerView.findViewById(R.id.sign_up_header_text_view);
        orTVheader = (TextView) headerView.findViewById(R.id.or_header_text_view);
        signInTVheader = (TextView) headerView.findViewById(R.id.sign_in_text_view_header);
        email = (TextView) headerView.findViewById(R.id.email_header_text_view);

        Bitmap defAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
        avatarImg.setImageBitmap(ImageFactoryUtils.getCircleBitmap(defAvatar, 100));

        if(getCurrentUser() != null){
            userStatus = true;
            signUpTVheader.setText(User.getCurrentUser().getDisplayName());
            email.setText(User.getCurrentUser().getEmail());
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

        File image = getImageFileFromCache(MainActivity.this, User.getCurrentUserUID());
        Log.i("trash", "getAvatarImage: " + image);
        if (image != null){
                    try {
                        imageStream = getContentResolver().openInputStream(Uri.fromFile(image));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    avatarImg.setImageBitmap(ImageFactoryUtils.getCircleBitmap(bitmap, 100));
        } else {
            Bitmap defAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
            avatarImg.setImageBitmap(ImageFactoryUtils.getCircleBitmap(defAvatar, 100));
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

//        TextView pageOne = (TextView) mBottomNavigationView.findViewById(R.id.custom_dictionary_page_1_bottom_bar).findViewById(R.id.largeLabel);
//        pageOne.setText(DictionaryCustomizer.getDictionaryName(this, DictionaryCustomizer.CUSTOM_DICTIONARY_PAGE_1));

//        TextView pageTwo = (TextView) mBottomNavigationView.findViewById(R.id.custom_dictionary_page_2_bottom_bar).findViewById(R.id.largeLabel);
//        pageTwo.setText(DictionaryCustomizer.getDictionaryName(this, DictionaryCustomizer.CUSTOM_DICTIONARY_PAGE_2));

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
//                fragmentCommit(TestFragment.class, null, menuItem, MAIN_DICTIONARY_PAGE_1);
                fragmentCommit(MainDictionaryFragment.class, null, menuItem, MAIN_DICTIONARY_PAGE_1, MAIN_DICTIONARY);
//                mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
                mBottomNavigationView.setVisibility(View.GONE);
                break;
            case R.id.custom_dictionary_item_nd:
                //fragmentCommit(CustomDictionaryFragment.class, null, menuItem, CUSTOM_DICTIONARY_PAGE_1);
                fragmentCommit(CustomDictionaryFragment.class, null, menuItem, CUSTOM_DICTIONARY_PAGE_1, CUSTOM_DICTIONARY);
//                mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
//                mBottomNavigationView.inflateMenu(R.menu.bottom_action_bar);
                mBottomNavigationView.setVisibility(View.VISIBLE);
//                mBottomNavigationView.invalidate();



//                TextView pageOne = (TextView) mBottomNavigationView.findViewById(R.id.custom_dictionary_page_1_bottom_bar).findViewById(R.id.largeLabel);
//                pageOne.setText(DictionaryCustomizer.getDictionaryName(this, DictionaryCustomizer.CUSTOM_DICTIONARY_PAGE_1));
//
//                TextView pageTwo = (TextView) mBottomNavigationView.findViewById(R.id.custom_dictionary_page_2_bottom_bar).findViewById(R.id.largeLabel);
//                pageTwo.setText(DictionaryCustomizer.getDictionaryName(this, DictionaryCustomizer.CUSTOM_DICTIONARY_PAGE_2));

//                mBottomNavigationView.setOnNavigationItemSelectedListener(this);
                break;
            case R.id.shared_dictionaries_item_nd:
                //fragmentCommit(TestFragment.class, null, menuItem, SHARED_DICTIONARY_PAGE_1);
                fragmentCommit(TranslatorFragment.class, null, menuItem, SHARED_DICTIONARY_PAGE_1, SHARED_DICTIONARY);
                mBottomNavigationView.setVisibility(View.VISIBLE);
                break;
            case R.id.society_item_nd:
                //fragmentCommit(FriendsFragment.class, null, menuItem, SOCIETY_FRIENDS_LIST);
                fragmentCommit(FriendsFragment.class, null, menuItem, SOCIETY_FRIENDS_LIST, SOCIETY);
                mBottomNavigationView.setVisibility(View.VISIBLE);

                break;
            default:
               // fragmentCommit(TestFragment.class, null, menuItem, HOME_PAGE_1);
                fragmentCommit(TestFragment.class, null, menuItem, HOME_PAGE_1, HOME_PAGE);
                break;
        }
    }

    private void fragmentCommit (Class fragmentClass, Fragment fragment, MenuItem menuItem, String homeFragmentId, String pageId){
        //mPageID.setPageID(homeFragmentId);
        mPageId = pageId;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(createBundle(homeFragmentId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();





        menuItem.setChecked(true);
        //setTitle(menuItem.getTitle());
//        setTitle(DictionaryCustomizer.getDictionaryName(this, homeFragmentId));
//
//        TextView pageTitleOne = (TextView) mBottomNavigationView.findViewById(R.id.custom_dictionary_page_1_bottom_bar).findViewById(R.id.largeLabel);
//        //mBottomNavigationView.findViewById(R.homeFragmentId.custom_dictionary_page_1_bottom_bar).findViewById(R.homeFragmentId.largeLabel);
//        pageTitleOne.setText(DictionaryCustomizer.getDictionaryName(this, DictionaryCustomizer.CUSTOM_DICTIONARY_PAGE_1));
//
//        TextView pageTitleTwo = (TextView) mBottomNavigationView.findViewById(R.id.custom_dictionary_page_2_bottom_bar).findViewById(R.id.largeLabel);
//                //mBottomNavigationView.findViewById(R.homeFragmentId.custom_dictionary_page_1_bottom_bar).findViewById(R.homeFragmentId.largeLabel);
//        pageTitleTwo.setText(DictionaryCustomizer.getDictionaryName(this, DictionaryCustomizer.CUSTOM_DICTIONARY_PAGE_2));

        mDrawer.closeDrawers();

    }

    public String getPageId() {
        return mPageId;
    }

    private void setDefaultScreen (Class fragmentClass, Fragment fragment, String homeFragmentId, String pageId){
        //mPageID.setPageID(id);
        mPageId = pageId;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(createBundle(homeFragmentId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();
        setTitle(DictionaryCustomizer.getDictionaryName(this, homeFragmentId));






//        TextView pageTitleOne = (TextView) mBottomNavigationView.findViewById(R.id.custom_dictionary_page_1_bottom_bar).findViewById(R.id.largeLabel);
//        //mBottomNavigationView.findViewById(R.id.custom_dictionary_page_1_bottom_bar).findViewById(R.id.largeLabel);
//        pageTitleOne.setText(DictionaryCustomizer.getDictionaryName(this, DictionaryCustomizer.CUSTOM_DICTIONARY_PAGE_1));
//
//        TextView pageTitleTwo = (TextView) mBottomNavigationView.findViewById(R.id.custom_dictionary_page_2_bottom_bar).findViewById(R.id.largeLabel);
//        //mBottomNavigationView.findViewById(R.id.custom_dictionary_page_1_bottom_bar).findViewById(R.id.largeLabel);
//        pageTitleTwo.setText(DictionaryCustomizer.getDictionaryName(this, DictionaryCustomizer.CUSTOM_DICTIONARY_PAGE_2));
    }

    public static Bundle createBundle(String mode){
        Bundle bundle = new Bundle();
        bundle.putString("DictionaryMode", mode);
        return bundle;
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
            case R.id.custom_dictionary_page_1_bottom_bar:
                switch (getPageId()){
                    case HOME_PAGE:
                        break;
                    case MAIN_DICTIONARY:
                        break;
                    case CUSTOM_DICTIONARY:
                        fragmentCommit(CustomDictionaryFragment.class, null, item, CUSTOM_DICTIONARY_PAGE_1, CUSTOM_DICTIONARY);
                        break;
                    case SHARED_DICTIONARY:
                        break;
                    case SOCIETY:
                        fragmentCommit(FriendsFragment.class, null, item, SOCIETY_FRIENDS_LIST, SOCIETY);
                        break;
                    default:
                        break;
                }
                //SharedPreferencesUtils.putToSharedPreferences(this, "customDict", "dictionaryMode", CUSTOM_DICTIONARY_PAGE_1);
                break;
            case R.id.custom_dictionary_page_2_bottom_bar:
                switch (getPageId()) {
                    case HOME_PAGE:
                        break;
                    case MAIN_DICTIONARY:
                        break;
                    case CUSTOM_DICTIONARY:
                        fragmentCommit(CustomDictionaryFragment.class, null, item, CUSTOM_DICTIONARY_PAGE_2, CUSTOM_DICTIONARY);
                        break;
                    case SHARED_DICTIONARY:
                        break;
                    case SOCIETY:
                        break;
                    default:
                        break;
                }
                // SharedPreferencesUtils.putToSharedPreferences(this, "customDict", "dictionaryMode", CUSTOM_DICTIONARY_PAGE_2);
                break;
            case R.id.custom_dictionary_list_bottom_bar:
                switch (getPageId()){
                    case HOME_PAGE:
                        break;
                    case MAIN_DICTIONARY:
                        break;
                    case CUSTOM_DICTIONARY:
                        fragmentCommit(CustomDictionaryListFragment.class, null, item, CUSTOM_DICTIONARY_LIST, CUSTOM_DICTIONARY);
                        break;
                    case SHARED_DICTIONARY:
                        break;
                    case SOCIETY:
                        break;
                    default:
                        break;
                }
                //TextView textView = (TextView) mBottomNavigationView.findViewById(R.id.custom_dictionary_page_1_bottom_bar).findViewById(R.id.largeLabel);
                //textView.setText(DictionaryCustomizer.getDictionaryName(this, CUSTOM_DICTIONARY_PAGE_1));
                //SharedPreferencesUtils.putToSharedPreferences(this, "customDict", "dictionaryMode", CUSTOM_DICTIONARY_LIST);
                break;
            default:
                //fragmentCommit(CustomDictionaryFragment.class, null, null, CUSTOM_DICTIONARY_PAGE_1);
                //SharedPreferencesUtils.putToSharedPreferences(this, "customDict", "dictionaryMode", CUSTOM_DICTIONARY_PAGE_1);
                break;
        }
        return false;
    }












    private void parsingTxt() throws IOException {

//        FileReader input = new FileReader("c:\\rsen.txt");
      //  BufferedReader bufRead = new BufferedReader(input);
      //  String myLine = null;


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                123);







        String myLine2 = "Заратуштра:Zoroaster\n" +
                "Зороастр:Zoroaster\n" +
                "а:and, while\n" +
                "а:non, a\n" +
                "абажур:lampshade\n" +
                "абак:abacus\n" +
                "аббат:abbot\n" +
                "аббатиса:abbess\n" +
                "аббатство:abbacy";

        int count = 0;
       // File f = new File("rsen.txt");




        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "art.txt";
// Not sure if the / is on the path or not





        CheckPermissionReadExternalStorage checkPermissionReadExternalStorage = new CheckPermissionReadExternalStorage();
        CheckPermissionWriteExternalStorage checkPermissionWriteExternalStorage = new CheckPermissionWriteExternalStorage();
        if (checkPermissionReadExternalStorage.checkReadPermission(this) && checkPermissionWriteExternalStorage.checkWritePermission(this)) {

            File f = new File(baseDir + File.separator + fileName);


            Log.i(TAG, "parsingTxt: " + f);
            Scanner sc = new Scanner(f);

            List<miniCusomDictModel> dictionary = new ArrayList<>();

            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] details = line.split(" - ");
                String wordC = details[0].trim();
                String translation = details[1].trim();
            //    if (translation.contains(", ")){
                String[] translation2 = translation.split(", ");
                miniCusomDictModel mcdm = new miniCusomDictModel();
                ArrayList<String> al = new ArrayList<>();
                mcdm.setWord(wordC);

                for (int i = 0; i < translation2.length; i++) {
                        al.add(translation2[i]);
                        mcdm.setTranslation(al);

                        //Log.i(TAG, "parsingTxt: " + detailWord[i]);
                        //String word = detailWord[i];
                        //MainDictionaryModel dictItem = new MainDictionaryModel(word, translation);

                    }
                dictionary.add(mcdm);
                count++;

//                } else {
//                    miniCusomDictModel mcdm = new miniCusomDictModel();
//                    ArrayList<String> al = new ArrayList<>();
//                    al.add(translation);
//                    mcdm.setWord(wordC);
//                    mcdm.setTranslation(al);

                    //Log.i(TAG, "parsingTxt: " + detailWord[i]);
                    //String word = detailWord[i];
                    //MainDictionaryModel dictItem = new MainDictionaryModel(word, translation);
              //      dictionary.add(mcdm);
               //     count++;
//                    String word = details[1];
//                    MainDictionaryModel dictItem = new MainDictionaryModel(word, translation);
//                    dictionary.add(dictItem);
//                    count++;
           //     }


//                MainDictionaryModel dictItem = new MainDictionaryModel(wordC, translation);
//                dictionary.add(dictItem);
//                count++;
            }
            for(miniCusomDictModel d: dictionary){
                String key =  FirebaseDatabase.getInstance().getReference().child("custom_dictionary/musd33qW0sdE7paVwLLRvOUB7Q43/dictionaries/art/").push().getKey();
                Map<String, String> m = new HashMap<>();
                m.put("word",  d.getWord());
                FirebaseDatabase.getInstance().getReference().child("custom_dictionary/musd33qW0sdE7paVwLLRvOUB7Q43/dictionaries/art/" + key +"/").setValue(m);
                FirebaseDatabase.getInstance().getReference().child("custom_dictionary/musd33qW0sdE7paVwLLRvOUB7Q43/dictionaries/art/" + key + "/translation/").setValue(d.getTranslation());
                Log.i(TAG, "parsingTxt: " + d.getWord() + " :?: " + d.getTranslation());
            }
            Log.i(TAG, "parsingTxt counter: " + count);

        }else {
            // no permission
            Log.i(TAG,getString(R.string.no_permission));

        }



//        for (int i = 0; i < myLine2.length(); i++) {
//            String[] array1 = myLine2.split("\\n");
//            // check to make sure you have valid data
//            String[] array2 = array1[1].split(":");
//            for (int j = 0; j < array2.length; j++)
//            Log.i(TAG, "parsingTxt: " + array2[j]);
//        }

//        while ( (myLine = myLine2) != null)
//        {
//           // String[] array1 = myLine.split(":");
//            String[] array1 = myLine.split("\\n");
//            // check to make sure you have valid data
//            String[] array2 = array1[1].split(":");
//            for (int i = 0; i < array2.length; i++)
//                Log.i(TAG, "parsingTxt: " + array2[i]);
//
//              //  function(array1[0], array2[i]);
//        }

    }
    private class miniCusomDictModel2{
        private String mWord;

        public String getWord() {
            return mWord;
        }

        public void setWord(String word) {
            mWord = word;
        }

    }

    private class miniCusomDictModel{
        private String mWord;
        private ArrayList<String> mTranslation;

        public String getWord() {
            return mWord;
        }

        public void setWord(String word) {
            mWord = word;
        }

        public ArrayList<String> getTranslation() {
            return mTranslation;
        }

        public void setTranslation(ArrayList<String> translation) {
            mTranslation = translation;
        }
    }

    private void mainDictToLogFromFire() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final int[] itemsCounter = {0};
                FirebaseDatabase.getInstance().getReference().child("main_dictionary/main/").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String word = (String) dataSnapshot.child("word").getValue();
                        String translation = (String) dataSnapshot.child("translation").getValue();
                        itemsCounter[0]++;
                        Log.i(TAG, "Item from Main Dictionary: " + "id[" +  itemsCounter[0] + "], Word: " + word + ", Translation: " + translation);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        thread.start();
    }
}
