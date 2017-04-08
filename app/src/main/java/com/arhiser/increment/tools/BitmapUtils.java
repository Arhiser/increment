package com.arhiser.increment.tools;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {

    private static final int MAX_PORTFOLIO_IMAGE_DIMENSION = 1024;

    private BitmapUtils()
    {}
/*
    public static DisplayImageOptions sAvatarDisplayImageOptions = new DisplayImageOptions.Builder()
            .showImageOnFail(R.drawable.img_avatar)
            .showImageForEmptyUri(R.drawable.img_avatar)
            .showImageOnLoading(R.drawable.progress_spinner_animated_small)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .build();
*/

    /**
     * Scales source bitmap so that it fit into the square of given size If
     * source is smaller that target square then the result will stretch to have
     * the size equal to the target
     * 
     * @param source
     *            bitmap to be scaled
     * @param size
     *            width and height of target bounding box
     * @return resulting bitmap
     */
    public static Bitmap scaleTo(Bitmap source, int size) {
        if (source == null || source.getWidth() <= 0 || source.getHeight() <= 0 ) {
            return null;
        }
        int destWidth = source.getWidth();
        int destHeight = source.getHeight();

        if (destHeight * size / destWidth >= size) {
            destHeight = destHeight * size / destWidth;
            destWidth = size;
        }
        else {
            destWidth = destWidth * size / destHeight;
            destHeight = size;
        }

        Bitmap destBitmap = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);
        if(destBitmap == null) return null;
        Canvas canvas = new Canvas(destBitmap);
        canvas.drawBitmap(source, new Rect(0, 0, source.getWidth(), source.getHeight()),
                new Rect(0, 0, destWidth, destHeight), new Paint(Paint.ANTI_ALIAS_FLAG));
        return destBitmap;
    }

    /**
     * Creates image masked by circle using clip path technique
     * 
     * @param source
     *            Source bitmap
     * @param radius
     *            Target circle radius
     * @return Resulting bitmap
     */
    public static Bitmap getCircleMaskedBitmapUsingClip(Bitmap source, int radius) {
        if (source == null) {
            return null;
        }
        if (radius <= 0) {
            radius = 64;
        }

        int diam = radius * 2;

        Bitmap scaledBitmap = scaleTo(source, diam);

        final Path path = new Path();
        path.addCircle(radius, radius, radius, Path.Direction.CCW);

        Bitmap targetBitmap = Bitmap.createBitmap(diam, diam, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);

        canvas.clipPath(path);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        canvas.drawBitmap(scaledBitmap, 0, 0, paint);

        return targetBitmap;
    }

    /**
     * Creates image masked by circle using clip path technique
     * 
     * @param source
     *            Source bitmap
     * @param radius
     *            Target circle radius
     * @return Resulting bitmap
     */
    public static Bitmap getCircleMaskedBitmapUsingShader(Bitmap source, int radius) {
        if (source == null) {
            return null;
        }
        if (radius <= 0) {
            radius = 64;
        }

        int diam = radius * 2;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Bitmap scaledBitmap = scaleTo(source, diam);
        final Shader shader = new BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        Bitmap targetBitmap = Bitmap.createBitmap(diam, diam, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);

        canvas.drawCircle(radius, radius, radius, paint);

        return targetBitmap;
    }

    public static Bitmap getCircleMaskedBitmapUsingPorterDuff(Bitmap source, int radius) {
        if (source == null) {
            return null;
        }
        if (radius <= 0) {
            radius = 64;
        }

        int diam = radius * 2;
        Bitmap scaledBitmap = scaleTo(source, diam);

        Bitmap targetBitmap = Bitmap.createBitmap(diam, diam, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);

        final int color = 0xff424242;
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Rect rect = new Rect(0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, rect, rect, paint);
        return targetBitmap;
    }

    public static Bitmap getScaledBitmap(Bitmap source, int radius) {
        if (source == null)
        {
            return null;
        }
        if (radius <= 0) {
            radius = 64;
        }

        int diam = radius * 2;

        Bitmap scaledBitmap = scaleTo(source, diam);
        return scaledBitmap;
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaColumns.DATA);
            result = cursor.getString(idx);
        }
        cursor.close();
        return result;
    }

    public static Bitmap decodeStreamScaled(InputStream inputStream, int size) {
        if (inputStream == null) {
            return null;
        }
        if (size <= 0) {
            size = 256;
        }
        inputStream.mark(Integer.MAX_VALUE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        try {
            inputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

        options.inSampleSize = calculateInSampleSize(options,
                size, size);
        options.inPurgeable = true;
        options.inInputShareable = true;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(inputStream, null,
                options);
    }

    /*
     * Use decodeFileScaled(ContentResolver contentResolver, Uri uri, int size) instead
     */
    public static Bitmap decodeFileScaled(String fullPath, int size) {
        if (fullPath == null) {
            return null;
        }
        if (size <= 0) {
            size = 256;
        }
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(fullPath);
        } catch (FileNotFoundException e) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        try {
            inputStream.close();
            inputStream = new FileInputStream(fullPath);
        } catch (Exception e) {
            return null;
        }

        options.inSampleSize = calculateInSampleSize(options,
                size, size);
        options.inPurgeable = true;
        options.inInputShareable = true;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(inputStream, null,
                options);
    }

    public static Bitmap decodeFileScaled(ContentResolver contentResolver, Uri uri, int size) {
        if (uri == null) {
            return null;
        }
        if (size <= 0) {
            size = 256;
        }
        InputStream inputStream;
        try {
            inputStream = contentResolver.openInputStream(uri);
        } catch (Exception e) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        try {
            inputStream.close();
            inputStream = contentResolver.openInputStream(uri);
        } catch (Exception e) {
            return null;
        }

        options.inSampleSize = calculateInSampleSize(options,
                size, size);
        options.inPurgeable = true;
        options.inInputShareable = true;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap returnBitmap =  BitmapFactory.decodeStream(inputStream, null, options);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnBitmap;
    }

    protected static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth ||
                height > 2048 || width > 2048) {

            // not 1/4 to prevent blur
            final int halfHeight = height / 4;
            final int halfWidth = width / 4;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    || (halfWidth / inSampleSize) > reqWidth
                    || (halfHeight * 4 / inSampleSize) > 2048
                    || (halfWidth * 4 / inSampleSize) > 2048) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static Drawable makeCircleAvatar(Bitmap bmp) {
        ShapeDrawable dr = new ShapeDrawable(new OvalShape());
        if (bmp != null) {
            dr.getPaint().setShader(new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            int size = 0;
            if (bmp.getWidth() > bmp.getHeight()) {
                size = bmp.getHeight();
            } else {
                size = bmp.getWidth();
            }
            dr.setIntrinsicWidth(size);
            dr.setIntrinsicHeight(size);
        }
        return dr;
    }

    public static Bitmap drawableToBitmap (Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Drawable makeShapeAvatar(Bitmap bmp) {
        if (bmp == null) {
            return null;
        }
        int height = bmp.getHeight();
        int width = bmp.getWidth();

        int newHeight = 0;
        int newWidth = 0;
        if (height < width) {
            newHeight = height;
            float value = (float) height / 20 * 21;
            newWidth = (int) value;
        } else {
            float value = (float) width / 21 * 20;
            newHeight = (int) value;
            newWidth = width;
        }
        if (newHeight > height) {
            newHeight = height;
        }

        if (newWidth > width) {
            newWidth = width;
        }

        Bitmap cropped = Bitmap.createBitmap(bmp, (width - newWidth) / 2, (height - newHeight) / 2, newWidth, newHeight);

        float[] round = {0f, 0f, newHeight / 2, newHeight / 2, newHeight / 2, newHeight / 2, 0f, 0f};
        RoundRectShape rect = new RoundRectShape(round, null, null);
        ShapeDrawable dr = new ShapeDrawable(rect);
        dr.getPaint().setShader(new BitmapShader(cropped, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        dr.setIntrinsicWidth(newWidth);
        dr.setIntrinsicHeight(newHeight);
        return dr;
    }

    public static Bitmap getBitmapFromFile(Context context, String uriStr) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int size = Math.max(dm.widthPixels, dm.heightPixels);
        return getBitmapFromFile(context, uriStr, size);
    }

    public static Bitmap getBitmapFromFile(Context context, String uriStr, int size) {
        if (TextUtils.isEmpty(uriStr)) {
            return null;
        }

        Bitmap bitmap;
        Uri uri = Uri.parse(uriStr);

        String path = "";
        if(uri != null) {
            path = Utils.getPath(context, uri);
            return BitmapUtils.decodeFileScaled(path, size);
        }
        return null;
    }

    public static Uri setBitmapToImageView(Activity activity, Intent data, ImageView dest)
    {
        if(data == null || data.getData() == null || dest == null) return null;

        Bundle extras = data.getExtras();

        Bitmap bitmap;
        Uri uri = data.getData();

        String path = "";
        if(uri != null)
        {
            path = Utils.getPath(activity, uri);
            bitmap = BitmapUtils.decodeFileScaled(path, dest.getWidth());
            dest.setImageDrawable(BitmapUtils.makeCircleAvatar(bitmap));
        }
        return Uri.parse(path);
    }

    public static Uri setBitmapToImageView(Activity activity, String uriStr, ImageView dest)
    {
        if(TextUtils.isEmpty(uriStr) || dest == null) return null;

        Uri uri = Uri.parse(uriStr);

        String path = "";
        if(uri != null) {
            path = Utils.getPath(activity, uri);
            Bitmap bitmap = BitmapUtils.decodeFileScaled(path, dest.getWidth());
            dest.setImageDrawable(BitmapUtils.makeCircleAvatar(bitmap));
        }
        return Uri.parse(path);
    }

    public static Uri setBitmapToImageView(Fragment fragment, Intent data, ImageView dest)
    {
        if(data == null || data.getData() == null || dest == null || !fragment.isAdded())
            return null;

        Bundle extras = data.getExtras();

        Bitmap bitmap;
        Uri uri = data.getData();
        /*
        InputStream is = null;

        if(uri.getAuthority() != null) {
            try {
                is = fragment.getActivity().getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                uri = writeToTempImageAndGetPathUri(fragment.getActivity(), bmp);
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(is != null)
                        is.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

        else if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            if (photo != null) {
                photo = BitmapUtils.scaleTo(photo, dest.getWidth());
                dest.setImageDrawable(BitmapUtils.makeShapeAvatar(photo));
            }
            //else {
            //Utils.showErrorAlert(getActivity(), getResources().getString(R.string.profile_wrong_photo));
            //}
        }
        */

        String path = "";
        if(uri != null)
        {
            path = Utils.getPath(fragment.getActivity(), uri);
            bitmap = BitmapUtils.decodeFileScaled(path, dest.getWidth());
            dest.setImageDrawable(BitmapUtils.makeShapeAvatar(bitmap));
        }

        return Uri.parse(path);
    }

    public static Uri getBitmapUriForFragment(Fragment fragment, Intent data)
    {
        if(data == null || data.getData() == null || !fragment.isAdded())
            return null;

        Uri uri = data.getData();
        /*
        //if(uri.getAuthority() != null) {
        try {
            Bitmap bmp = getCorrectlyOrientedImage(fragment.getActivity(), uri);
            uri = writeToTempImageAndGetPathUri(fragment.getActivity(), bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //}
        */
        return uri;
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage,
                "Title" + String.valueOf(System.currentTimeMillis()),
                null);
        return Uri.parse(path);
    }

    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        if(is != null) {
            BitmapFactory.decodeStream(is, null, dbo);
            is.close();
        }

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_PORTFOLIO_IMAGE_DIMENSION || rotatedHeight > MAX_PORTFOLIO_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_PORTFOLIO_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_PORTFOLIO_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        if(is != null)
            is.close();

    /*
     * if the orientation is not 0 (or -1, which means we don't know), we
     * have to do a rotation.
     */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }

    public static int getOrientation(Uri photoUri) {
        try {
            ExifInterface exifInterface = new ExifInterface(photoUri.getPath());
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            return -1;
        }
    }
}