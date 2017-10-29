package com.fallenangel.linmea.linmea.utils.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.fallenangel.linmea.linmea.user.authentication.user;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by NineB on 10/5/2017.
 */

public class ImageFileUtils {

    public static File  getCacheDir (final Context context, final String fileName) {

        final File localFile = new File(context.getCacheDir().getAbsolutePath(), "user_avatar_"  + user.getCurrentUserUID()+".jpg");

      //  final File cacheDir = new File(context.getCacheDir().getAbsolutePath());
      //  final File localFile = new File(cacheDir, "user_avatar_"  + fileName + ".jpg");

//        if (!cacheDir.exists()) {
//            cacheDir.mkdir();
//        }
//        if (localFile.exists()){
//            localFile.delete();
//        }

        try {
            localFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localFile;
    }

    public static File getExternalStoregeDir (final String fileName){
        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/LinMea/Avatars");
        File localFile = new File (directory, fileName + ".jpg");

        if (!directory.exists()) {
            directory.mkdirs();
        } if (localFile.exists ()) {
            localFile.delete ();
        }

        try {
            localFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localFile;
    }

    public Uri saveImageToCache (Activity activity, Bitmap image, String fileName){

        File imageFile = getCacheDir(activity, fileName);

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

    public static Uri getImageFromCache(Context context, String userName){
        String root = context.getCacheDir().toString();
        File directory = new File(root);
        String imageName = "user_avatar_" + userName + ".jpg";

        File imageFile;

        Log.i("GetUserDataAsyncTask", "getImageFromCache: " + imageName);
        if (!directory.exists()){
            return null;
        }

        try {
            imageFile = new File(directory, imageName);
            Log.i("GetUserDataAsyncTask", "getImageFromCache: " + imageFile);
            if (!imageFile.exists()) {
                Log.i("GetUserDataAsyncTask", "getImageFromCache:  NULL not exist");
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return Uri.fromFile(imageFile);
    }

    public static File getImageFileFromCache(Context context, String userName){

        File localFile = new File(context.getCacheDir().getAbsolutePath(), "user_avatar_"  + user.getCurrentUserUID()+".jpg");

      //  String root = context.getCacheDir().toString();
       // File directory = new File(root);
       // String imageName = "user_avatar_" + userName + ".jpg";

        //File imageFile;

     //   Log.i("GetUserDataAsyncTask", "getImageFromCache: " + imageName);
        if (!localFile.exists()){
            return null;
        }

       // try {
           // imageFile = new File(directory, imageName);
           // Log.i("GetUserDataAsyncTask", "getImageFromCache: " + imageFile);
           // if (!imageFile.exists()) {
             //   Log.i("GetUserDataAsyncTask", "getImageFromCache:  NULL not exist");
             //   return null;
          //  }
       // } catch (Exception e){
        //   e.printStackTrace();
         //   return null;
      //  }
        return localFile;
    }

}
