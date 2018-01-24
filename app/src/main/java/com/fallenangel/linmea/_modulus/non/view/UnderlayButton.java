package com.fallenangel.linmea._modulus.non.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.fallenangel.linmea._linmea.interfaces.UnderlayButtonClickListener;


/**
 * Created by NineB on 11/5/2017.
 */

public class UnderlayButton {
    private String text;
    private int imageResId;
    private int color;
    private int pos;
    private int id;
    private RectF clickRegion;
    private UnderlayButtonClickListener clickListener;
    private Context mContext;

    private boolean mDrawBitmap = false;

    private String mText = "";
    private int mImageResId = 0;
    private int mTextColor = Color.WHITE;
    private int mBackgroundColor = Color.LTGRAY;
    private int mTextSize = 32;
  //  private int mSide = RIGHT;
    private UnderlayButtonClickListener mOnClickListener;

//    public static final int LEFT = 0;
//    public static final int RIGHT = 1;

 //   public static final int LEFT = 1 << 2;
  //  public static final int RIGHT = 1 << 3;

//    public UnderlayButton(Context context, String text, int imageResId, int color, UnderlayButtonClickListener clickListener) {
//        this.mContext = context;
//        this.text = text;
//        this.imageResId = imageResId;
//        this.color = color;
//        this.clickListener = clickListener;
//    }
//
//    public UnderlayButton(UnderlayButtonClickListener clickListener) {
//        this.clickListener = clickListener;
//    }
//
//    public UnderlayButton(Context context) {
//        this.mContext = context;
//    }

    public UnderlayButton(Context context, int res) {
        this.mContext = context;
        this.imageResId = res;
    }

    public boolean onClickUnderlayButton(float x, float y){
        if (clickRegion != null && clickRegion.contains(x, y)){
            clickListener.onClickUnderlayButton(pos, id);
            return true;
        }
        return false;
    }

    public void onDraw(Canvas c, RectF rect, int pos){
        Paint p = new Paint();

        // Draw background
        p.setColor(mBackgroundColor);
        c.drawRect(rect, p);

        // Draw Text
        p.setColor(mTextColor);
        p.setTextSize(mTextSize);

        Rect r = new Rect();
        float cHeight = rect.height();
        float cWidth = rect.width();
        p.setTextAlign(Paint.Align.LEFT);
        p.getTextBounds(mText, 0, mText.length(), r);


        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        c.drawText(mText, rect.left + x, rect.bottom -20, p);
       // c.drawText(mText, rect.left + x, rect.top + yy, p);

        // Draw Image

        p.setColor(Color.BLACK);
        Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), imageResId);

        int cx = Math.round(r.width() - b.getWidth()) >> 1; // same as (...) / 2
        int cy = Math.round(r.height() - b.getHeight()) >> 1;
        c.drawBitmap(b, rect.right - rect.width()/1.3f, rect.top + rect.height()/6f, p);


        clickRegion = rect;
        this.pos = pos;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public void setImage(Context context, int imageResId) {
        this.mImageResId = imageResId;
        this.mContext = context;
        mDrawBitmap = true;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public void setOnClickListener(UnderlayButtonClickListener onClickListener) {
        this.clickListener = onClickListener;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //    public int getSide() {
//        return mSide;
//    }
//
//    public void setSide(int side) {
//        this.mSide = side;
//    }
}
