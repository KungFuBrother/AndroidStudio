package yitgogo.consumer.product.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 特价促销详情
 * 
 * @author Tiger
 * 
 * @json { "message": "ok", "state": "SUCCESS", "cacheKey": null, "dataList":
 *       [], "totalCount": 1, "dataMap": { "id": 6, "salePromotionName": "121",
 *       "price": 2899, "numbers": 2, "productDetais": "", "productImg": [ {
 *       "id": 402140, "imgName":
 *       "http://images.yitos.net/images/public/20140908/57861410170930154.jpg"
 *       }, { "id": 402139, "imgName":
 *       "http://images.yitos.net/images/public/20140908/57711410170931213.jpg"
 *       }, { "id": 402141, "imgName":
 *       "http://images.yitos.net/images/public/20140908/40891410170933517.jpg"
 *       } ], "attribute": "白色37码", "salePrice": 0, "number": "YT25050861150",
 *       "productName": "Haier/海尔XQG70-1279滚筒7.0公斤 全自动洗衣机防霉菌 全国联保（仅限在系统中下单有效！）"
 *       }, "object": null }
 */
public class ModelSaleDetailTejia {

	String type = "特价促销";
	double price = -1, salePrice = -1;
	long numbers = -1;
	String id = "", salePromotionName = "", productDetais = "", attribute = "",
			number = "", productName = "";
	List<ModelProductImage> images = new ArrayList<ModelProductImage>();

	public ModelSaleDetailTejia() {
	}

	public ModelSaleDetailTejia(String result) throws JSONException {
		if (result.length() > 0) {
			JSONObject resultObject = new JSONObject(result);
			if (resultObject != null) {
				if (resultObject.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONObject object = resultObject.optJSONObject("dataMap");
					if (object != null) {
						if (object.has("price")) {
							if (!object.optString("price").equalsIgnoreCase(
									"null")) {
								price = object.optDouble("price");
							}
						}
						if (object.has("salePrice")) {
							if (!object.optString("salePrice")
									.equalsIgnoreCase("null")) {
								salePrice = object.optDouble("salePrice");
							}
						}
						if (object.has("numbers")) {
							if (!object.optString("numbers").equalsIgnoreCase(
									"null")) {
								numbers = object.optLong("numbers");
							}
						}
						if (object.has("id")) {
							if (!object.optString("id")
									.equalsIgnoreCase("null")) {
								id = object.optString("id");
							}
						}
						if (object.has("salePromotionName")) {
							if (!object.optString("salePromotionName")
									.equalsIgnoreCase("null")) {
								salePromotionName = object
										.optString("salePromotionName");
							}
						}
						if (object.has("productDetais")) {
							if (!object.optString("productDetais")
									.equalsIgnoreCase("null")) {
								productDetais = object
										.optString("productDetais");
							}
						}
						if (object.has("attribute")) {
							if (!object.optString("attribute")
									.equalsIgnoreCase("null")) {
								attribute = object.optString("attribute");
							}
						}
						if (object.has("number")) {
							if (!object.optString("number").equalsIgnoreCase(
									"null")) {
								number = object.optString("number");
							}
						}
						if (object.has("productName")) {
							if (!object.optString("productName")
									.equalsIgnoreCase("null")) {
								productName = object.optString("productName");
							}
						}
						JSONArray array = object.optJSONArray("productImg");
						if (array != null) {
							for (int i = 0; i < array.length(); i++) {
								JSONObject image = array.optJSONObject(i);
								if (image != null) {
									images.add(new ModelProductImage(image));
								}
							}
						}
					}
				}
			}
		}
	}

	public String getType() {
		return type;
	}

	public double getPrice() {
		return price;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public String getId() {
		return id;
	}

	public String getSalePromotionName() {
		return salePromotionName;
	}

	public String getProductDetais() {
		return productDetais;
	}

	public String getAttribute() {
		return attribute;
	}

	public String getNumber() {
		return number;
	}

	public long getNumbers() {
		return numbers;
	}

	public String getProductName() {
		return productName;
	}

	public List<ModelProductImage> getImages() {
		return images;
	}

	@Override
	public String toString() {
		return "ModelSaleDetailTejia [type=" + type + ", price=" + price
				+ ", salePrice=" + salePrice + ", numbers=" + numbers + ", id="
				+ id + ", salePromotionName=" + salePromotionName
				+ ", productDetais=" + productDetais + ", attribute="
				+ attribute + ", number=" + number + ", productName="
				+ productName + ", images=" + images + "]";
	}

}
