package com.fallenangel.linmea.linmea.utils.image;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.interfaces.OnResultListener;
import com.fallenangel.linmea.linmea.user.authentication.User;
import com.fallenangel.linmea._linmea.util.SharedPreferencesUtils;
import com.fallenangel.linmea.profile.UserMetaData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by NineB on 10/5/2017.
 */

public class ImageUplDwnlUtils {


    public static Uri uploadImgToFirebaseStorage (final Activity activity, Uri uri, final String name) {
        final SharedPreferencesUtils mSharedPreferencesUtils = new SharedPreferencesUtils();
        final Uri[] mUrl = { null };

        FirebaseStorage.getInstance().getReference().child("Avatars/" + name + ".jpg").putFile(uri)
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100 * (float)taskSnapshot.getBytesTransferred()) / taskSnapshot.getBytesTransferred());
                Log.i(activity.getString(R.string.LOG_TAG_USER_PROFILE), "AsyncTask Uploading " + progress + "%");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mUrl[0] = taskSnapshot.getDownloadUrl();
                mSharedPreferencesUtils.putToSharedPreferences(activity, "MD5", name, taskSnapshot.getMetadata().getMd5Hash());
                mSharedPreferencesUtils.putToSharedPreferences(activity, "FireMD5", name, taskSnapshot.getMetadata().getMd5Hash());
            }
        });
        Uri url =  mUrl[0];
        return url;
    }



    public static Uri downloadImage (final Context context, final String name, final OnResultListener<String> result) {

        File localFile = ImageFileUtils.getCacheDir(context, name);
       // File localFile = new File(context.getCacheDir().getAbsolutePath(), "user_avatar_"  + name + ".jpg");

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Avatars/" + name + ".jpg");

        storageReference.getFile(localFile).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100 * (float)taskSnapshot.getBytesTransferred()) / taskSnapshot.getBytesTransferred());
                Log.i(context.getString(R.string.LOG_TAG_USER_PROFILE), "AsyncTask downloading bytes transferred " + taskSnapshot.getBytesTransferred() + " - " + progress + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(context.getString(R.string.LOG_TAG_USER_PROFILE), "onSuccess error: " + exception.getMessage());
            }
        });

        saveCurrentFireMD5OfImageFromServer(context, UserMetaData.getUserUID(), "FireMD5");

        return Uri.fromFile(localFile);
    }

    public static void getMD5ImagesFromFireBase (String name, final OnResultListener<String> result){
        FirebaseStorage.getInstance().getReference().child("Avatars/" + name + ".jpg")
                .getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                result.onSuccess(storageMetadata.getMd5Hash());
            }
        }).addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                    result.onComplete(task.getResult().getMd5Hash());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void saveCurrentFireMD5OfImageFromServer (final Context context, String uid, final String key) {
        if (User.getCurrentUser().getPhotoUrl() != null) {
            getMD5ImagesFromFireBase(uid, new OnResultListener<String>() {
                @Override
                public String onComplete(String result) {
                    SharedPreferencesUtils.putToSharedPreferences(context, key, User.getCurrentUserUID(), result);
                    return result;
                }

                @Override
                public String onSuccess(String result) {
                    return result;
                }

                @Override
                public void onFailure(Throwable error) {
                    error.printStackTrace();
                }
            });
        }
    }

                        public static String getFireMD5ofImage (final Context contextn, String name) {
                            final String finalFireMd[] = {""};
                            FirebaseStorage.getInstance().getReference().child("Avatars/" + name + ".jpg").getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                @Override
                                public void onSuccess(StorageMetadata storageMetadata) {
                                    finalFireMd[0] = storageMetadata.getMd5Hash();
                                    Log.i("00000000000", "getFireMD5ofImage:0 " + finalFireMd[0]);

                                }
                            });
                            String str = finalFireMd[0];
                            Log.i("00000000000", "getFireMD5ofImage:1 " + str);
                            return str;


                    //        final String[] fireMd5 = {""};
                    //        FirebaseStorage.getInstance().getReference().child("Avatars/" + name + ".jpg");
                    //        FirebaseStorage.getInstance().getReference().child("Avatars/" + name + ".jpg").getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    //            @Override
                    //            public void onSuccess(StorageMetadata storageMetadata) {
                    //                fireMd5[0] = storageMetadata.getMd5Hash();
                    //                Log.i("00000000000", "getFireMD5ofImage: " + fireMd5[0]);
                    //
                    //            }
                    //        });
                    //        String md5 = fireMd5[0];
                    //        return md5;
                        }

}
