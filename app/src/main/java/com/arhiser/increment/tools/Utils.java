package com.arhiser.increment.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.internal.util.Predicate;
import com.arhiser.increment.R;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import io.fabric.sdk.android.Fabric;
import rx.functions.Action1;

public class Utils {
    public static final float EPS_TIME = 10e-3f;

    // pixels
    public static float convertDpToPixel(float dp, Context context) {
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
            return 0;
        }

        DisplayMetrics metrics;
        try {
            Resources resources = context.getApplicationContext().getResources();
            metrics = resources.getDisplayMetrics();
        } catch (Exception e) {
            if (Fabric.isInitialized()) {
                Crashlytics.logException(e);
            }
            return 0;
        }

        return dp * (metrics.densityDpi / 160f);
    }

    public static int convertSpToPixel(float sp, Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return (int)(sp * dm.scaledDensity);
    }

    // String utils
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
/*
    public static String urlEncodeUTF8(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }

    public static String urlEncodeUTF8Pair(ArrayList<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (NameValuePair pair : params) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(pair.getName()),
                    urlEncodeUTF8(pair.getValue())
            ));
        }
        return sb.toString();
    }
*/
    public static String getPhoneInServerFormat(String phone) {
        if (phone == null) {
            return "";
        }
        StringBuilder result = new StringBuilder(25);
        for (int i = 0; i < phone.length(); ++i) {
            char c = phone.charAt(i);
            if (c == '+' && result.length() == 0) {
                result.append('+');
            } else if (c >= '0' && c <= '9') {
                result.append(c);
            }
        }
        /*if (result.length() > 0 && (result.charAt(0) == '8' || result.charAt(0) == '7')) {
            result = result.replace(0, 1, "+7");
        }*/
        return result.toString();
    }

    // Integer utils
    public static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // Date utils
    public static Calendar calendarFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar calendarFromMillis(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    public static boolean dateOfSameDay(Date date1, Date date2) {
        return dateOfSameDay(calendarFromDate(date1), calendarFromDate(date2));
    }

    public static boolean dateOfSameDay(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }

    public static int numberOfDaysBetweenDates(Calendar fromDay, Calendar toDay) {
        fromDay = calendarStartOfDay(fromDay);
        toDay = calendarStartOfDay(toDay);
        long from = fromDay.getTimeInMillis();
        long to = toDay.getTimeInMillis();
        return (int) TimeUnit.MILLISECONDS.toDays(to - from);
    }

    public static Calendar calendarStartOfDay(Calendar dateTime) {
        Calendar calendar = (Calendar) dateTime.clone();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar calendarStartOfMonth(Calendar dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return calendar;
    }

    public static Calendar calendarStartOfYear(Calendar dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.MONTH, 0);
        return calendar;
    }

    public static Calendar calendarStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return calendar;
    }

    // Equals
    public static boolean equalsStringsOrNull(String one, String other) {
        if (one == null && other == null) {
            return true;
        }
        if (one == null || other == null) {
            return false;
        }
        return one.equals(other);
    }

    public static boolean equalsListsOrNull(List one, List other) {
        if (one == null && other == null) {
            return true;
        }
        if (one == null || other == null) {
            return false;
        }
        if (one.size() != other.size()) {
            return false;
        }
        Iterator oneIt = one.iterator();
        Iterator otherIt = other.iterator();

        while (oneIt.hasNext()) {
            if (!oneIt.next().equals(otherIt.next())) {
                return false;
            }
        }

        return one.equals(other);
    }

    // Internet status
    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        try {
            if (activeNetwork.isConnectedOrConnecting() == false) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return activeNetwork.isConnectedOrConnecting();
    }


    // Asserts
    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static String getReadableTime(float time) {
        return String.format("%d:%02d", (int) time, (int) (60 * (time - (int) time)));
    }

    public static String getReadableDate(long mills) {
        return getReadableDate(new Date(mills));
    }

    public static String getReadableDateWeekDay(Date date) {
        Locale rusLocale = new Locale("ru", "RU");
        SimpleDateFormat readableFormat = new SimpleDateFormat("dd MMMM, EEEE", rusLocale);
        return readableFormat.format(date);
    }

    public static String getReadableDateTime(Date date) {
        Locale rusLocale = new Locale("ru", "RU");
        SimpleDateFormat readableFormat = new SimpleDateFormat("dd MMMM в k:mm", rusLocale);
        return readableFormat.format(date);
    }

    public static String getReadableDateTimeByTimeZone(Date date) {
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        int timeZoneOffset = timeZone.getRawOffset();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, timeZoneOffset);

        Locale rusLocale = new Locale("ru", "RU");
        SimpleDateFormat readableFormat = new SimpleDateFormat("dd MMMM в k:mm", rusLocale);
        return readableFormat.format(calendar.getTime());
    }

    public static String getReadableDate(Date date) {
        Locale rusLocale = new Locale("ru", "RU");
        SimpleDateFormat readableFormat = new SimpleDateFormat("dd MMMM", rusLocale);
        return readableFormat.format(date);
    }

    public static String getShortReadableDate(Date date) {
        Locale rusLocale = new Locale("ru", "RU");
        SimpleDateFormat readableFormat = new SimpleDateFormat("dd MMM", rusLocale);
        return readableFormat.format(date);
    }

    public static String getShortReadableDateSlots(Calendar calendar) {
        Locale rusLocale = new Locale("ru", "RU");
        SimpleDateFormat readableFormat = new SimpleDateFormat("dd MMM", rusLocale);
        String str = readableFormat.format(calendar.getTime()).replaceAll("\\.", "");
        str += ", " + calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        return str.toUpperCase();
    }

    public static String getReadableDateDotted(Date date) {
        Locale rusLocale = new Locale("ru", "RU");
        SimpleDateFormat readableFormat = new SimpleDateFormat("dd.MM.yy", rusLocale);
        return readableFormat.format(date);
    }

    public static String getServerTypeDate(Date date) {
        Locale rusLocale = new Locale("ru", "RU");
        SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd", rusLocale);

        return date != null ? serverDateFormat.format(date) : "";
    }

    public static String getReadableTime(Date date) {
        Locale rusLocale = new Locale("ru", "RU");
        SimpleDateFormat readableFormat = new SimpleDateFormat("k:mm", rusLocale);
        return readableFormat.format(date);
    }


    public static Drawable makeCircleAvatar(Bitmap bmp) {
        ShapeDrawable dr = new ShapeDrawable(new OvalShape());
        if (bmp != null) {
            dr.getPaint().setShader(new BitmapShader(bmp, TileMode.CLAMP, TileMode.CLAMP));
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

    public synchronized static Drawable makeShapeAvatar(Bitmap bmp) {
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
        dr.getPaint().setShader(new BitmapShader(cropped, TileMode.CLAMP, TileMode.CLAMP));
        dr.setIntrinsicWidth(newWidth);
        dr.setIntrinsicHeight(newHeight);
        return dr;
    }

    public ArrayList<String> getFilePaths(Context context) {
        ArrayList<String> filePaths = new ArrayList<String>();

        File directory = new File(
                android.os.Environment.getExternalStorageDirectory()
                        + File.separator + "ololo");

        // check for directory
        if (directory.isDirectory()) {
            // getting list of file paths
            File[] listFiles = directory.listFiles();

            // Check for count
            if (listFiles.length > 0) {

                // loop through all files
                for (int i = 0; i < listFiles.length; i++) {

                    // get file path
                    String filePath = listFiles[i].getAbsolutePath();
                    filePaths.add(filePath);

                }
            } else {
                // image directory is empty
                Toast.makeText(
                        context,
                        "ololo"
                                + " is empty. Please load some images in it !",
                        Toast.LENGTH_LONG).show();
            }

        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Error!");
            alert.setMessage("Attention!"
                    + " directory path is not valid! Please set the image directory name AppConstant.java class");
            alert.setPositiveButton("OK", null);
            alert.show();
        }

        return filePaths;
    }

    /*
     * getting screen width
     */
    @SuppressWarnings("deprecation")
    public int getScreenWidth(Context context) {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }


    /*
     * get path to image
     */

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            } else {
                return uri.toString();
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (uri.toString().contains("content://com.android.contacts/contacts/") || uri.toString().contains("content://com.google.android.apps.photos.content/")) {
                return uri.toString();
            } else {
                String path = getDataColumn(context, uri, null, null);
                if (path != null) {
                    return path;
                } else {
                    return copyFileFromUri(uri, context);
                }
            }

        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        } else {
            return uri.toString();
        }

        return null;
    }

    private static String attachExtensionToPath(ContentResolver resolver, String path, Uri uri) {
        if (!uri.getLastPathSegment().contains(".")) {
            String type = resolver.getType(uri);
            String[] typeParts = type.split("/");
            if (typeParts.length > 1) {
                path = path + "." + typeParts[1];
            }
        }
        return path;
    }

    public static String copyFileFromUri(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String cachePath = null;
        try {
            InputStream is = contentResolver.openInputStream(uri);
            cachePath = attachExtensionToPath(contentResolver, context.getCacheDir() + "/" + uri.getLastPathSegment(), uri);
            FileOutputStream fileOutputStream = new FileOutputStream(cachePath);

            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                fileOutputStream.write(buf, 0, len);
            }
            is.close();
            fileOutputStream.close();
        } catch (Exception e) {
            return null;
        }
        return cachePath;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }

        } catch (Exception e) {
            return uri.toString();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static Bitmap drawTextToBitmap(Context context, int resId, String text) {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(resources.getColor(android.R.color.white));//resources.getColor(R.color.views_background));
        // text size in pixels
        paint.setTextSize((int) (12 * scale));
        // text shadow
        //paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width() - 4) / 2 + 1;
        int y = (bitmap.getHeight() + 2 * bounds.height()) / 2 - 3;

        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    public final static boolean isValidEmail(CharSequence target, boolean allowEmpty) {
        if (TextUtils.isEmpty(target)) {
            return allowEmpty;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static String getValidNumber(String number) {
        number = number.replace(" ", "");
        number = number.replace("-", "");
        return number;
    }

    public static int getCountryPhoneCode(Context context) {
        String countryCode = getCountryLetterCode(context);
        return Constants.PHONE_CODE_BY_COUNTRY_CODE.get(countryCode);
    }

    public static String getCountryLetterCode(Context context) {
        TelephonyManager manager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        boolean isUseDefaultLocale = false;
        String countryCode = null;

        if(manager == null)
            isUseDefaultLocale = true;
        else
            countryCode = manager.getSimCountryIso().toUpperCase();

        if(isEmpty(countryCode) || isUseDefaultLocale)
            countryCode = Locale.getDefault().getCountry().toUpperCase();

        return countryCode;
    }

    public static String getPhoneNumber(Context context)
    {
        TelephonyManager manager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String number = "";
        try {
            number = manager.getLine1Number();
        } catch (SecurityException ex) {

        }

        return number;
    }


    public static void pleaseDumpMyBase(Context context)
    {
        File f = context.getDatabasePath("Salon3.db");
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try
        {
            fis = new FileInputStream(f);
            fos = new FileOutputStream(
                    new File(Environment.getExternalStorageDirectory().getPath(), "salon.db"));

            while(true)
            {
                int i = fis.read();
                if(i != -1) fos.write(i);
                else break;
            }
            fos.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            try
            {
                if(fos != null) fos.close();
                if(fis != null) fis.close();
            }
            catch(java.io.IOException ioe)
            {}
        }
    }

    public static String getPhoneFormatted(Context context, String fullNumber, String countryCode) {
        Pair<String, String> pair = Utils.parseFullPhone(context, fullNumber, countryCode);
        if (TextUtils.isEmpty(pair.first)) {
            return fullNumber;
        }

        if (fullNumber.startsWith("8")) {
            String prefix = "8";
            String pattern = " (###) ###-##-####";
            String suffix = fullNumber.substring(prefix.length(), fullNumber.length());

            StringBuilder sb = new StringBuilder();
            sb.append(prefix);
            for (int i = 0, p = 0; i < suffix.length() && p < pattern.length();) {
                if (pattern.charAt(p) != '#') {
                    sb.append(pattern.charAt(p));
                } else {
                    sb.append(suffix.charAt(i));
                    i++;
                }
                p++;
            }
            return sb.toString();
        }

        String prefix = "+" + Integer.toString(Constants.PHONE_CODE_BY_COUNTRY_CODE.get(pair.first));
        String pattern = " (###) ###-##-####";
        String suffix = fullNumber.substring(prefix.length(), fullNumber.length());

        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        for (int i = 0, p = 0; i < suffix.length() && p < pattern.length();) {
            if (pattern.charAt(p) != '#') {
                sb.append(pattern.charAt(p));
            } else {
                sb.append(suffix.charAt(i));
                i++;
            }
            p++;
        }
        return sb.toString();
    }

    public static Pair<String, String> parseFullPhone(Context context, String fullNumber, String countryCode)
    {
        String countryLetterCode = "";
        String phone = new String(fullNumber);
        boolean fullPhone = phone.startsWith("+");
        if(phone.startsWith("+")) phone = phone.substring(1); // if starts with plus, just remove it and continue parsing
        else if(phone.startsWith("8"))
        {
            // if starts with 8, replace it for current country code
            countryLetterCode = Utils.getCountryLetterCode(context);
            phone = phone.substring(1);

            return new Pair<>(countryLetterCode, phone);
        }
        else if(!phone.startsWith("+") && (phone.length() < 11) && !fullPhone)
        {
            // if we have a short number, suggest that it does not contain country code.
            // so return null countryLetterCode
            return new Pair<>(null, phone);
        }

        ArrayList<String> codes = new ArrayList<>();
        for(String country : Constants.PHONE_CODE_BY_COUNTRY_CODE.keySet())
        {
            String countryPhoneCodeString =
                    String.valueOf(Constants.PHONE_CODE_BY_COUNTRY_CODE.get(country));
            if (phone.startsWith(countryPhoneCodeString)) {
                codes.add(country);
            }
        }
        if (codes.size() == 1) {
            countryLetterCode = codes.get(0);
        } else if (codes.size() > 1) {
            countryLetterCode = countryCode;
            if (!codes.contains(countryCode)) {
                countryLetterCode = codes.get(0);
            }

            if (TextUtils.isEmpty(countryLetterCode)) {
                countryLetterCode = codes.get(0);
            }
        }
        phone = phone.substring(String.valueOf(Constants.PHONE_CODE_BY_COUNTRY_CODE.get(countryLetterCode)).length());

        return new Pair<>(countryLetterCode, phone);
    }

    public static void choosePhotoForFragment(Fragment fragment, int resultCode)
    {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        try {
            if(fragment != null && fragment.isAdded())
                fragment.startActivityForResult(Intent.createChooser(intent, fragment.getString(R.string.choose_image)), resultCode);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public static void choosePhotoForActivity(Activity activity, int resultCode) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.choose_image)), resultCode);
    }

    public static ArrayList<String> extractLinksFromString(String text) {
        ArrayList<String> links = new ArrayList<>();
        Matcher matcher = Patterns.WEB_URL.matcher(text);
        while (matcher.find()) {
            String url = matcher.group();
            links.add(url);
        }

        return links;
    }

    public static void goToSite(Context context, String url)
    {
        if(Utils.isEmpty(url)) return;

        try {
            if (!url.contains("http://")) {
                url = "http://" + url;
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static
    <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }

    public static Date getMaxCalendarDate() {
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        nextYear.add(Calendar.MONTH, 1);
        nextYear = calendarStartOfMonth(nextYear);
        return nextYear.getTime();
    }

    public static Date getMinCalendarDate() {
        Calendar lastYear = Calendar.getInstance();
        lastYear = calendarStartOfMonth(lastYear);
        lastYear.set(Calendar.YEAR, 2015);
        lastYear.set(Calendar.MONTH, 0);
        lastYear.add(Calendar.DAY_OF_YEAR, 1);
        return lastYear.getTime();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String toListString(List<? extends Object> objects) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objects.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(objects.get(i).toString());
        }
        return sb.toString();
    }

    public static <T> T find(Iterable<T> collection, Predicate<T> criteria) {
        for (T item: collection) {
            if (criteria.apply(item)) {
                return item;
            }
        }
        return null;
    }

    public static <T> void forEach(Iterable<T> collection, Action1<T> action) {
        for (T item: collection) {
            action.call(item);
        }
    }

    public static <T> ArrayList<T> filterList(List<T> source, Predicate<T> criteria) {
        ArrayList<T> dest = new ArrayList<>();
        for (T item: source) {
            if (criteria.apply(item)) {
                dest.add(item);
            }
        }
        return dest;
    }
}
