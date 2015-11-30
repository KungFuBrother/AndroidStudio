package yitgogo.consumer.home.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModelClass {

	String id = "", name = "", img = "";
	List<ModelClass> subClasses = new ArrayList<ModelClass>();

	public ModelClass(JSONObject object) throws JSONException {
		if (object.has("id")) {
			if (!(object.getString("id").equalsIgnoreCase("null") || object
					.getString("id").equalsIgnoreCase("undefined"))) {
				id = object.optString("id");
			}
		}
		if (object.has("name")) {
			if (!(object.getString("name").equalsIgnoreCase("null") || object
					.getString("name").equalsIgnoreCase("undefined"))) {
				name = object.optString("name");
			}
		}
		if (object.has("img")) {
			if (!(object.getString("img").equalsIgnoreCase("null") || object
					.getString("img").equalsIgnoreCase("undefined"))) {
				img = object.optString("img");
			}
		}
		if (object.has("pcSet")) {
			if (!(object.getString("pcSet").equalsIgnoreCase("null") || object
					.getString("pcSet").equalsIgnoreCase("undefined"))) {
				JSONArray subClassArray = object.optJSONArray("pcSet");
				if (subClassArray != null) {
					if (subClassArray.length() > 0) {
						for (int i = 0; i < subClassArray.length(); i++) {
							subClasses.add(new ModelClass(subClassArray
									.getJSONObject(i)));
						}
					}
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getImg() {
		return img;
	}

	public List<ModelClass> getSubClasses() {
		return subClasses;
	}

}
