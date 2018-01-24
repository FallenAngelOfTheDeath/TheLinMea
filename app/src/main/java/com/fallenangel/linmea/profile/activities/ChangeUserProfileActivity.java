package com.fallenangel.linmea.profile.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.ui.LoginActivity;
import com.fallenangel.linmea._linmea.model.UserModel;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._linmea.util.SharedPreferencesUtils;
import com.fallenangel.linmea.linmea.utils.image.ImageUplDwnlUtils;
import com.fallenangel.linmea.profile.utils.DecodeSampledBitmapAsyncTask;
import com.fallenangel.linmea.profile.utils.ImageAsyncTask;
import com.fallenangel.linmea.profile.utils.ImageAsyncTask.SaveImageAsyncTask;
import com.fallenangel.linmea.profile.utils.ImageAsyncTask.UploadImageAsyncTask;
import com.fallenangel.linmea.utils.CheckPermissionNetwork;
import com.fallenangel.linmea.utils.CheckPermissionReadExternalStorage;
import com.fallenangel.linmea.utils.CheckPermissionWriteExternalStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import static com.fallenangel.linmea._linmea.util.SharedPreferencesUtils.getFromSharedPreferences;
import static com.fallenangel.linmea.profile.UserMetaData.getCurrentUser;
import static com.fallenangel.linmea.profile.UserMetaData.getUserUID;

public class ChangeUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUserNameEditText,mEmailEditText;
    private ImageButton mUpdateButton, mGetImgFromGalleryButton, mDownload;
    public ImageView mAvatarImageView;

    private RelativeLayout mRelativeLayout;

    private StorageReference mStorageReference;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference reference = mFirebaseStorage.getReference();

    private CheckPermissionReadExternalStorage checkPermissionReadExternalStorage = new CheckPermissionReadExternalStorage();
    private CheckPermissionWriteExternalStorage checkPermissionWriteExternalStorage = new CheckPermissionWriteExternalStorage();
    private CheckPermissionNetwork checkPermissionNetwork = new CheckPermissionNetwork();
    private SharedPreferencesUtils mSharedPreferencesUtils = new SharedPreferencesUtils();

    static final int GALLERY_REQUEST = 1;
    final static int reqHeight = 300;
    final static int reqWidth = 300;

    private boolean btnStatus = false;

    Uri selectedImage;
    UserModel userM;
    final String[] meta = {""};

    InputStream inputStream[] = null;


    @Override
    protected void onStart() {
        super.onStart();
    }

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
        mRelativeLayout = (RelativeLayout) findViewById(R.id.constraint_layout);

        mUserNameEditText = (EditText)findViewById(R.id.change_user_name_ed);
        mEmailEditText = (EditText)findViewById(R.id.change_email_ed);

      //  mUpdateButton = (Button)findViewById(R.id.update_profile_btn);
        mGetImgFromGalleryButton = (ImageButton)findViewById(R.id.img_options);
        //mDownload = (Button)findViewById(R.id.download);

        mAvatarImageView = (ImageView)findViewById(R.id.avatar_imgview);

        mUpdateButton.setOnClickListener(this);
        mGetImgFromGalleryButton.setOnClickListener(this);
        mDownload.setOnClickListener(this);


        Log.i("nmm", "implementView: " + userM.getLastName() + userM.getFirstName());
    }


    private void setImage (String user){
        if (checkPermissionWriteExternalStorage.checkWritePermission(this)) {
            String savedMD5 = getFromSharedPreferences(this, "MD5", user);
            String fireMD5 = getFromSharedPreferences(this, "FireMD5", user);

            if (savedMD5 != null | fireMD5 != null) {
                if (savedMD5.equals(fireMD5)) {
                    //get bitmap image from external storage and det to image view
                    mAvatarImageView.setImageURI(ImageAsyncTask.getImageFromExternalStorage(user));
                } else {
                    //start async task for download from firebase storage
//                    ImageAsyncTask.DownloadImageAsyncTask downloadImageAsyncTask = new ImageAsyncTask.DownloadImageAsyncTask(this);
//                    downloadImageAsyncTask.execute(this, user);
                    //get result from async task
                    Uri imageUri = null;
                    imageUri = ImageUplDwnlUtils.downloadImage(this, User.getCurrentUserUID(), null);
//                    try {
//                        imageUri = downloadImageAsyncTask.get();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
                    //set image by uri
                    mAvatarImageView.setImageURI(imageUri);
                }
            } else {
                // no permission
                Log.i(getString(R.string.LOG_TAG_USER_PROFILE), getString(R.string.no_permission));
            }
        }

    }



    private void updateUI (){
        if (getCurrentUser() != null) {
            setImage(getUserUID());
            mUserNameEditText.setText(user.getDisplayName());
            mEmailEditText.setText(user.getEmail());

        } else {
            // user offline, UI not will be upd
            Snackbar.make(mRelativeLayout, R.string.user_offline, Snackbar.LENGTH_INDEFINITE).setAction(R.string.login, this).show();
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update_profile_btn:

                break;
//            case R.id.get_img_from_gallery_btn:
//                getImgFromGallery();
//                break;
//            case R.id.upload_avatar:
//                if (btnStatus = false){
//                    Toast.makeText(this, "first time choose the new image", Toast.LENGTH_SHORT).show();
//                } else {
//                    updateImage(selectedImage);
//                }
//                break;
            case R.id.snackbar_action:
                Intent logInIntent = new Intent(this, LoginActivity.class);
                startActivity(logInIntent);
                break;
//            case R.id.download:
//                Log.i(getString(R.string.LOG_TAG_USER_PROFILE), "   :   " + inputStream[0]);
//                if (checkPermissionReadExternalStorage.checkReadPermission(this) && checkPermissionWriteExternalStorage.checkWritePermission(this) && checkPermissionNetwork.checkNetworkPermission(this)) {
//                    ImageAsyncTask.DownloadImageAsyncTask downloadImageAsyncTask = new ImageAsyncTask.DownloadImageAsyncTask(this);
//                    downloadImageAsyncTask.execute(this, user.getUid());
//                    try {
//                        mAvatarImageView.setImageURI(downloadImageAsyncTask.get());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
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
}
