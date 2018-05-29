package com.waymaps.util;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

public class TilesProvider {

    /** This returns moon tiles. */
    private static final String OSM_MAP =
            "https://c.tile.openstreetmap.org/%d/%d/%d.png";


    public static void setTile(GoogleMap mMap, Context context){
        if (MapProvider.valueOf(LocalPreferenceManager.getMapProvider(context)) == MapProvider.OSM) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            mMap.addTileOverlay(new TileOverlayOptions().
                    tileProvider(TilesProvider.getOSMTile(context)));
        } else if (MapProvider.valueOf(LocalPreferenceManager.getMapProvider(context)) == MapProvider.GOOGLE_SATELLITE){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (MapProvider.valueOf(LocalPreferenceManager.getMapProvider(context)) == MapProvider.GOOGLE_HYBRID){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    }

    public static TileProvider getOSMTile(Context context) {
        if (MapProvider.valueOf(LocalPreferenceManager.getMapProvider(context)) == MapProvider.OSM) {
            return new CustomTileProvider(OSM_MAP);
        } else {
            return null;
        }
    }
}
