package yitgogo.consumer.local.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @JsonObject "localOrderProductInfoSet": [ { "id": 1, "productName":
 *             "测试产品本地团购", "productNumber": "YT2342424", "productType": "服务",
 *             "productUnitPrice": 2500, "productNum": 1, "consumptionInfo":
 *             "111111111", "img": null } ]
 * 
 */
public class ModelLocalServiceOrderGoods {

	String id = "", productName = "", productNumber = "", productType = "",
			consumptionInfo = "", img = "";
	double productUnitPrice = 0;
	int productNum = 0;
	JSONObject jsonObject = new JSONObject();

	public ModelLocalServiceOrderGoods() {
	}

	public ModelLocalServiceOrderGoods(JSONObject object) throws JSONException {

		if (object != null) {
			this.jsonObject = object;
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("productName")) {
				if (!object.getString("productName").equalsIgnoreCase("null")) {
					productName = object.optString("productName");
				}
			}
			if (object.has("productNumber")) {
				if (!object.getString("productNumber").equalsIgnoreCase("null")) {
					productNumber = object.optString("productNumber");
				}
			}
			if (object.has("productType")) {
				if (!object.getString("productType").equalsIgnoreCase("null")) {
					productType = object.optString("productType");
				}
			}
			if (object.has("productUnitPrice")) {
				if (!object.getString("productUnitPrice").equalsIgnoreCase(
						"null")) {
					productUnitPrice = object.optDouble("productUnitPrice");
				}
			}
			if (object.has("productNum")) {
				if (!object.getString("productNum").equalsIgnoreCase("null")) {
					productNum = object.optInt("productNum");
				}
			}
			if (object.has("consumptionInfo")) {
				if (!object.getString("consumptionInfo").equalsIgnoreCase(
						"null")) {
					consumptionInfo = object.optString("consumptionInfo");
				}
			}
			if (object.has("img")) {
				if (!object.getString("img").equalsIgnoreCase("null")) {
					img = object.optString("img");
				}
			}
		}

	}

	public String getId() {
		return id;
	}

	public String getProductName() {
		return productName;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public String getProductType() {
		return productType;
	}

	public String getConsumptionInfo() {
		return consumptionInfo;
	}

	public String getImg() {
		return img;
	}

	public double getProductUnitPrice() {
		return productUnitPrice;
	}

	public int getProductNum() {
		return productNum;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public String toString() {
		return "ModelLocalServiceOrderGoods [id=" + id + ", productName="
				+ productName + ", productNumber=" + productNumber
				+ ", productType=" + productType + ", consumptionInfo="
				+ consumptionInfo + ", img=" + img + ", productUnitPrice="
				+ productUnitPrice + ", productNum=" + productNum
				+ ", jsonObject=" + jsonObject + "]";
	}

}