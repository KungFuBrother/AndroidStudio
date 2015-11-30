package yitgogo.consumer.home.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Tiger
 * 
 * @JsonObject { "id": 1070, "address": "四川省成都市金牛区解放路二段", "img": "", "number":
 *             "YT117351743686", "shopname": "测试店铺一" }
 */
public class ModelLocalStore {

	String id = "", address = "", img = "", number = "", shopname = "";

	public ModelLocalStore() {
	}

	public ModelLocalStore(JSONObject object) throws JSONException {

		if (object != null) {
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("address")) {
				if (!object.getString("address").equalsIgnoreCase("null")) {
					address = object.optString("address");
				}
			}
			if (object.has("img")) {
				if (!object.getString("img").equalsIgnoreCase("null")) {
					img = object.optString("img");
				}
			}
			if (object.has("number")) {
				if (!object.getString("number").equalsIgnoreCase("null")) {
					number = object.optString("number");
				}
			}
			if (object.has("shopname")) {
				if (!object.getString("shopname").equalsIgnoreCase("null")) {
					shopname = object.optString("shopname");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}

	public String getImg() {
		return img;
	}

	public String getNumber() {
		return number;
	}

	public String getShopname() {
		return shopname;
	}

	@Override
	public String toString() {
		return "ModelLocalStore [id=" + id + ", address=" + address + ", img="
				+ img + ", number=" + number + ", shopname=" + shopname + "]";
	}

}
