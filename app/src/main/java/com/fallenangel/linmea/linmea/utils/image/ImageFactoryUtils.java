package com.fallenangel.linmea.linmea.utils.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import java.io.InputStream;

/**
 * Created by NineB on 9/29/2017.
 */

public class ImageFactoryUtils {

    public static Bitmap decodToSample (String file, int size){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);

        options.inSampleSize = calculateInSampleSize(options, size, size);
        options.inJustDecodeBounds = false;

        Bitmap bitmapImage = BitmapFactory.decodeFile(file);


        return bitmapImage;
    }



    public static Bitmap decodStreamToSample (InputStream stream, int size){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);

        options.inSampleSize = calculateInSampleSize(options, size, size);
        options.inJustDecodeBounds = false;

        Bitmap bitmapImage = BitmapFactory.decodeStream(stream);


        return bitmapImage;
    }

    public static Bitmap decodResourcesToSample (Resources resources, int id, int size){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, id, options);

        options.inSampleSize = calculateInSampleSize(options, size, size);
        options.inJustDecodeBounds = false;

        Bitmap bitmapImage = BitmapFactory.decodeResource(resources, id);


        return bitmapImage;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    public static Bitmap getCircleBitmap(Bitmap source, int radius) {
        if (source == null) {
            return null;
        }

        int diam = radius << 1;

        Bitmap scaledBitmap = scaleTo(source, diam);

        final Path path = new Path();
        path.addCircle(radius, radius, radius, Path.Direction.CCW);

        Bitmap targetBitmap = Bitmap.createBitmap(diam, diam, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);

        canvas.clipPath(path);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        canvas.drawBitmap(scaledBitmap, 0, 0, paint);

        return targetBitmap;
    }

    public static Bitmap scaleTo(Bitmap source, int size){
        int width = source.getWidth();
        int height = source.getHeight();

        height = height * size / width;
        width = size;

        if (height < size){
            width = width * size / height;
            height = size;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source, new Rect(0, 0, source.getWidth(), source.getHeight()), new Rect(0, 0, width, height), new Paint(Paint.ANTI_ALIAS_FLAG));
        return bitmap;
    }

}
