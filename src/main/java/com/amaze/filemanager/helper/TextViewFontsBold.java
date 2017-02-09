package com.amaze.filemanager.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by slobodanzdravkovic on 1/19/17.
 */

public class TextViewFontsBold extends TextView {

    public TextViewFontsBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createFont();
    }

    public TextViewFontsBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }

    public TextViewFontsBold(Context context) {
        super(context);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-BoldCondensed.ttf");
        //Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/orangejuice.ttf");
        setTypeface(font);
    }
}