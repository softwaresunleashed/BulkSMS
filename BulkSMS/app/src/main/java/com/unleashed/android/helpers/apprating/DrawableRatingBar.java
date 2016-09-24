package com.unleashed.android.helpers.apprating;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RatingBar;

import com.unleashed.android.bulksms1.R;


public class DrawableRatingBar extends RatingBar {
    // TileBitmap to base the width and hight off of.
    @Nullable
    private Bitmap iconTile;
    private float scaleIconFactor;
    private
    @ColorInt
    int iconBackgroundColor;
    private
    @ColorInt
    int iconForegroundColor;
    private
    @DrawableRes
    int iconDrawable;

    public DrawableRatingBar(Context context) {
        super(context);
        init();
    }

    public DrawableRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawableRatingBarItem);
        scaleIconFactor = a.getFloat(R.styleable.DrawableRatingBarItem_scaleIconFactor, 1);
        iconBackgroundColor = a.getColor(R.styleable.DrawableRatingBarItem_iconBackgroundColor, Color.BLACK);
        iconForegroundColor = a.getColor(R.styleable.DrawableRatingBarItem_iconForegroundColor, Color.WHITE);
        iconDrawable = a.getResourceId(R.styleable.DrawableRatingBarItem_iconDrawable, -1);
        a.recycle();

        init();
    }

    public DrawableRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawableRatingBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setProgressDrawable(createProgressDrawable());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (iconTile != null) {
            final int width = iconTile.getWidth() * getNumStars();
            final int height = iconTile.getHeight();
            setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0),
                    resolveSizeAndState(height, heightMeasureSpec, 0));
        }
    }

    protected LayerDrawable createProgressDrawable() {
        final Drawable backgroundDrawable = createBackgroundDrawableShape();
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{backgroundDrawable,
                backgroundDrawable,
                createProgressDrawableShape()});
        layerDrawable.setId(0, android.R.id.background);
        layerDrawable.setId(1, android.R.id.secondaryProgress);
        layerDrawable.setId(2, android.R.id.progress);

        return layerDrawable;
    }

    protected Drawable createBackgroundDrawableShape() {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_rating);

        final Bitmap tileBitmap = scaleImageWithColor(drawable, scaleIconFactor, iconBackgroundColor);
        if (iconTile == null) {
            iconTile = tileBitmap;
        }
        final ShapeDrawable shapeDrawable = new ShapeDrawable(getDrawableShape());
        final BitmapShader bitmapShader = new BitmapShader(tileBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        shapeDrawable.getPaint().setShader(bitmapShader);

        return shapeDrawable;
    }


    private void setRatingStarColor(Drawable drawable, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(drawable, color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    protected Drawable createProgressDrawableShape() {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_rating);

        final Bitmap tileBitmap = scaleImageWithColor(drawable, scaleIconFactor, iconForegroundColor);
        final ShapeDrawable shapeDrawable = new ShapeDrawable(getDrawableShape());
        final BitmapShader bitmapShader = new BitmapShader(tileBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        shapeDrawable.getPaint().setShader(bitmapShader);
        return new ClipDrawable(shapeDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
    }

    Shape getDrawableShape() {
        return new RectShape();
    }

    public Bitmap scaleImageWithColor(Drawable drawable, float scaleFactor, @ColorInt int color) {
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        int sizeX = Math.round(drawable.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(drawable.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, true);
        Bitmap mutableBitmap = bitmapResized.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        canvas.drawBitmap(mutableBitmap, 0, 0, paint);

        return bitmapResized;
    }

    protected Bitmap fromDrawable(final Drawable drawable, final int height, final int width) {
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public float getScaleIconFactor() {
        return scaleIconFactor;
    }

    public void setScaleIconFactor(float scaleIconFactor) {
        this.scaleIconFactor = scaleIconFactor;
    }

    public int getIconForegroundColor() {
        return iconForegroundColor;
    }

    public void setIconForegroundColor(int iconForegroundColor) {
        this.iconForegroundColor = iconForegroundColor;
    }

    public int getIconBackgroundColor() {
        return iconBackgroundColor;
    }

    public void setIconBackgroundColor(int iconBackgroundColor) {
        this.iconBackgroundColor = iconBackgroundColor;
    }

    public int getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(int iconDrawable) {
        this.iconDrawable = iconDrawable;
    }
}