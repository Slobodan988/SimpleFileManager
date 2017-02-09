package com.amaze.filemanager.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by slobodanzdravkovic on 1/20/17.
 */

public class ButtonFont extends Button {
    public ButtonFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public ButtonFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        createButtonFont();
    }

    public ButtonFont(Context context) {
        super(context);
        createButtonFont();
    }

    public void createButtonFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        //Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/orangejuice.ttf");
        setTypeface(font);
    }
}
