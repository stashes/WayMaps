package com.waymaps.util;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class CustomTileProvider extends UrlTileProvider {

    String url = null;

    public CustomTileProvider(String url) {
        super(512, 512);
        this.url = url;
    }

    @Override
    public URL getTileUrl(int x, int y, int z) {
        String s = String.format(Locale.US, url,new Object[]{Integer.valueOf(z), Integer.valueOf(x), Integer.valueOf(y)});
        URL url = null;
        try {
            url = new URL(s);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        return url;
    }
}

