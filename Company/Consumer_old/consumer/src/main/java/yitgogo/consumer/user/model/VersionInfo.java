package yitgogo.consumer.user.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 服务器版本信息
 * 
 * @author Tiger
 * 
 * @Json [{"verName":"ytgogo_consumer.1.9.5"
 *       ,"verCode":14,"message":"修复无法查看商品分类的问题","grade":2}]
 */
public class VersionInfo {

	String verName = "", message = "";
	int verCode = 0, grade = 0;

	public VersionInfo(String resultJson) {
		if (!TextUtils.isEmpty(resultJson)) {
			try {
				JSONArray array = new JSONArray(resultJson);
				if (array.length() > 0) {
					JSONObject object = array.optJSONObject(0);
					if (object != null) {
						if (object.has("verName")) {
							if (!object.optString("verName").equalsIgnoreCase(
									"null")) {
								verName = object.optString("verName");
							}
						}
						if (object.has("message")) {
							if (!object.optString("message").equalsIgnoreCase(
									"null")) {
								message = object.optString("message");
							}
						}
						verCode = object.optInt("verCode");
						grade = object.optInt("grade");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public VersionInfo() {
	}

	public String getVerName() {
		return verName;
	}

	public String getMessage() {
		return message;
	}

	public int getVerCode() {
		return verCode;
	}

	public int getGrade() {
		return grade;
	}

	@Override
	public String toString() {
		return "VersionInfo [verName=" + verName + ", message=" + message
				+ ", verCode=" + verCode + "]";
	}

}