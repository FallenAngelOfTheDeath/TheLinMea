/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.non.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.fallenangel.linmea._modulus.non.interfaces.UnderlayButtonClickListener;


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

    private int mTextGravity = BOTTOM;

    private boolean mDrawBitmap = false;

    private String mText = "";
    private int mImageResId = 0;
    private int mTextColor = Color.WHITE;
    private int mBackgroundColor = Color.LTGRAY;
    private int mTextSize = 32;

    public static final int CENTER = 0;
    public static final int TOP = 1;
    public static final int BOTTOM = 2;


    public UnderlayButton(Context context, int res) {
        this.mContext = context;
        this.imageResId = res;
    }
    public UnderlayButton(Context context) {
        this.mContext = context;
    }

    public boolean onClickUnderlayButton(float x, float y){
        if (clickRegion != null && clickRegion.contains(x, y)){
            try {
                if (clickListener != null)
                clickListener.onClickUnderlayButton(pos, id);
            } catch (Exception e){
                throw e;
            }
            return true;
        }
        return false;
    }

    public void onDraw(Canvas c, RectF rect, int pos) {
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
        switch (mTextGravity){
            case CENTER:
                c.drawText(mText, rect.left + x, rect.centerY() + 20, p);
                break;
            case TOP:
                c.drawText(mText, rect.left + x, rect.top + 30, p);
                break;
            case BOTTOM:
                c.drawText(mText, rect.left + x, rect.bottom - 20, p);
                break;
            default:
                c.drawText(mText, rect.left + x, rect.bottom - 20, p);
                break;
        }
        // c.drawText(mText, rect.left + x, rect.top + yy, p);

        // Draw Image
        if (imageResId != 0){
            p.setColor(Color.BLACK);
            Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), imageResId);

            int cx = Math.round(r.width() - b.getWidth()) >> 1; // same as (...) / 2
            int cy = Math.round(r.height() - b.getHeight()) >> 1;
            c.drawBitmap(b, rect.right - rect.width() / 1.3f, rect.top + rect.height() / 6f, p);
        }

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

    public void setTextGravity(int gravity){
        mTextGravity = gravity;
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
