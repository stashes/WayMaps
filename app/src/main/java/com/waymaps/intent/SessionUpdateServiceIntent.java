package com.waymaps.intent;

import android.content.Context;
import android.content.Intent;

import com.waymaps.activity.MainActivity;

/**
 * Created by Admin on 10.02.2018.
 */

public class SessionUpdateServiceIntent extends Intent {
    public SessionUpdateServiceIntent(Context packageContext) {
        super(packageContext, SessionUpdateServiceIntent.class);
    }
}
