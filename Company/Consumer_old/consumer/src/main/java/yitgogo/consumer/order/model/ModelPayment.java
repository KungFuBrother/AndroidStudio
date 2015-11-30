package yitgogo.consumer.order.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 支付方式
 * 
 * @author Tiger
 * 
 */
public class ModelPayment {

	public final static int TYPE_RECEIVED = 1;
	public final static int TYPE_ONLINE = 2;
	public final static int TYPE_CASH = 3;

	public final static String NAME_RECEIVED = "货到付款";
	public final static String NAME_ONLINE = "在线支付";
	public final static String NAME_CASH = "现金支付";

	int type = TYPE_ONLINE;
	String name = NAME_ONLINE;

	public ModelPayment() {
	}

	public ModelPayment(JSONObject object) {
		if (object != null) {
			type = object.optInt("type");
			name = object.optString("name");
		}
	}

	public ModelPayment(int type, String name) {
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
