package yitgogo.smart.print;

import org.json.JSONObject;

/**
 * 打印数据中的产品
 * 
 * @author Tiger
 * 
 * @Json {"spname": "外贸童鞋 2015春款儿童运动鞋全能鞋休闲跑步鞋韩版男女童鞋B3175[颜色:红色-尺码:26码]"
 *       ,"price":135.0,"num":1,"Amount":135.0}
 */
public class PrintDataProduct {
	String spname = "";
	double price = 0, Amount = 0;
	int num = 0;

	public PrintDataProduct(JSONObject object) {
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