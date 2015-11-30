package yitgogo.consumer.order.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelWuliuDetail {

	String time = "", context = "";

	public ModelWuliuDetail(JSONObject object) throws JSONException {
		// TODO Auto-generated constructor stub
		if (object.has("time")) {
			if (!object.getString("time").equalsIgnoreCase("null")) {
				time = object.optString("time");
			}
		}
		if (object.has("context")) {
			if (!object.getString("context").equalsIgnoreCase("null")) {
				context = object.optString("context");
			}
		}
	}

	public String getTime() {
		return time;
	}

	public String getContext() {
		return context;
	}

}
