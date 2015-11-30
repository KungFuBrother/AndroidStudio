package yitgogo.consumer.tools;

import android.util.Log;

/**
 * logcat工具
 * 
 * @author Tiger
 * 
 */
public class LogUtil {

	public static boolean isLogEnable = true;

	public static void setLogEnable(boolean enable) {
		isLogEnable = enable;
	}

	public static void logInfo(String tag, String info) {
		if (isLogEnable) {
			Log.i(tag, info);
		}
	}

	public static void logError(String tag, String info) {
		if (isLogEnable) {
			Log.e(tag, info);
		}
	}

}
