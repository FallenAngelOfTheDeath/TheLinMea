package com.fallenangel.linmea.profile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.utils.SharedPreferencesUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by NineB on 9/9/2017.
 */

public class ImageAsyncTask {

    public static class UploadImageAsyncTask extends AsyncTask<Object, Void, Uri>{

        private SharedPreferencesUtils mSharedPreferencesUtils = new SharedPreferencesUtils();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i ("AsyncTask", "Uploading - started");

        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
            Log.i ("AsyncTask", "Uploading - finished");
        }

        @Override
        protected Uri doInBackground(Object... params) {
            Activity activity = (Activity) params[0];
            Uri uri = (Uri) params[1];
            String name = (String) params[2];

            return uploadImgToFirebaseStorage(activity, uri, name);
        }


        private Uri uploadImgToFirebaseStorage (final Activity activity, Uri uri, final String name) {
            final Uri[] downloadUrl = {null};
            UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("Avatars/" + name + ".jpg").putFile(uri);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred());
                    Log.i(activity.getString(R.string.LOG_TAG_USER_PROFILE), "Upload is " + progress + "% done");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl[0] = taskSnapshot.getDownloadUrl();
                    mSharedPreferencesUtils.putToSharedPreferences(activity, "MD5", name, taskSnapshot.getMetadata().getMd5Hash());
                }
            });
            Uri url = downloadUrl[0];
            return url;
        }
    }




    public static class SaveImageAsyncTask extends AsyncTask<Object, Void, Object>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i ("AsyncTask", "Saving - started");
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.i ("AsyncTask", "Saving - finished");
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

    public static Bitmap getImageFromExternalStorage(String userName){
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

        return BitmapFactory.decodeFile(file);
    }

}
