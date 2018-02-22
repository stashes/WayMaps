package com.waymaps.util;

import android.content.Context;

/**
 * Created by Admin on 03.02.2018.
 */

public class LocalPreferencesManagerUtil {

    public static void saveCredentials(String login, String password, boolean savePass , Context context) {
        LocalPreferenceManager.saveUsername(context, login);
        LocalPreferenceManager.setSavePassword(context, savePass);
        if (savePass) {
            LocalPreferenceManager.savePassword(context, password);
        } else
            LocalPreferenceManager.savePassword(context, "");
    }

    public static void clearCredentials(Context context) {
        LocalPreferenceManager.saveUsername(context, "");
        LocalPreferenceManager.setSavePassword(context, false);
        LocalPreferenceManager.savePassword(context, "");
    }
}
