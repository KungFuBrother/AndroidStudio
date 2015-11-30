package yitgogo.consumer.home.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 批量查询价格
 * 
 * @author Tiger
 * 
 * @Json { "productNumber": "YT55918425666", "num": 58, "price": 257,
 *       "memberNumber": 0, "seckillNumber": 0, "bonusNum": 0, "productId":
 *       "206932" }
 */
public class ModelListPrice {

	String productNumber = "", productId = "";
	int num = 0, memberNumber = 0, seckillNumber = 0, bonusNum = 0;
	double price = 0;

	public ModelListPrice(JSONObject object) throws JSONException {
		if (object != null) {
			if (object.has("productNumber")) {
				if (!object.getString("productNumber").equalsIgnoreCase("null")) {
					productNumber = object.optString("productNumber");
				}
			}
			if (object.has("productId")) {
				if (!object.getString("productId").equalsIgnoreCase("null")) {
					productId = object.optString("productId");
				}
			}
			if (object.has("num")) {
				if (!object.getString("num").equalsIgnoreCase("null")) {
					num = object.optInt("num");
				}
			}
			if (object.has("memberNumber")) {
				if (!object.getString("memberNumber").equalsIgnoreCase("null")) {
					memberNumber = object.optInt("memberNumber");
				}
			}
			if (object.has("seckillNumber")) {
				if (!object.getString("seckillNumber").equalsIgnoreCase("null")) {
					seckillNumber = object.optInt("seckillNumber");
				}
			}
			if (object.has("bonusNum")) {
				if (!object.getString("bonusNum").equalsIgnoreCase("null")) {
					bonusNum = object.optInt("bonusNum");
				}
			}
			if (object.has("price")) {
				if (!object.getString("price").equalsIgnoreCase("null")) {
					price = object.optDouble("price");
				}
			}
		}
	}

	public String getProductNumber() {
		return productNumber;
	}

	public String getProductId() {
		return productId;
	}

	public int getNum() {
		return num;
	}

	public int getMemberNumber() {
		return memberNumber;
	}

	public int getSeckillNumber() {
		return seckillNumber;
	}

	public int getBonusNum() {
		return bonusNum;
	}

	public double getPrice() {
		return price;
	}

}
