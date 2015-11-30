package yitgogo.consumer.order.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json {\ "spname\":\"产品组-测试产品\",\"price\":\"33.0\",\"Amount\":\"66.0\",\"num
 *       \ " : \ " 2 \ " }
 */
public class ModelLocalGoodsOrderResultGoods {
	String spname = "";
	double price = 0, Amount = 0;
	int num = 0;

	public ModelLocalGoodsOrderResultGoods(JSONObject object) {
		if (object != null) {
			if (object.has("spname")) {
				if (!object.optString("spname").equalsIgnoreCase("null")) {
					spname = object.optString("spname");
				}
			}
			if (object.has("price")) {
				if (!object.optString("price").equalsIgnoreCase("null")) {
					price = object.optDouble("price");
				}
			}
			if (object.has("Amount")) {
				if (!object.optString("Amount").equalsIgnoreCase("null")) {
					Amount = object.optDouble("Amount");
				}
			}
			if (object.has("num")) {
				if (!object.optString("num").equalsIgnoreCase("null")) {
					num = object.optInt("num");
				}
			}
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
