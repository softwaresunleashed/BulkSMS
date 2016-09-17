package com.unleashed.android.helpers.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Pair;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Helpers;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;

import java.io.IOException;


public class BitmapUtils {

    public static Bitmap loadRotatedBitmapFromPath(String filePath, int reqWidth, int reqHeight, float rotationDegrees) {
        Bitmap normal = decodeSampledBitmapFromPath(filePath, reqWidth, reqHeight);
        Bitmap rotated = createRotatedBitmap(normal, rotationDegrees);

        normal.recycle();
        normal = null;

        return rotated;
    }

    public static Bitmap loadRotatedBitmapFromPathWithOrientationConstraint(String filePath, int reqWidth, int reqHeight) {
        return loadRotatedBitmapFromPathWithOrientationConstraint(filePath, reqWidth, reqHeight, ImageRotate.Degrees_0);
    }

    public static Bitmap loadRotatedBitmapFromPathWithOrientationConstraint(String filePath, int reqWidth, int reqHeight, ImageRotate rotate) {
        int rotationDegrees = rotate.getDegrees();
        int orientation = getImageOrientation(filePath);

        if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            int tempReqWidth = reqWidth;
            reqWidth = reqHeight;
            reqHeight = tempReqWidth;

            // Limit max image size.
            if (Helpers.isLimitImageSize()) {
                if (reqWidth > 640) {
                    reqWidth = 640;
                }
                if (reqHeight > 640) {
                    reqHeight = 640;
                }
            } else {
                if (reqWidth > 1400) {
                    reqWidth = 1300;
                }
                if (reqHeight > 1400) {
                    reqHeight = 1300;
                }
            }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                rotationDegrees += 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                rotationDegrees += 270;
            }
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotationDegrees += 180;
        }

        rotationDegrees = rotationDegrees % 360;

        Bitmap normal = decodeSampledBitmapFromPath(filePath, reqWidth, reqHeight);
        if (rotationDegrees > 0) {
            Bitmap rotated = createRotatedBitmap(normal, rotationDegrees);

            normal.recycle();
            normal = null;

            return rotated;
        } else {
            return normal;
        }
    }


    public static float getRatio(String ratio) {
        if (ratio.contains(":")) {
            String[] rat = ratio.split(":");
            return Float.parseFloat(rat[0]) / Float.parseFloat(rat[1]);
        } else {
            return Float.parseFloat(ratio);
        }
    }

    public static int translateExifOrientationToDegrees(int exifOrientationConst) {
        int rotationDegrees = 0;
        if (exifOrientationConst != ExifInterface.ORIENTATION_UNDEFINED) {
            switch (exifOrientationConst) {
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationDegrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationDegrees = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationDegrees = 90;
                    break;
            }
        }

        return rotationDegrees;
    }

    public static int getImageOrientation(String filePath) {
        int orientation = ExifInterface.ORIENTATION_NORMAL;
        ExifInterface exifReader = null;
        try {
            exifReader = new ExifInterface(filePath);
            orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            //e.printStackTrace();
            CrashReportBase.sendCrashReport(e);
        }

        return orientation;
    }

    public static Bitmap loadRotatedBitmapFromPath(String filePath, float rotationDegrees) {
        Bitmap normal = BitmapFactory.decodeFile(filePath, null);
        Bitmap rotated = createRotatedBitmap(normal, rotationDegrees);

        normal.recycle();
        normal = null;

        return rotated;
    }

    public static Bitmap createRotatedBitmap(Bitmap srcBitmap, float rotationDegrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
    }

    public static Bitmap decodeSampledBitmapFromPath(String filePath, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSizeWithRatio(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap decodeSampledBitmapFromPath(String filePath, int reqWidth, int reqHeight, boolean checkAvailableMem, boolean callGc, boolean waitForGc) {
        long startTime = System.currentTimeMillis();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSizeWithRatio(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = null;

        boolean cacheCleared = false;

        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int inSampleSize = 0;

        while (true) {
            try {
                inSampleSize = options.inSampleSize;
                bitmap = BitmapFactory.decodeFile(filePath, options);
                break;
            } catch (Throwable t) {
                options.outWidth = outWidth;
                options.outHeight = outHeight;
                options.inSampleSize = inSampleSize;

                if (callGc) {
                    System.gc();
                }

                if (!cacheCleared) {
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.clearMemoryCache();
                    cacheCleared = true;
                } else if (options.outWidth < 350 || options.outHeight < 350) {
                    throw t;
                } else {
                    options.inSampleSize *= 2;
                }

            }
        }
        //ImageLogger.getInstance().setScale(Integer.toString(options.inSampleSize));
        return bitmap;
    }

    public static long getMemorySizeOfBitmap(int width, int height, int inSampleSize) {
        // For RGBA_8888 , Multiplication factor of 4 is used
        return (long) ((4 * width * height) / (double) (inSampleSize * inSampleSize));
    }

    public static long getMemorySizeOfBitmap(Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSizeWithRatio(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * @param options
     * @param reqWidth  Minimum width
     * @param reqHeight Minimum height
     * @return Largest sampleSize value that is a power of 2 and keeps both height and width larger than the requested height and width.
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static int calculateInSampleSizeWithRatio(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        float widthHeightSrcBitmapRatio = width / (float) height;
        float widthHeightDestRatio = reqWidth / (float) reqHeight;

        if (widthHeightSrcBitmapRatio > widthHeightDestRatio) {
            reqHeight = (int) (reqWidth / widthHeightSrcBitmapRatio);
        } else {
            reqWidth = (int) (widthHeightSrcBitmapRatio * reqHeight);
        }


        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Pair<Integer, Integer> getBitmapSize(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //Returns null, sizes are in the options variable

        BitmapFactory.decodeFile(imagePath, options);
        int width = options.outWidth;
        int height = options.outHeight;

        return new Pair<Integer, Integer>(width, height);
    }

    public static Pair<Bitmap, Integer> loadRotatedBitmapFromPathWithRotation(String filePath, int reqWidth, int reqHeight) {
        Bitmap normal = decodeSampledBitmapFromPath(filePath, reqWidth, reqHeight);
        int rotation = getRotationDegreesFromPhoto(filePath);

        return new Pair<Bitmap, Integer>(normal, rotation);
    }

    public static Pair<Bitmap, Integer> loadRotatedBitmapFromPathWithRotation(String filePath, int reqWidth, int reqHeight, boolean checkAvailableMem, boolean callGc, boolean waitForGc) {
        Bitmap normal = decodeSampledBitmapFromPath(filePath, reqWidth, reqHeight, checkAvailableMem, callGc, waitForGc);
        int rotation = getRotationDegreesFromPhoto(filePath);

        return new Pair<Bitmap, Integer>(normal, rotation);
    }

    public static int getRotationDegreesFromPhoto(String filePath) {
        int rotationDegrees = 0;
        int orientation = getImageOrientation(filePath);

        if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {


            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                rotationDegrees += 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                rotationDegrees += 270;
            }
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotationDegrees += 180;
        }

        rotationDegrees = rotationDegrees % 360;

        return rotationDegrees;
    }


    public static TextDrawable createRoundedTextDrawable(Context context, String contactName, int width, int height) {
        if (contactName == null)
            return null;

        int bgColor = ColorGenerator.MATERIAL.getColor(contactName);
        return createRoundedTextDrawable(context, contactName, width, height, bgColor);
    }

    public static TextDrawable createRoundedTextDrawable(Context context, String contactName, int width, int height, int bgColor) {
        if (android.text.TextUtils.isEmpty(contactName))
            return null;

        String nameInititals = Character.toString(contactName.charAt(0));
        boolean spaceEncountered = false;
        for (int i = 1; i < contactName.length(); i++) {

            if (spaceEncountered) {
                nameInititals = nameInititals + Character.toString(contactName.charAt(i));
                break;
            }

            if (Character.toString(contactName.charAt(i)).equals(" ")) {
                spaceEncountered = true;
            }
        }

        int marginInPix = context.getResources().getDimensionPixelSize(R.dimen.md_8dp_margin);

        return TextDrawable.builder().
                beginConfig().
                toUpperCase().
                width(width - marginInPix).
                height(height - marginInPix).
                endConfig().buildRound("" + nameInititals.charAt(0), bgColor);
    }

    public enum ImageRotate {
        Degrees_0(0), Degrees_90(90), Degrees_180(180), Degrees_270(270);

        private int angle;

        ImageRotate(int angle) {
            this.angle = angle;
        }

        public static ImageRotate fromDegrees(int degrees) {
            switch (degrees) {
                case 0: {
                    return Degrees_0;
                }
                case 90: {
                    return Degrees_90;
                }
                case 180: {
                    return Degrees_180;
                }
                case 270: {
                    return Degrees_270;
                }
            }

            return Degrees_0;
        }

        public int getDegrees() {
            return angle;
        }

        public ImageRotate rotateRightBy90Degrees() {
            if (this == Degrees_0) {
                return Degrees_90;
            } else if (this == Degrees_90) {
                return Degrees_180;
            } else if (this == Degrees_180) {
                return Degrees_270;
            } else if (this == Degrees_270) {
                return Degrees_0;
            }

            return Degrees_0;
        }
    }


}
