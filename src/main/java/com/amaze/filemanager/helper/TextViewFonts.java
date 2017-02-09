package com.amaze.filemanager.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by slobodanzdravkovic on 1/19/17.
 */

public class TextViewFonts extends TextView {

    public TextViewFonts(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createFont();
    }

    public TextViewFonts(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }

    public TextViewFonts(Context context) {
        super(context);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        //Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/orangejuice.ttf");
        setTypeface(font);
    }
}