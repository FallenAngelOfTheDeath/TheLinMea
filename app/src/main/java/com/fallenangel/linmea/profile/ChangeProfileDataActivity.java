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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private Uri avatarUri = null;
    private SharedPreferences mPreferences;

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

        //String avatarUri = getFromSharedPreferences(getString(R.string.avatarURI), user.getUid());
        //String md5FromFB = mStorage.getReferenceFromUrl(avatarUri).getMetadata().getResult().getMd5Hash();
        final String[] md5FromFB = {""};
        StorageReference forestRef = FirebaseStorage.getInstance().getReference().child("Avatars/" + user.getUid() + ".jpg");

        forestRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                md5FromFB[0] = storageMetadata.getMd5Hash();
            }
        });

        if (validateMD5(md5FromFB[0], getFromSharedPreferences(getString(R.string.MD5), user.getUid()))){
            //not validated, download new from fb
            Log.i("Validate", "MD5FB = MD5ES");

            if (checkPermissionReadExternalStorage.checkReadPermission(this)) {
                mAvatarImageView.setImageBitmap(imageUtils.getAvatarFromExternalStorage(this, user.getUid()));
            } else {
                Log.i(getString(R.string.permission),getString(R.string.no_permission_to_read_from_es));
            }

        } else {
            // is validated get from external storage
            Log.i("Validate", "MD5FB != MD5ES");
            imageUtils.downloadAvatar(user.getUid(), this);
            forestRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    md5FromFB[0] = storageMetadata.getMd5Hash();
                }
            });
            saveToSharedPreferences(getString(R.string.MD5), user.getUid(),  md5FromFB[0]);
            mAvatarImageView.setImageBitmap(imageUtils.getAvatarFromExternalStorage(this, user.getUid()));
        }
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


    private void uploadAvatar (Uri uri, final String avatarName) {
        final Uri[] downloadUrl = {null};
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

                    Log.i(getString(R.string.LOG_TAG_AVATAR), "Upload is done.");
                    //saveToSharedPreferences(getString(R.string.avatarURI), user.getUid(), taskSnapshot.getMetadata().getDownloadUrl().toString());
                    saveToSharedPreferences(getString(R.string.MD5), user.getUid(), getMd5FromFireBase(taskSnapshot));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(getString(R.string.LOG_TAG_AVATAR), "Upload is failed");
                }
            });
        }
    }


    private String getMd5FromFireBase (UploadTask.TaskSnapshot taskSnapshot){
        String avatarMd5Hash = taskSnapshot.getMetadata().getMd5Hash();
        return avatarMd5Hash;
    }

    private void saveToSharedPreferences(String key, String userName, String md5) {
        mPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(key + ":" + userName, md5);
        edit.commit();
        Log.i(getString(R.string.LOG_TAG_AVATAR), "MD5 Hash has been saved");
    }

    private String getFromSharedPreferences (String key, String userName) {
        mPreferences = getPreferences(MODE_PRIVATE);
        String savedSharedPreferences = mPreferences.getString(key + ":" + userName, "");
        return savedSharedPreferences;
    }

    private boolean validateMD5 (String Md5FromFB, String Md5FromSP){
        if (Md5FromFB == Md5FromSP){
            return true;
        } else {
            return false;
        }
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

                    if (checkPermissionReadExternalStorage.checkReadPermission(this)) {
                        Bitmap imgBitmap = ImageUtils.decodeSampledBitmapFromStream(this, selectedImage, reqWidth, reqHeight);
                        Uri decodeImageUri = imageUtils.saveImage(imgBitmap, this, user.getUid());
                        uploadAvatar(decodeImageUri, user.getUid());

                        //get MD

                        imageUtils.saveImage(imgBitmap, this, user.getUid());
                        mAvatarImageView.setImageBitmap(imgBitmap);
                    }

                }
                break;
            default:
                break;
        }

    }
}
