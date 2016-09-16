package com.unleashed.android.helpers.widgets.inputs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;
import com.unleashed.android.helpers.Utils.TextUtils;


/**
 * Set custom Typeface to TextView.
 * Usage:
 * <pl.tablica2.widgets.TypefaceTextView
 * android:layout_width="wrap_content"
 * android:layout_height="wrap_content"
 * android:text="@string/fa_heart"
 * android:textColor="@color/white_opaque"
 * olx:customTypeface="@string/fontawesome" />
 * <p/>
 * Created by tarandeep on 17/12/14.
 */
public class TypefaceTextView extends TextView {

    public TypefaceTextView(final Context context) {
        this(context, null);
    }

    public TypefaceTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TypefaceTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        TextUtils.setFace(this, context, attrs, defStyle);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
    }
}