package com.waymaps.notification;

import android.app.Activity;
import android.widget.Toast;

import com.waymaps.data.requestEntity.Action;

/**
 * Created by nazar on 28.02.2018.
 */

public class NotificationManager {

    public static void showNotification(Activity activity, String text){
        Toast.makeText(activity , text , Toast.LENGTH_SHORT).show();
    }

}
