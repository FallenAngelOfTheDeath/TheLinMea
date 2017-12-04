package com.fallenangel.linmea.profile.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.interfaces.OnResultListener;
import com.fallenangel.linmea._linmea.util.SharedPreferencesUtils;
import com.fallenangel.linmea.linmea.utils.image.ImageUplDwnlUtils;
import com.fallenangel.linmea.profile.UserMetaData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.fallenangel.linmea.profile.UserMetaData.getUserUID;

/**
 * Created by NineB on 9/9/2017.
 */

public class ImageAsyncTask {

    public static class UploadImageAsyncTask extends AsyncTask<Object, Void, Uri>{

        private SharedPreferencesUtils mSharedPreferencesUtils = new SharedPreferencesUtils();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i ("UserProfile", "AsyncTask Uploading - started");

        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
            Log.i ("UserProfile", "AsyncTask Uploading - finished");
        }

        @Override
        protected Uri doInBackground(Object... params) {
            Activity activity = (Activity) params[0];
            Uri uri = (Uri) params[1];
            String name = (String) params[2];

            return uploadImgToFirebaseStorage(activity, uri, name);
        }

        private Uri uploadImgToFirebaseStorage (final Activity activity, Uri uri, final String name) {
            final Uri[] mUrl = { null };
            final Uri[] downloadUrl = {null};
            UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("Avatars/" + name + ".jpg").putFile(uri);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) ((100 * (float)taskSnapshot.getBytesTransferred()) / taskSnapshot.getBytesTransferred());
                    Log.i(activity.getString(R.string.LOG_TAG_USER_PROFILE), "AsyncTask Uploading " + progress + "% done");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mUrl[0] = taskSnapshot.getDownloadUrl();
                    //downloadUrl[0] = taskSnapshot.getDownloadUrl();
                   // Log.i(activity.getString(R.string.LOG_TAG_USER_PROFILE), "Returned uri of uploadImgToFirebaseStorage: " + mUrl[0]);

                    mSharedPreferencesUtils.putToSharedPreferences(activity, "MD5", name, taskSnapshot.getMetadata().getMd5Hash());
                    mSharedPreferencesUtils.putToSharedPreferences(activity, "FireMD5", name, taskSnapshot.getMetadata().getMd5Hash());

                }
            });
            Uri url = mUrl[0];
            //Log.i(activity.getString(R.string.LOG_TAG_USER_PROFILE), "Returned uri of uploadImgToFirebaseStorage: " + mUrl[0]);
            return url;
        }
    }

    // --------
    public static class DownloadImageAsyncTask extends AsyncTask<Object, Void, Uri> {

        private Context mContext;
        private View rootView;
        private ImageView imageView;

        public DownloadImageAsyncTask(Context context) {
            this.mContext = context;
            this.imageView = imageView;
        }

        private SharedPreferencesUtils mSharedPreferencesUtils = new SharedPreferencesUtils();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Log.i ("UserProfile", "AsyncTask Downloading - started");
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
//             imageView.setImageURI(uri);
           // Log.i ("UserProfile", "AsyncTask Downloading - finished");
        }

        @Override
        protected Uri doInBackground(Object... params) {
            final Activity activity = (Activity) params[0];
            final String name = (String) params[1];

            return downloadImage(activity, name, new OnResultListener<String>() {

                @Override
                public String onComplete(String result) {
                    mSharedPreferencesUtils.putToSharedPreferences(activity, "MD5", getUserUID(), result);
                 //   Log.i ("UserProfile", "onComplete Saved MD5: " + result);
                    return result;
                }

                @Override
                public String onSuccess(String result) {
                 //   Log.i ("UserProfile", "onSuccess Saved MD5: " + result);
                    return result;
                }

                @Override
                public void onFailure(Throwable error) {

                }
            });
        }


        public Uri downloadImage (final Activity activity, final String name, final OnResultListener<String> result) {

                final StorageReference storageReferenceImage = FirebaseStorage.getInstance().getReference().child("Avatars/" + name + ".jpg");

                final File mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString(), "LinMea/Avatars/");
                final File cacheDir = new File(mContext.getCacheDir(), "LinMea/Avatars/");
                //Log.i(activity.getString(R.string.LOG_TAG_USER_PROFILE), "mediaDir: " + mediaStorageDir);


                if (!cacheDir.exists()) {
                    if (!cacheDir.mkdirs()) {
                        //Log.d("UserProfile", "failed to create Storage directory");
                    }
                }


                final File localFile = new File(mediaStorageDir, name + ".jpg");
                Uri fileUri = Uri.fromFile(localFile);

            try {
                    localFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                storageReferenceImage.getFile(localFile).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) ((100 * (float)taskSnapshot.getBytesTransferred()) / taskSnapshot.getBytesTransferred());
                        Log.i(activity.getString(R.string.LOG_TAG_USER_PROFILE), "AsyncTask downloading " + progress + "%");
                    }
                }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                  //     result.onSuccess(taskSnapshot.getStorage().getMetadata().getResult().getMd5Hash());
                    }
                }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
//                        result.onComplete(task.getResult().getStorage().getMetadata().getResult().getMd5Hash());
                     //   Log.i(activity.getString(R.string.LOG_TAG_USER_PROFILE), "AsyncTask downloading TASK task.getResult().getStorage().getMetadata().getResult().getMd5Hash(): " + task.getResult().getStorage().getMetadata().getResult().getMd5Hash());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();

                     //   Log.i(activity.getString(R.string.LOG_TAG_USER_PROFILE), "AsyncTask downloading failed");
                    }
                });
            ImageUplDwnlUtils.saveCurrentFireMD5OfImageFromServer(activity, UserMetaData.getUserUID(), "FireMD5");
            return fileUri;
            }
    }




    public static class SaveImageAsyncTask extends AsyncTask<Object, Void, Object>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Log.i ("AsyncTask", "Saving - started");
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
          //  Log.i ("AsyncTask", "Saving - finished");
        }

        @Override
        protected Object doInBackground(Object... params) {
            Bitmap bitmap = (Bitmap) params[0];
            String userName = (String) params[1];
            return saveImage(bitmap, userName);
        }


        public Uri saveImage (Bitmap image, String userName){
            String root = Environment.getExternalStorageDirectory().toString();
            File directory = new File(root + "/LinMea/Avatars");
            String imageName = userName + ".jpg";

            File imageFile = new File (directory, imageName);

            if (!directory.exists()) {
                directory.mkdirs();
            } if (imageFile.exists ()) {
                imageFile.delete ();
            }

            try {
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception  e) {
                e.printStackTrace();
            }
            return Uri.fromFile(imageFile);
        }
    }

    public static Uri getImageFromExternalStorage(String userName){
        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/LinMea/Avatars");
        String imageName = userName + ".jpg";
        String file = directory + "/" + imageName;
        File imageFile;

        if (!directory.exists()){
            return null;
        }

        try {
            imageFile = new File(directory, imageName);
            if (!imageFile.exists()) {
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return Uri.fromFile(imageFile);
    }

    public static File getImageFileFromExternalStorage(String userName){
        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/LinMea/Avatars");
        String imageName = userName + ".jpg";
        String file = directory + "/" + imageName;
        File imageFile;

        if (!directory.exists()){
            return null;
        }

        try {
            imageFile = new File(directory, imageName);
            if (!imageFile.exists()) {
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return imageFile;
    }

}
