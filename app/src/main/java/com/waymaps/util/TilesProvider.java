package com.waymaps.util;

import android.content.Context;

import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class TilesProvider {

    /** This returns moon tiles. */
    private static final String MOON_MAP_URL_FORMAT =
            "https://c.tile.openstreetmap.org/%d/%d/%d.png";


    public static TileProvider getTile(Context context) {
        if (MapProvider.valueOf(LocalPreferenceManager.getMapProvider(context)) == MapProvider.OSM) {
            return new CustomTileProvider(MOON_MAP_URL_FORMAT);
        } else {
            return null;
        }
    }
}
