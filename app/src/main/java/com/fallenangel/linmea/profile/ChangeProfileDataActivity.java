package com.fallenangel.linmea.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.utils.CheckPermissionReadExternalStorage;
import com.fallenangel.linmea.utils.ImageUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class ChangeProfileDataActivity extends AppCompatActivity implements View.OnClickListener {

    static final int GALLERY_REQUEST = 1;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    final static int reqHeight = 200;
    final static int reqWidth = 200;

    private EditText mUserNameEditText,mEmailEditText;
    private Button mUpdateButton, mGetImgFromGalleryButton;
    private ImageView mAvatarImageView;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private StorageReference mStorageRef;
    private Uri avatarUri = null;
    private SharedPreferences mAvatarMd5Preferences;

    ImageUtils imageUtils = new ImageUtils();
    CheckPermissionReadExternalStorage checkPermissionReadExternalStorage = new CheckPermissionReadExternalStorage();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_data);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        implementView();
        if (user != null) {
            Log.d(getString(R.string.LOG_TAG_AUTH), "User is online");
            updUI(user);
        } else {
            Log.d(getString(R.string.LOG_TAG_AUTH), "User is offline");
        }
    }

    private void implementView (){

        mUserNameEditText = (EditText)findViewById(R.id.change_user_name_ed);
        mEmailEditText = (EditText)findViewById(R.id.change_email_ed);

        mUpdateButton = (Button)findViewById(R.id.update_profile_btn);
        mGetImgFromGalleryButton = (Button)findViewById(R.id.get_img_from_gallery_btn);

        mAvatarImageView = (ImageView)findViewById(R.id.avatar_imgview);

        mUpdateButton.setOnClickListener(this);
        mGetImgFromGalleryButton.setOnClickListener(this);
    }

    private void updUI(FirebaseUser user){
        String userUid = user.getUid();
        mUserNameEditText.setText(user.getDisplayName());
        mEmailEditText.setText(user.getEmail());

        if (checkPermissionReadExternalStorage.checkReadPermission(this)) {
            mAvatarImageView.setImageBitmap(imageUtils.getAvatarFromExternalStorage(this, user.getUid()));
        }
//        validateMD5("Avatar/",userUid,loadAvatarMd5(userUid));
//        Log.d("Avatar Validate", String.valueOf(validateMD5("Avatar/",userUid,loadAvatarMd5(userUid))));

    }

    private void updateUserData (){
        if (user != null) {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(mUserNameEditText.getText().toString()).setPhotoUri(avatarUri).build();
            user.updateProfile(profileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(getString(R.string.LOG_TAG_AUTH), "User profile updated.");
                    mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseUser newUserData = mAuth.getCurrentUser();
                            if (task.isSuccessful()) {
                                Log.d(getString(R.string.LOG_TAG_AUTH), "User name after update is " + newUserData.getDisplayName());
                            } else {
                                Log.d(getString(R.string.LOG_TAG_AUTH), "User data is not updated");
                            }

                        }
                    });

                }
            });
        } else {
            Log.d(getString(R.string.LOG_TAG_AUTH), "User is offline");
            Toast.makeText(getApplicationContext(), "You are Offline", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadAatar (String avatarName){
        StorageReference downloadRef = mStorageRef.child("Avatars/" + avatarName);
        try {
            File localFile = File.createTempFile(avatarName, "jpg");
            downloadRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void uploadAvatar (Uri uri, final String avatarName) {
        if (checkPermissionReadExternalStorage.checkReadPermission(this)) {
            UploadTask uploadTask = mStorageRef.child("Avatars/" + avatarName).putFile(uri);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred());
                    Log.i(getString(R.string.LOG_TAG_AVATAR), "Upload is " + progress + "% done");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    avatarUri = taskSnapshot.getMetadata().getDownloadUrl();
                    String avatarMd5Hash = taskSnapshot.getMetadata().getMd5Hash();
                    String avatarName = taskSnapshot.getMetadata().getName();
                    Log.i(getString(R.string.LOG_TAG_AVATAR), "Upload is done.");

                    saveAvatarMd5(avatarName, avatarMd5Hash);

                    Log.i(getString(R.string.LOG_TAG_AVATAR), "Name: " + avatarName + " MD5: " + avatarMd5Hash);
                    Log.i(getString(R.string.LOG_TAG_AVATAR), "Avatar uri: " + avatarUri);

//                UserProfileChangeRequest profileChangeRequest1 = new UserProfileChangeRequest.Builder().setPhotoUri(avatarUri).build();
//                user.updateProfile(profileChangeRequest1);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(getString(R.string.LOG_TAG_AVATAR), "Upload is failed");
                }
            });
        }
    }

//    private boolean validateMD5 (String path, String name, String md5){
//        StorageReference storageReference = mStorageRef.child(path + name);
//        String md5Hash = storageReference.getMetadata().getResult().getMd5Hash();
//        if (md5 == md5Hash){
//            return true;
//        }else return false;
//    }

    private void saveAvatarMd5(String key, String md5) {
        mAvatarMd5Preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = mAvatarMd5Preferences.edit();
        edit.putString(key, md5);
        edit.commit();
        Log.i(getString(R.string.LOG_TAG_AVATAR), "MD5 Hash has been saved");
    }

    private String loadAvatarMd5(String key) {
        mAvatarMd5Preferences = getPreferences(MODE_PRIVATE);
        String savedMd5 = mAvatarMd5Preferences.getString(key, "");
        return savedMd5;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update_profile_btn:
                updateUserData();
                break;
            case R.id.get_img_from_gallery_btn:
                getImgFromGallery();
                break;
            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    private void getImgFromGallery (){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String avatarName = user.getUid();

                    if (checkPermissionReadExternalStorage.checkReadPermission(this)) {
                        Bitmap imgBitmap = ImageUtils.decodeSampledBitmapFromStream(this, selectedImage, reqWidth, reqHeight);
                        imageUtils.saveImage(imgBitmap, this, avatarName);
                        mAvatarImageView.setImageBitmap(imgBitmap);
                    }

                    uploadAvatar(selectedImage, avatarName);
                }
                break;
            default:
                break;
        }

    }
}
