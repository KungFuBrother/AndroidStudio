package yitgogo.consumer.local.model;

import org.json.JSONObject;

/**
 * 本地秒杀促销
 * 
 * @author Tiger
 * 
 * @json { "promotionId": 7, "spikeTime": 1441555200000, "promotionName": "史泰龙",
 *       "promotionImg":
 *       "http://images.yitos.net/images/public/20150908/49261441714605041.jpg",
 *       "Numbers": 499, "promotionalPrice": 111, "productPrice": 3288,
 *       "productName": "[演示商品]华津时代源之圆直饮机超霸自吸式和普通式HJ-SY-100G-演示商品", "productId":
 *       23 }
 */
public class ModelLocalSaleMiaosha {
	String promotionId = "", promotionName = "", promotionImg = "",
			productName = "", productId = "";
	int Numbers = 0;
	double promotionalPrice = 0, productPrice = 0;
	long spikeTime = 0;

	public ModelLocalSaleMiaosha(JSONObject object) {
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
			if (object.has("spikeTime")) {
				if (!object.optString("spikeTime").equalsIgnoreCase("null")) {
					spikeTime = object.optLong("spikeTime");
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

	public long getSpikeTime() {
		return spikeTime;
	}

}
