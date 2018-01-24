package com.fallenangel.linmea._modulus.prferences.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fallenangel.linmea.R;

/**
 * Created by NineB on 1/19/2018.
 */

public class SimplePreferenceItem extends RelativeLayout {

    private ImageView ivIcon;
    private TextView tvTitle, tvDescription;

    public SimplePreferenceItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SimplePreferenceItem);
        String title = typedArray.getString(R.styleable.SimplePreferenceItem_title);
        String description = typedArray.getString(R.styleable.SimplePreferenceItem_description);
        Drawable icon =typedArray.getDrawable(R.styleable.SimplePreferenceItem_icon);
        typedArray.recycle();



        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.simple_preference_item, this, true);


        RelativeLayout.LayoutParams rlParamsIv = new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlParamsIv.addRule(RelativeLayout.ALIGN_LEFT);

        ivIcon = (ImageView) getChildAt(0);
        ivIcon.setImageDrawable(icon);
        ivIcon.setMinimumHeight(34);
        ivIcon.setMaxHeight(34);
        ivIcon.setMaxWidth(34);
        ivIcon.setMinimumWidth(34);
        ivIcon.setScaleType(ImageView.ScaleType.CENTER);
        ivIcon.setLayoutParams(rlParamsIv);

        RelativeLayout.LayoutParams rlParamsTitle = new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlParamsTitle.addRule(RelativeLayout.RIGHT_OF, ivIcon.getId());


        tvTitle = (TextView) getChildAt(1);
        tvTitle.setText(title);
        tvTitle.setLayoutParams(rlParamsTitle);

        RelativeLayout.LayoutParams rlParamsDescription = new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlParamsTitle.addRule(RelativeLayout.RIGHT_OF, tvTitle.getId());
        rlParamsTitle.addRule(RelativeLayout.BELOW, tvTitle.getId());

        tvDescription = (TextView) getChildAt(2);
        tvDescription.setText(description);
        tvDescription.setLayoutParams(rlParamsDescription);



    }

    public void setDrawable(Drawable drawable) {
        this.ivIcon.setImageDrawable(drawable);
    }

    public void setTitle(int title) {
        this.tvTitle.setText(title);
    }

    public void setDescription(int description) {
        this.tvDescription.setText(description);
    }
}
