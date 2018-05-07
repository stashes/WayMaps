package com.waymaps.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waymaps.R;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Admin on 11.02.2018.
 */

public class ApplicationUtil {

    public static String datePattern = "yyyy-MM-dd HH:mm:ss";
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);

    public static Bundle setValueToBundle(Bundle bundle, String key, Object value) throws JsonProcessingException {
        bundle.putString(key, JSONUtil.getObjectMapper().writeValueAsString(value));
        return bundle;
    }

    public static <T> T getObjectFromBundle(Bundle bundle, String key, Class<T> tClass) throws IOException {
        if (bundle.getString(key)!=null)
        return JSONUtil.getObjectMapper().readValue(bundle.getString(key), tClass);
        else return null;
    }

    public static Bitmap drawToBitmap(Drawable drawable, int color) {
        if (!(drawable instanceof BitmapDrawable)) {
            Drawable mWrappedDrawable = drawable.mutate();
            mWrappedDrawable = DrawableCompat.wrap(mWrappedDrawable);
            DrawableCompat.setTint(mWrappedDrawable, color);
            DrawableCompat.setTintMode(mWrappedDrawable, PorterDuff.Mode.SRC_IN);

            Bitmap bitmap = Bitmap.createBitmap(mWrappedDrawable.getIntrinsicWidth(),
                    mWrappedDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;

        } else {
            BitmapDrawable bitmapdraw = (BitmapDrawable) drawable;
            Bitmap b = bitmapdraw.getBitmap();

            Bitmap mutableBitmap = b.copy(Bitmap.Config.ARGB_8888, true);

            Paint paint = new Paint();
            ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
            paint.setColorFilter(filter);

            Canvas canvas = new Canvas(mutableBitmap);
            canvas.drawBitmap(b, 0, 0, paint);
            return mutableBitmap;
        }
    }

    public static Bitmap drawToBitmap(Drawable drawable) {
        if (!(drawable instanceof BitmapDrawable)) {
            Drawable mWrappedDrawable = drawable.mutate();
            mWrappedDrawable = DrawableCompat.wrap(mWrappedDrawable);

            Bitmap bitmap = Bitmap.createBitmap(mWrappedDrawable.getIntrinsicWidth(),
                    mWrappedDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;

        } else {
            BitmapDrawable bitmapdraw = (BitmapDrawable) drawable;
            Bitmap b = bitmapdraw.getBitmap();

            Bitmap mutableBitmap = b.copy(Bitmap.Config.ARGB_8888, true);

            Paint paint = new Paint();

            Canvas canvas = new Canvas(mutableBitmap);
            canvas.drawBitmap(b, 0, 0, paint);
            return mutableBitmap;

        }
    }

    public static Bitmap drawToBitmap(Drawable d , int height,int width){
        Bitmap bitmap = drawToBitmap(d);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return resizedBitmap;
    }

    public static Bitmap drawToBitmap(Drawable d , int color, int height,int width){
        Bitmap bitmap = drawToBitmap(d,color);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return resizedBitmap;
    }

    public static int changeColorScaleTo16Int(String color) {
        int intColor = 100000;
        if (color != null) {
             intColor = Integer.parseInt(color);
        }
        String hexColor = String.format("#%06X", (0xFFFFFF & intColor));
        return  Color.parseColor(hexColor);
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }
    }

    public static Bitmap pickImage(Context context, double speed, String marker, String color) {
        Drawable drawable;
        if (speed > 5) {
            drawable = context.getResources().getDrawable(R.drawable.ic_marker_navigation);
        } else {
            if ("0".equals(marker)) {
                drawable = context.getResources().getDrawable(R.drawable.ic_0);
            } else if ("1".equals(marker)) {
                drawable = context.getResources().getDrawable(R.drawable.ic_1);
            } else if ("2".equals(marker)) {
                drawable = context.getResources().getDrawable(R.drawable.ic_2);
            } else if ("3".equals(marker)) {
                drawable = context.getResources().getDrawable(R.drawable.ic_3);
            } else if ("4".equals(marker)) {
                drawable = context.getResources().getDrawable(R.drawable.ic_4);
            } else if ("5".equals(marker)) {
                drawable = context.getResources().getDrawable(R.drawable.ic_5);
            } else if ("6".equals(marker)) {
                drawable = context.getResources().getDrawable(R.drawable.ic_6);
            } else {
                drawable = context.getResources().getDrawable(R.drawable.ic_0);
            }
        }

        int bitmapColor = ApplicationUtil.changeColorScaleTo16Int(color);

        return ApplicationUtil.drawToBitmap(drawable, bitmapColor);
    }

    public static void showToast(Context context,String text) {
        Toast toast = Toast.makeText(context,
                text,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
