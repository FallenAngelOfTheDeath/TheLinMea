package com.fallenangel.linmea.profile.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;

/**
 * Created by NineB on 9/27/2017.
 */

public class GetCircleImageAsyncTask extends AsyncTask<Object, Void, Bitmap> {



    @Override
    protected Bitmap doInBackground(Object... params) {
        Bitmap bitmap = (Bitmap) params[0];
        int radius = (Integer) params[1];
        return getCircleBitmap(bitmap, radius);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
    }



    public static Bitmap getCircleBitmap(Bitmap bitmap, int radius) {
        if (bitmap == null) {
            return null;
        }

        int diam = radius << 1;

        final Path path = new Path();
        path.addCircle(radius, radius, radius, Path.Direction.CCW);

        Bitmap targetBitmap = Bitmap.createBitmap(diam, diam, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);

        canvas.clipPath(path);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        canvas.drawBitmap(bitmap, 0, 0, paint);

        return targetBitmap;
    }



}
