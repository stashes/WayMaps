package com.waymaps.intent;

import android.content.Context;
import android.content.Intent;

import com.waymaps.activity.MainActivity;

/**
 * Created by Admin on 03.02.2018.
 */

public class MainActivityIntent extends Intent{
    public MainActivityIntent(Context packageContext) {
        super(packageContext, MainActivity.class);
    }
}
