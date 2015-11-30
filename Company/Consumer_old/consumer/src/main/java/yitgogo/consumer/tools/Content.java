package yitgogo.consumer.tools;

import android.content.Context;
import android.content.SharedPreferences;

import yitgogo.consumer.user.model.VersionInfo;

public class Content {

    static SharedPreferences contentPreferences;

    public static void init(Context context) {
        contentPreferences = context.getSharedPreferences("contentPreferences",
                Context.MODE_PRIVATE);
    }

    public static void saveStringContent(String key, String value) {
        contentPreferences.edit().putString(key, value).commit();
    }

    public static void saveIntContent(String key, int value) {
        contentPreferences.edit().putInt(key, value).commit();
    }

    public static void saveBooleanContent(String key, Boolean value) {
        contentPreferences.edit().putBoolean(key, value).commit();
    }

    public static String getStringContent(String key, String defValue) {
        return contentPreferences.getString(key, defValue);
    }

    public static int getIntContent(String key, int defValue) {
        return contentPreferences.getInt(key, defValue);
    }

    public static boolean getBooleanContent(String key, Boolean defValue) {
        return contentPreferences.getBoolean(key, defValue);
    }

    public static boolean contain(String key) {
        return contentPreferences.contains(key);
    }

    public static void removeContent(String key) {
        contentPreferences.edit().remove(key).commit();
    }
}
