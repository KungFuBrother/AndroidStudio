package yitgogo.consumer.product.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 限时促销详情
 * 
 * @author Tiger
 * 
 * @json 
 *       {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount"
 *       :1,"dataMap":{"startTime":1437408000000,"id":33513,"price":444.0,
 *       "promotionName":"测试","productImg":[{"id":167379,"imgName":
 *       "http://images.yitos.net/images/public/20150608/4441433747842430.png"
 *       }],"productDetails":"saddfasfsdddddddddddddddddddddddddddddddddd",
 *       "attribute":"黑色-25码","number":"YT81764243379","endTime":1438272000000,
 *       "productName":"测试促销分类1 ","promotionPrice":6.0},"object":null}
 */
public class ModelSaleDetailTime {

	String type = "限时促销";
	long startTime = 0, endTime = 0;
	double price = 0, promotionPrice = 0;
	String id = "", promotionName = "", productDetails = "", attribute = "",
			number = "", productName = "";
	List<ModelProductImage> images = new ArrayList<ModelProductImage>();

	public ModelSaleDetailTime() {
	}

	public ModelSaleDetailTime(String result) throws JSONException {
		if (result.length() > 0) {
			JSONObject resultObject = new JSONObject(result);
			if (resultObject != null) {
				if (resultObject.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONObject object = resultObject.optJSONObject("dataMap");
					if (object != null) {
						if (object.has("startTime")) {
							if (!object.optString("startTime")
									.equalsIgnoreCase("null")) {
								startTime = object.optLong("startTime");
							}
						}
						if (object.has("endTime")) {
							if (!object.optString("endTime").equalsIgnoreCase(
									"null")) {
								endTime = object.optLong("endTime");
							}
						}
						if (object.has("price")) {
							if (!object.optString("price").equalsIgnoreCase(
									"null")) {
								price = object.optDouble("price");
							}
						}
						if (object.has("promotionPrice")) {
							if (!object.optString("promotionPrice")
									.equalsIgnoreCase("null")) {
								promotionPrice = object
										.optDouble("promotionPrice");
							}
						}
						if (object.has("id")) {
							if (!object.optString("id")
									.equalsIgnoreCase("null")) {
								id = object.optString("id");
							}
						}
						if (object.has("promotionName")) {
							if (!object.optString("promotionName")
									.equalsIgnoreCase("null")) {
								promotionName = object
										.optString("promotionName");
							}
						}
						if (object.has("productDetails")) {
							if (!object.optString("productDetails")
									.equalsIgnoreCase("null")) {
								productDetails = object
										.optString("productDetails");
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

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public double getPrice() {
		return price;
	}

	public double getPromotionPrice() {
		return promotionPrice;
	}

	public String getId() {
		return id;
	}

	public String getPromotionName() {
		return promotionName;
	}

	public String getProductDetails() {
		return productDetails;
	}

	public String getAttribute() {
		return attribute;
	}

	public String getNumber() {
		return number;
	}

	public String getProductName() {
		return productName;
	}

	public List<ModelProductImage> getImages() {
		return images;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "ModelSaleDetailTime [startTime=" + startTime + ", endTime="
				+ endTime + ", price=" + price + ", promotionPrice="
				+ promotionPrice + ", id=" + id + ", promotionName="
				+ promotionName + ", productDetails=" + productDetails
				+ ", attribute=" + attribute + ", number=" + number
				+ ", productName=" + productName + ", images=" + images + "]";
	}

}
