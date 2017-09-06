package com.fallenangel.linmea.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.fallenangel.linmea.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by NineB on 9/4/2017.
 */

public class ImageUtils extends AsyncTask<InputStream, Void, Bitmap> {

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            Log.i ("ImageUtils", "" + "img height: " + height + " img width: " + width);
            Log.i ("ImageUtils", "" + "img height Ratio: " + heightRatio + " img width Ratio: " + widthRatio);
            Log.i ("ImageUtils", "" + "img sample size: " + inSampleSize);

        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromStream( Context context, Uri selectedImage, final int reqWidth, final int reqHeight) {
        Bitmap bitmapImage = null;
        InputStream inputStream = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "InputStream has been created!");

        try {
            inputStream = context.getContentResolver().openInputStream(selectedImage);

            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            try {
                inputStream.close();
                Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "InputStream has been closed!");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "InputStream can not be close!");
            }

            inputStream = context.getContentResolver().openInputStream(selectedImage);
            Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "InputStream has been recreated!");

            options.inJustDecodeBounds = false;
            bitmapImage = BitmapFactory.decodeStream(inputStream, null, options);

            try {
                inputStream.close();
                Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "InputStream has been closed!");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "InputStream can not be close!");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "InputStream can not be create!");
        }
        return bitmapImage;
    }


    public Bitmap getAvatarFromExternalStorage (Context context, String userName){
        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/LinMea/Avatars");
        String imageName = userName + ".jpg";
        String file = directory + "/" + imageName;
        File imageFile;

        Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "Avatar: " + file);



        if (!directory.exists()){
            Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "Dictionary not exist!");
            return null;
        }

        try {
            imageFile = new File(directory, imageName);
            if (!imageFile.exists()) {
                Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "Avatar image not exist!");
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return BitmapFactory.decodeFile(file);
    }

    public void saveImage (Bitmap image, Context context, String userName){
            String root = Environment.getExternalStorageDirectory().toString();
            File directory = new File(root + "/LinMea/Avatars");
            String imageName = userName + ".jpg";

            File imageFile = new File (directory, imageName);

            if (!directory.exists()) {
                directory.mkdirs();
                Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "Director " + directory + " has been created!");
            } else {
                Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "Director " + directory + " already exist!");
            }


            if (imageFile.exists ()) {
                imageFile.delete ();
                Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "Image " + imageName + " already exits! Old img has been deleted!");
            } else {
                Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "Image " + imageName + " not already exits!");
            }

            try {
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "Image " + imageName + " has been saved to " + directory);
            } catch (Exception  e) {
                e.printStackTrace();
                Log.i(context.getString(R.string.LOG_TAG_IMAGE_UTILS), "saving image has been failed!");
            }

    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Log.i ("ImageUtils", "AsyncTask: decode Bitmap from stream is complete");
    }

    @Override
    protected Bitmap doInBackground(InputStream... params) {
        return null;
    }
}
