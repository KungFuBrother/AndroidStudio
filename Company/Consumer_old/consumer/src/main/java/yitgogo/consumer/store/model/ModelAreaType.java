package yitgogo.consumer.store.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelAreaType {

	// {"id":2,"typename":"省"}

	String typename = "";
	int id = -1;

	public ModelAreaType() {
	}

	public ModelAreaType(JSONObject object) throws JSONException {
		if (object.has("id")) {
			if (!object.getString("id").equalsIgnoreCase("null")) {
				id = object.optInt("id");
			}
		}
		if (object.has("typename")) {
			if (!object.getString("typename").equalsIgnoreCase("null")) {
				typename = object.optString("typename");
			}
		}
	}

	public int getId() {
		return id;
	}

	public String getTypename() {
		return typename;
	}

}
