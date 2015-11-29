package yitgogo.consumer.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Tiger on 2015-11-10.
 */
public class ApplicationTool {

    private static Toast toast;

    //show loginfo
    private static boolean debugMode = true;

    //screen info
    private static double scale;
    private static int screenWidth = 0, screenHeight = 0;

    //application info
    private static int versionCode = 0;
    private static String versionName = "";
    private static String packageName = "";

    public static void init(Context context) {
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);

        scale = context.getResources().getDisplayMetrics().density;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        try {
            packageName = context.getPackageName();
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showToast(String text) {
        toast.setText(text);
        toast.show();
    }

    public static void setDebugMode(boolean enable) {
        debugMode = enable;
    }

    public static void log(String tag, String info) {
        if (debugMode) {
            Log.i(tag, info);
        }
    }

    public static int dip2px(float dipValue) {
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getVersionCode() {
        return versionCode;
    }

    public static String getVersionName() {
        return versionName;
    }

    public static String getPackageName() {
        return packageName;
    }

}
