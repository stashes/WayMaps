package com.waymaps.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waymaps.R;
import com.waymaps.activity.MainActivity;

import java.io.IOException;

/**
 * Created by Admin on 11.02.2018.
 */

public class ApplicationUtil {

    public static Bundle setValueToBundle(Bundle bundle, String key, Object value) throws JsonProcessingException {
        bundle.putString(key, JSONUtil.getObjectMapper().writeValueAsString(value));
        return bundle;
    }

    public static <T> T getObjectFromBundle(Bundle bundle, String key, Class<T> tClass) throws IOException {
        return JSONUtil.getObjectMapper().readValue(bundle.getString(key), tClass);
    }

    public static Bitmap changeIconColor(Drawable drawable, int color) {
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
