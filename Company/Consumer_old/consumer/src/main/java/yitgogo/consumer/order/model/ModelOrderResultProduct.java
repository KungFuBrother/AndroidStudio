package yitgogo.consumer.order.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json {\"spname\":\"玖尼 2015年夏新款修身印花无袖圆领连体裤
 *       XY0182\",\"price\":\"194.0\",\"Amount\":\"194.0\",\"num\":\"1\"}
 */
public class ModelOrderResultProduct {

	String spname = "";
	double price = 0, Amount = 0;
	int num = 0;

	public ModelOrderResultProduct(JSONObject object) {
		if (object != null) {
			if (object.has("spname")) {
				if (!object.optString("spname").equalsIgnoreCase("null")) {
					spname = object.optString("spname");
				}
			}
			price = object.optDouble("price");
			Amount = object.optDouble("Amount");
			num = object.optInt("num");
		}
	}

	public String getSpname() {
		return spname;
	}

	public double getPrice() {
		return price;
	}

	public double getAmount() {
		return Amount;
	}

	public int getNum() {
		return num;
	}

}
