package com.fallenangel.linmea.profile;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView mNameTV, mEmailTV, mUidTV, mEmailVerificationTV, mConfirmEmailTextView, mDataCreatedTextView, mSingInTimeTextView;
    private ImageView mAvatarImageView;
    private FloatingActionButton mFABProfile;
    private StorageReference mStorageReference;
    private SharedPreferences mAvatarUriPreferences;


    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://linmea-1e338.appspot.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseUser user = mAuth.getInstance().getCurrentUser();


        mNameTV = (TextView)findViewById(R.id.user_name_profile_status);
        mEmailTV = (TextView)findViewById(R.id.email_profile_status);
        mUidTV = (TextView)findViewById(R.id.uid_profile_status);
        mEmailVerificationTV = (TextView)findViewById(R.id.email_verification_status);
        mDataCreatedTextView = (TextView)findViewById(R.id.data_profile_created);
        mSingInTimeTextView = (TextView)findViewById(R.id.data_last_singin);
        mAvatarImageView = (ImageView)findViewById(R.id.profile_avatar);

        mConfirmEmailTextView = (TextView)findViewById(R.id.confirm_email_textview);
        mConfirmEmailTextView.setOnClickListener(this);

        mFABProfile = (FloatingActionButton)findViewById(R.id.fab_profile);
        mFABProfile.setOnClickListener(this);

        userData(user);
    }


    private void userData (FirebaseUser user){
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri avatarUrl = user.getPhotoUrl();

            downloadAvatar(user);

            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();
            mDataCreatedTextView.setText("Avatar URL: " + String.valueOf(avatarUrl));
            mNameTV.setText(name);
            mEmailTV.setText(email);
            mUidTV.setText(uid);
            mEmailVerificationTV.setText(String.valueOf(emailVerified));

            mAvatarUriPreferences = getPreferences(MODE_PRIVATE);
            String savedUri = mAvatarUriPreferences.getString(user.getUid(), "");

            mAvatarImageView.setImageURI(Uri.parse(savedUri));
        }
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


    @Override
    public void onClick(View v) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        switch (v.getId()){
            case R.id.fab_profile:
                String userUID = user.getUid();
                if (user != null) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signOut();
                    Log.i(String.valueOf(getText(R.string.LOG_TAG_AUTH)), R.string.user + " " + userUID + " has logged out!" );
                }
                break;
            case R.id.confirm_email_textview:
                user.sendEmailVerification();
                break;
            default:
                break;
        }
    }
}
