package yitgogo.consumer.home.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelAds {
	/*
	 * {"id":"1","adverImg":
	 * "http:\/\/images.yitos.net\/images\/public\/20141231\/2641420012135427.jpg","defaultImg":"http:\/\/images.yitos.net\/images\/public\/20141124\/97161416824657992.jpg","type":"产品","defaultUrl":"48","advername":"美啦啦啦啊","adverUrl":"38"}
	 */

	private String id = "", adverImg = "", defaultImg = "", type = "",
			adverUrl = "", defaultUrl = "", advername = "";

	public ModelAds(JSONObject object) throws JSONException {
		// TODO Auto-generated constructor stub
		if (object.has("id")) {
			if (!object.getString("id").equalsIgnoreCase("null")) {
				id = object.optString("id");
			}
		}
		if (object.has("adverImg")) {
			if (!object.getString("adverImg").equalsIgnoreCase("null")) {
				adverImg = object.optString("adverImg");
			}
		}
		if (object.has("defaultImg")) {
			if (!object.getString("defaultImg").equalsIgnoreCase("null")) {
				defaultImg = object.optString("defaultImg");
			}
		}
		if (object.has("type")) {
			if (!object.getString("type").equalsIgnoreCase("null")) {
				type = object.optString("type");
			}
		}
		if (object.has("adverUrl")) {
			if (!object.getString("adverUrl").equalsIgnoreCase("null")) {
				adverUrl = object.optString("adverUrl");
			}
		}
		if (object.has("defaultUrl")) {
			if (!object.getString("defaultUrl").equalsIgnoreCase("null")) {
				defaultUrl = object.optString("defaultUrl");
			}
		}
		if (object.has("advername")) {
			if (!object.getString("advername").equalsIgnoreCase("null")) {
				advername = object.optString("advername");
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getAdverImg() {
		return adverImg;
	}

	public String getDefaultImg() {
		return defaultImg;
	}

	public String getType() {
		return type;
	}

	public String getAdverUrl() {
		return adverUrl;
	}

	public String getDefaultUrl() {
		return defaultUrl;
	}

	public String getAdvername() {
		return advername;
	}

}
