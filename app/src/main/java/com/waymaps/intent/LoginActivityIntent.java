package com.waymaps.intent;

import android.content.Context;
import android.content.Intent;

import com.waymaps.activity.LoginActivity;
import com.waymaps.activity.MainActivity;

/**
 * Created by Admin on 06.03.2018.
 */

public class LoginActivityIntent extends Intent {
    public LoginActivityIntent(Context packageContext) {
        super(packageContext, LoginActivity.class);
    }
}
