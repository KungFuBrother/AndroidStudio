package yitgogo.consumer.product.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelSaleTimeProduct {

	String promotionName = "", protionImg = "", productName = "",
			productId = "", productNumber = "", showtime = "", starttime = "",
			endtime = "";

	public ModelSaleTimeProduct(JSONObject object) throws JSONException {
		// TODO Auto-generated constructor stub
		if (object.has("promotionName")) {
			if (!object.getString("promotionName").equalsIgnoreCase("null")) {
				promotionName = object.optString("promotionName");
			}
		}
		if (object.has("protionImg")) {
			if (!object.getString("protionImg").equalsIgnoreCase("null")) {
				protionImg = object.optString("protionImg");
			}
		}
		if (object.has("productName")) {
			if (!object.getString("productName").equalsIgnoreCase("null")) {
				productName = object.optString("productName");
			}
		}
		if (object.has("productId")) {
			if (!object.getString("productId").equalsIgnoreCase("null")) {
				productId = object.optString("productId");
			}
		}
		if (object.has("productNumber")) {
			if (!object.getString("productNumber").equalsIgnoreCase("null")) {
				productNumber = object.optString("productNumber");
			}
		}
		if (object.has("showtime")) {
			if (!object.getString("showtime").equalsIgnoreCase("null")) {
				showtime = object.optString("showtime");
			}
		}
		if (object.has("starttime")) {
			if (!object.getString("starttime").equalsIgnoreCase("null")) {
				starttime = object.optString("starttime");
			}
		}
		if (object.has("endtime")) {
			if (!object.getString("endtime").equalsIgnoreCase("null")) {
				endtime = object.optString("endtime");
			}
		}
	}

	public String getPromotionName() {
		return promotionName;
	}

	public String getProtionImg() {
		return protionImg;
	}

	public String getProductName() {
		return productName;
	}

	public String getProductId() {
		return productId;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public String getShowtime() {
		return showtime;
	}

	public String getStarttime() {
		return starttime;
	}

	public String getEndtime() {
		return endtime;
	}
}
