package yitgogo.consumer.order.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 配送方式
 * 
 * @author Tiger
 * 
 */
public class ModelDiliver {

	public final static int TYPE_SELF = 0;
	public final static int TYPE_HOME = 1;

	public final static String NAME_SELF = "自取";
	public final static String NAME_HOME = "送货上门";

	int type = TYPE_SELF;
	String name = NAME_SELF;

	public ModelDiliver() {
	}

	public ModelDiliver(JSONObject object) {
		if (object != null) {
			type = object.optInt("type");
			name = object.optString("name");
		}
	}

	public ModelDiliver(int type, String name) {
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public JSONObject toJsonObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", type);
		jsonObject.put("name", name);
		return jsonObject;
	}

}
