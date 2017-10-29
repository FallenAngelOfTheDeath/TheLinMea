package com.fallenangel.linmea.profile.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by NineB on 9/8/2017.
 */

public class DecodeSampledBitmapAsyncTask extends AsyncTask<Object, Void, Bitmap> {


    @Override
    protected Bitmap doInBackground(Object... params) {
        Context context = (Context) params[0];
        Uri uri = (Uri) params[1];
        int reqWidth = (Integer) params[2];
        int reqHeight = (Integer) params[3];
        return decodeBitmapFromStream(context, uri, reqWidth, reqHeight);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i("ImageUtilsAsyncTask", "Started");
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Log.i("ImageUtilsAsyncTask", "Closed");

    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeBitmapFromStream(Context context, Uri uri, final int reqWidth, final int reqHeight) {

        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        options.inJustDecodeBounds = false;
            Bitmap bitmapImage = BitmapFactory.decodeStream(inputStream, null, options);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmapImage;
    }

}
