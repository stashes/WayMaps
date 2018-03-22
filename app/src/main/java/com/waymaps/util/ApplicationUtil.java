package com.waymaps.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waymaps.R;
import com.waymaps.activity.MainActivity;

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
        return JSONUtil.getObjectMapper().readValue(bundle.getString(key), tClass);
    }

    public static Bitmap changeIconColor(Drawable drawable, int color) {
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

}
