package yitgogo.consumer.local.model;

import org.json.JSONObject;

/**
 * 本地特价促销
 * 
 * @author Tiger
 * 
 * @json { "promotionId": 7, "promotionName": "测试二", "promotionImg":
 *       "http://images.yitos.net/images/public/20150708/98591436346988459.png",
 *       "Numbers": 498, "promotionalPrice": 99.9, "productPrice": 399,
 *       "productName": "[演示商品]九阳方煲40FS16  WY-演示商品", "productId": 48 }
 */
public class ModelLocalSaleTejia {
	String promotionId = "", promotionName = "", promotionImg = "",
			productName = "", productId = "";
	int Numbers = 0;
	double promotionalPrice = 0, productPrice = 0;

	public ModelLocalSaleTejia(JSONObject object) {
		if (object != null) {
			if (object.has("promotionId")) {
				if (!object.optString("promotionId").equalsIgnoreCase("null")) {
					promotionId = object.optString("promotionId");
				}
			}
			if (object.has("promotionName")) {
				if (!object.optString("promotionName").equalsIgnoreCase("null")) {
					promotionName = object.optString("promotionName");
				}
			}
			if (object.has("promotionImg")) {
				if (!object.optString("promotionImg").equalsIgnoreCase("null")) {
					promotionImg = object.optString("promotionImg");
				}
			}
			if (object.has("productName")) {
				if (!object.optString("productName").equalsIgnoreCase("null")) {
					productName = object.optString("productName");
				}
			}
			if (object.has("productId")) {
				if (!object.optString("productId").equalsIgnoreCase("null")) {
					productId = object.optString("productId");
				}
			}
			if (object.has("Numbers")) {
				if (!object.optString("Numbers").equalsIgnoreCase("null")) {
					Numbers = object.optInt("Numbers");
				}
			}
			if (object.has("promotionalPrice")) {
				if (!object.optString("promotionalPrice").equalsIgnoreCase(
						"null")) {
					promotionalPrice = object.optDouble("promotionalPrice");
				}
			}
			if (object.has("productPrice")) {
				if (!object.optString("productPrice").equalsIgnoreCase("null")) {
					productPrice = object.optDouble("productPrice");
				}
			}
		}
	}

	public String getPromotionId() {
		return promotionId;
	}

	public String getPromotionName() {
		return promotionName;
	}

	public String getPromotionImg() {
		return promotionImg;
	}

	public String getProductName() {
		return productName;
	}

	public String getProductId() {
		return productId;
	}

	public int getNumbers() {
		return Numbers;
	}

	public double getPromotionalPrice() {
		return promotionalPrice;
	}

	public double getProductPrice() {
		return productPrice;
	}

}
