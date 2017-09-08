package com.fallenangel.linmea.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fallenangel.linmea.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by NineB on 9/4/2017.
 */

public class ImageUtils {

























    public Uri downloadAvatar (final String userName, final Context context){
        //final Context context = activity.getBaseContext();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Avatars/" + userName);

        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/LinMea/Avatars");
        String imageName = userName + ".jpg";
        String file = directory + "/" + imageName;
        final File imageFile;

        final Uri[] uriM = {null};

        Log.i(context.getString(R.string.LOG_TAG_AVATAR), "Avatar will be saved as: " + file);



        if (!directory.exists()){
            Log.i(context.getString(R.string.LOG_TAG_AVATAR), "Dictionary not exist!");
            directory.mkdir();
            Log.i(context.getString(R.string.LOG_TAG_AVATAR), "Dictionary has been created!");
        }

        try {
            imageFile = new File(directory, imageName);
            storageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    taskSnapshot.getStorage();
                    uriM[0] = Uri.fromFile(imageFile);
                    //SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
                    //sharedPreferencesUtils.putToSharedPreferences(activity, "MD5", userName, taskSnapshot.getStorage().getMetadata().getResult().getMd5Hash());

                    Log.i(context.getString(R.string.LOG_TAG_AVATAR), "Download is " + progress + " done"  + " ^ " +  imageFile);
                }
            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    Log.i(context.getString(R.string.LOG_TAG_AVATAR), "Download is done");
                }
            });
            if (!imageFile.exists()) {
                Log.i(context.getString(R.string.LOG_TAG_AVATAR), "Avatar image not exist!");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        Uri uri = uriM[0];
        return uri;
    }

    public Bitmap getAvatarFromExternalStorage (String userName){
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
