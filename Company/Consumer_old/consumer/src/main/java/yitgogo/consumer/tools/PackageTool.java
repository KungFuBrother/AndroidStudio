package yitgogo.consumer.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Tiger on 2015-10-29.
 */
public class PackageTool {

    private static int versionCode = 0;
    private static String versionName = "default";
    private static String packageName = "default";

    public static void init(Context context) {

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
