package com.smartown.controller.mission;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Smartown on 15/11/29.
 */
public class CookieController {

    private static SharedPreferences cookiePreferences;

    public static void init(Context context) {
        cookiePreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
    }

    public static void saveCookie(String host, String cookie) {
        cookiePreferences.edit().putString(host, cookie).commit();
    }

    public static String getCookie(String host) {
        return cookiePreferences.getString(host, "");
    }

}
