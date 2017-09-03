package com.fallenangel.linmea.authentication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class ChangeProfileDataActivity extends AppCompatActivity implements View.OnClickListener {

    static final int GALLERY_REQUEST = 1;

    private EditText mUserNameEditText,mEmailEditText;
    private Button mUpdateButton, mGetImgFromGalleryButton;
    private ImageView mAvatarImageView;
    private Bitmap avatarImg;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseStorage mAvatarStorage = FirebaseStorage.getInstance("gs://linmea-1e338.appspot.com");
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_data);
        implementView();
        if (user != null) {
            Log.d(getString(R.string.LOG_TAG_AUTH), "User is online");
            updUI(user);
        } else {
            Log.d(getString(R.string.LOG_TAG_AUTH), "User is offline");
        }
       /// updUI(user);


//        mAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getApplicationContext(), "Changed", Toast.LENGTH_SHORT).show();
//
//


                //FirebaseUser cUser = mAuth.getCurrentUser();
              //  mUserNameEditText.setText(cUser.getDisplayName());
              //  mEmailEditText.setText(cUser.getEmail());
//            }
//        });

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
//        mAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                FirebaseUser user = mAuth.getCurrentUser();

                mUserNameEditText.setText(user.getDisplayName());
                mEmailEditText.setText(user.getEmail());
//            }
//        });
    }

    private void updateUserData (){
        if (user != null) {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(mUserNameEditText.getText().toString()).setPhotoUri(null).build();
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
                    try {
                        avatarImg = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                        Log.d(getString(R.string.LOG_TAG_AUTH), "file: " + selectedImage);

                        String folder = "avatars/";
                        String nameIMG = user.getUid();
                        StorageReference storageRef = mStorageRef.child(folder + nameIMG + ".jpg");
                        Log.d(getString(R.string.LOG_TAG_AUTH), "file: " + folder + nameIMG + ".jpg");
                        Uri file = Uri.fromFile(new File("path/to/folderName/file.jpg"));
                        UploadTask uploadTask = storageRef.putFile(file);
                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                System.out.println(taskSnapshot.getBytesTransferred());
                            }
                        });
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    mAvatarImageView.setImageURI(selectedImage);
                    //mAvatarImageView.setImageBitmap(avatarImg);
                }
                break;
            default:
                break;
        }

    }
}
