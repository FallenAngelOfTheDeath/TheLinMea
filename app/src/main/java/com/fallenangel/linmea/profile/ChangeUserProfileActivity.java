package com.fallenangel.linmea.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.authentication.LoginActivity;
import com.fallenangel.linmea.profile.ImageAsyncTask.SaveImageAsyncTask;
import com.fallenangel.linmea.profile.ImageAsyncTask.UploadImageAsyncTask;
import com.fallenangel.linmea.utils.CheckPermissionReadExternalStorage;
import com.fallenangel.linmea.utils.SharedPreferencesUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.ExecutionException;

public class ChangeUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUserNameEditText,mEmailEditText;
    private Button mUpdateButton, mGetImgFromGalleryButton;
    private ImageView mAvatarImageView;

    private ConstraintLayout mConstraintLayout;

    private StorageReference mStorageReference;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference reference = mFirebaseStorage.getReference();

    private CheckPermissionReadExternalStorage checkPermissionReadExternalStorage = new CheckPermissionReadExternalStorage();
    private SharedPreferencesUtils mSharedPreferencesUtils = new SharedPreferencesUtils();

    static final int GALLERY_REQUEST = 1;
    final static int reqHeight = 200;
    final static int reqWidth = 200;

    private boolean btnStatus = false;

    Uri selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_data);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        implementView();
        updateUI();
        btnStatus = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        btnStatus = false;
    }

    private void implementView (){
        mConstraintLayout = (ConstraintLayout)findViewById(R.id.constraint_layout);

        mUserNameEditText = (EditText)findViewById(R.id.change_user_name_ed);
        mEmailEditText = (EditText)findViewById(R.id.change_email_ed);

        mUpdateButton = (Button)findViewById(R.id.update_profile_btn);
        mGetImgFromGalleryButton = (Button)findViewById(R.id.get_img_from_gallery_btn);

        mAvatarImageView = (ImageView)findViewById(R.id.avatar_imgview);

        mUpdateButton.setOnClickListener(this);
        mGetImgFromGalleryButton.setOnClickListener(this);
    }


    private void updateUI (){
        if (user != null) {
            // Upd UI for current user
            mUserNameEditText.setText(user.getDisplayName());
            mEmailEditText.setText(user.getEmail());

            if (checkPermissionReadExternalStorage.checkReadPermission(this)) {
                mAvatarImageView.setImageBitmap(ImageAsyncTask.getImageFromExternalStorage(user.getUid()));
            } else {
                Log.i(getString(R.string.permission),getString(R.string.no_permission_to_read_from_es));
            }
        } else {
            // user offline, UI not will be upd
            Snackbar.make(mConstraintLayout, R.string.user_offline, Snackbar.LENGTH_INDEFINITE).setAction(R.string.login, this).show();
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update_profile_btn:

                break;
            case R.id.get_img_from_gallery_btn:
                getImgFromGallery();
                break;
            case R.id.upload_avatar:
                if (btnStatus = false){
                    Toast.makeText(this, "first time choose the new image", Toast.LENGTH_SHORT).show();
                } else {
                    updateImage(selectedImage);
                }
                break;
            case R.id.snackbar_action:
                Intent logInIntent = new Intent(this, LoginActivity.class);
                startActivity(logInIntent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    selectedImage = data.getData();
                    updateImage(selectedImage);
                }
                break;
            default:
                break;
        }
    }

    private void getImgFromGallery (){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);

        btnStatus = true;
    }

    private void getImgFromCam (){

        btnStatus = true;
    }

    private void updateImage (Uri selectedImage){
        if (checkPermissionReadExternalStorage.checkReadPermission(this)) {

            DecodeSampledBitmapAsyncTask decodeAsyncTask = new DecodeSampledBitmapAsyncTask();
            decodeAsyncTask.execute(this, selectedImage, reqWidth, reqHeight);

            Bitmap sampledImg = null;
            try {
                sampledImg = decodeAsyncTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            mAvatarImageView.setImageBitmap(sampledImg);

            SaveImageAsyncTask savingAsynkTask = new SaveImageAsyncTask();
            savingAsynkTask.execute(sampledImg, user.getUid());
            Uri imgUriFromFile = null;
            try {
                imgUriFromFile = (Uri) savingAsynkTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            UploadImageAsyncTask uploadAsyncTask = new UploadImageAsyncTask();
            uploadAsyncTask.execute(this, imgUriFromFile, user.getUid());
        }
    }

//______________________________________________________________________________________________________________________________________________________________________________________________
    private int validateAvatarFromFireBase () {
        final int[] bool = {0};
        reference.child("Avatars/" + user.getUid() + ".jpg");
        reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                if (storageMetadata.getMd5Hash() == mSharedPreferencesUtils.getFromSharedPreferences(ChangeUserProfileActivity.this, "MD5", user.getUid())) {
                    bool[0] = 1;
                    Log.i(getString(R.string.LOG_TAG_USER_PROFILE), "MD5 is validated, " + "MD5_FB: " + bool[0]);

                } else {
                    bool[0] = 0;
                    Log.i(getString(R.string.LOG_TAG_USER_PROFILE), "MD5 is not validated" + "MD5_FB: " + bool[0]);

                }
            }
        });
        return bool[0];
    }


    private void updateUIold (){
        if (user != null) {
            Log.i(getString(R.string.LOG_TAG_USER_PROFILE), getString(R.string.user_online));

            mUserNameEditText.setText(user.getDisplayName());
            mEmailEditText.setText(user.getEmail());

            if (checkPermissionReadExternalStorage.checkReadPermission(this)) {
            //    mAvatarImageView.setImageBitmap(mImageUtils.getAvatarFromExternalStorage(this, user.getUid()));

                switch (validateAvatarFromFireBase()){
                    case 0:
                        Log.i(getString(R.string.LOG_TAG_USER_PROFILE), "get avatar from External Storage");
                      //  mAvatarImageView.setImageBitmap(mImageUtils.getAvatarFromExternalStorage(this, user.getUid()));
                        break;
                    case 1:
                        Log.i(getString(R.string.LOG_TAG_USER_PROFILE), "get avatar from FireBase");
                        //mAvatarImageView.setImageURI(mImageUtils.downloadAvatar(user.getUid(), this));
                        //Log.i("fuck", String.valueOf(mImageUtils.downloadAvatar(user.getUid(), this)));

                        break;
                    default:
                        break;
                }
            } else {
                Log.i(getString(R.string.permission),getString(R.string.no_permission_to_read_from_es));
            }
        } else {
            Log.i(getString(R.string.LOG_TAG_USER_PROFILE), getString(R.string.user_offline));
            Snackbar.make(mConstraintLayout, R.string.user_offline, Snackbar.LENGTH_INDEFINITE).setAction(R.string.login, this).show();
        }

    }

}
