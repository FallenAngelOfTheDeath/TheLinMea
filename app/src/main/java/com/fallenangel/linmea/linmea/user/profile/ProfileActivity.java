package com.fallenangel.linmea.linmea.user.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.adapters.ViewPagerAdapter;
import com.fallenangel.linmea.linmea.user.authentication.user;
import com.fallenangel.linmea.linmea.user.utils.SharedPreferencesUtils;
import com.fallenangel.linmea.linmea.utils.image.Blur;
import com.fallenangel.linmea.linmea.utils.image.ImageFactoryUtils;
import com.fallenangel.linmea.profile.fragment.FirstPageProfileFragment;
import com.fallenangel.linmea.profile.fragment.SecondPageProfileFragment;
import com.fallenangel.linmea.profile.fragment.ThirdPageProfileFragment;
import com.fallenangel.linmea.profile.utils.GetCircleImageAsyncTask;
import com.fallenangel.linmea.profile.utils.ImageAsyncTask;
import com.fallenangel.linmea.utils.CheckPermissionNetwork;
import com.fallenangel.linmea.utils.CheckPermissionReadExternalStorage;
import com.fallenangel.linmea.utils.CheckPermissionWriteExternalStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import static com.fallenangel.linmea.linmea.utils.image.ImageFileUtils.getImageFileFromCache;
import static com.fallenangel.linmea.profile.UserMetaData.getUserUID;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ImageView mAvatarImageView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private FloatingActionButton mFABProfile;
    private StorageReference mStorageReference;
    private SharedPreferences mAvatarUriPreferences;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference reference = mFirebaseStorage.getReference();

    private SharedPreferencesUtils mSharedPreferencesUtils = new SharedPreferencesUtils();


    private CheckPermissionReadExternalStorage checkPermissionReadExternalStorage = new CheckPermissionReadExternalStorage();
    private CheckPermissionWriteExternalStorage checkPermissionWriteExternalStorage = new CheckPermissionWriteExternalStorage();
    private CheckPermissionNetwork checkPermissionNetwork = new CheckPermissionNetwork();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (user.getCurrentUser() != null){
            implementUI();

        }
    }



    private void implementUI () {
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mActionBarToolbar.setTitle(user.getCurrentUser().getDisplayName());
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAvatarImageView = (ImageView)findViewById(R.id.profile_avatar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout_profile);

        mFABProfile = (FloatingActionButton)findViewById(R.id.fab_profile);
        mFABProfile.setOnClickListener(this);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager_profile);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_dots_profile);
        tabLayout.setupWithViewPager(mViewPager, true);
        mViewPager.addOnPageChangeListener(this);



        getAvatarImage(mAvatarImageView);

        Blur.applyBlur(mCollapsingToolbarLayout, ProfileActivity.this);

        setupViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FirstPageProfileFragment(), null);
        adapter.addFragment(new SecondPageProfileFragment(), null);
        adapter.addFragment(new ThirdPageProfileFragment(), null);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        switch (position){
            case 0:
                break;
            case 1:
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out_profile:
                user.logOut();
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAvatarImage(ImageView avatarImg){
        InputStream imageStream = null;

        File image = getImageFileFromCache(this, user.getCurrentUserUID());

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

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.fab_profile:
                Intent intentEdit = new Intent(this, ChangeProfileDataActivity.class);
                startActivity(intentEdit);
                break;

            default:
                break;
        }
    }

































    private void userData (FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri avatarUrl = user.getPhotoUrl();



//            ImageAsyncTask.DownloadImageAsyncTask downloadImageAsyncTask = new ImageAsyncTask.DownloadImageAsyncTask();
//            downloadImageAsyncTask.execute(this, user.getUid());
                                        //            Uri imgUri = null;
                                        //            try {
                                        //                imgUri = (Uri) downloadImageAsyncTask.get();
                                        //            } catch (InterruptedException e) {
                                        //                e.printStackTrace();
                                        //            } catch (ExecutionException e) {
                                        //                e.printStackTrace();
                                        //            }
                                        //
                                        //            mAvatarImageView.setImageURI(imgUri);

            //downloadAvatar(user);

            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();
                                                                    //            mDataCreatedTextView.setText("Avatar URL: " + String.valueOf(avatarUrl));
                                                                    //            mNameTV.setText(name);
                                                                    //            mEmailTV.setText(email);
                                                                    //            mUidTV.setText(uid);
                                                                    //            mEmailVerificationTV.setText(String.valueOf(emailVerified));

            //setImage();

            //mAvatarUriPreferences = getPreferences(MODE_PRIVATE);
            //String savedUri = mAvatarUriPreferences.getString(user.getUid(), "");

            //mAvatarImageView.setImageURI(Uri.parse(savedUri));
        }
    }

    private void setImage () {


        if (checkPermissionReadExternalStorage.checkReadPermission(this) && checkPermissionWriteExternalStorage.checkWritePermission(this) && checkPermissionNetwork.checkNetworkPermission(this)) {
            String savedMD5 = mSharedPreferencesUtils.getFromSharedPreferences(this,"MD5",user.getCurrentUserUID());
            String fireMD5 = getFireMD5(user.getCurrentUserUID());

            Bitmap bitmap = null;

            if (savedMD5 == fireMD5) {

                //get bitmap image from external storage

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImageAsyncTask.getImageFromExternalStorage(getUserUID()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                GetCircleImageAsyncTask getCircleImageAsyncTask = new GetCircleImageAsyncTask();
                getCircleImageAsyncTask.execute(bitmap, 300);

                try {
                    bitmap = getCircleImageAsyncTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                mAvatarImageView.setImageBitmap(bitmap);

            } else {
                //start async task for download from firebase storage
                ImageAsyncTask.DownloadImageAsyncTask downloadImageAsyncTask = new ImageAsyncTask.DownloadImageAsyncTask(this);
                //downloadImageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                downloadImageAsyncTask.execute(this, getUserUID());

                //get result from async task
                Uri imageUri = null;
                try {
                    imageUri = downloadImageAsyncTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                GetCircleImageAsyncTask getCircleImageAsyncTask = new GetCircleImageAsyncTask();
                getCircleImageAsyncTask.execute(bitmap, 300);

                try {
                    bitmap = getCircleImageAsyncTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                mAvatarImageView.setImageBitmap(bitmap);

            }

        } else {
            // no permission
            Log.i(getString(R.string.LOG_TAG_USER_PROFILE),getString(R.string.no_permission));

        }
    }

    private String getFireMD5 (String name) {
        final String[] fireMd5 = {""};
        reference.child("Avatars/" + name + ".jpg");
        reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                fireMd5[0] = storageMetadata.getMd5Hash();
                Log.i(getString(R.string.LOG_TAG_USER_PROFILE), "fireMD5: " + fireMd5[0]);
            }
        });
        String md5 = fireMd5[0];
        return md5;
    }


    private void downloadAvatar(final FirebaseUser user){
        StorageReference storageRef = storage.getReference().child("Avatars/" + user.getUid());
        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "Avatars/");
            if (!dir.exists()) {
                dir.mkdir();
            }



            //if(!f.exits()) {
                ///...create new file..
           // }
           // else {
                    ////...do something...
          //  }

           final File localFile = File.createTempFile(user.getUid(), ".jpg");

           // final File file = new File(user.getUid(), ".jpg");
            Log.i(getString(R.string.LOG_TAG_AVATAR), "Name: " + user.getUid() + "       "  + "    "+  localFile);

            //createTempFile(user.getUid(), ".jpg");

            //final File localFile = new File(dir, user.getUid() + ".jpg");

            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    taskSnapshot.getStorage();
                    mAvatarUriPreferences = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor edit = mAvatarUriPreferences.edit();
                    edit.putString(user.getUid(), localFile.getPath());
                    edit.commit();
                    Log.i(getString(R.string.LOG_TAG_AVATAR), "Download is " + progress + " done"  + " ^ " +  localFile);

                }
            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    Log.i(getString(R.string.LOG_TAG_AVATAR), "Download is done");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
