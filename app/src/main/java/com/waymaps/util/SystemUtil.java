package com.waymaps.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;

/**
 * Created by Admin on 03.02.2018.
 */

public class SystemUtil {
    public static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        return "Android " + release;
    }

    public static String getWifiMAC(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getMacAddress();
    }


}
