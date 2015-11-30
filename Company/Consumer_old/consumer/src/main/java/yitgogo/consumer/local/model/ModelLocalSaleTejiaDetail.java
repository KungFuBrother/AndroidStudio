package yitgogo.consumer.local.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 本地特价促销
 * 
 * @author Tiger
 * 
 * @json result={"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
 *       "totalCount":1,"dataMap":{"promotionImg":
 *       "http://imageprocess.yitos.net/images/public/20151014/55201444819353263.jpg"
 *       ,"attribute":"水电费收水电费","number":"YT52427416812","productId":12003,
 *       "salePromotionName":"水水水水","price":9.9999999E7,"productDetais":
 *       "水电费水电费水电费上对发斯蒂芬斯蒂芬斯蒂芬是的斯蒂芬斯蒂芬斯蒂芬是的"
 *       ,"numbers":9996,"productImg":[{"imgName":
 *       "http://imageprocess.yitos.net/images/public/20151012/32871444632765864.jpg"
 *       }],"spNo":"YT292879022253","salePrice":111.0,"spId":2,"productName":
 *       "浏览记录测试"},"object":null}]
 */
public class ModelLocalSaleTejiaDetail {

	String salePromotionName = "", productDetais = "", promotionImg = "",
			attribute = "", number = "", productName = "", productId = "",
			spNo = "", spId = "";
	int numbers = 0;
	double price = 0, salePrice = 0;
	List<ModelLocalServiceImage> images = new ArrayList<ModelLocalServiceImage>();
	JSONObject jsonObject = new JSONObject();

	public ModelLocalSaleTejiaDetail() {
	}

	public ModelLocalSaleTejiaDetail(JSONObject object) {
		if (object != null) {
			this.jsonObject = object;
			if (object.has("salePromotionName")) {
				if (!object.optString("salePromotionName").equalsIgnoreCase(
						"null")) {
					salePromotionName = object.optString("salePromotionName");
				}
			}
			if (object.has("productDetais")) {
				if (!object.optString("productDetais").equalsIgnoreCase("null")) {
					productDetais = object.optString("productDetais");
				}
			}
			if (object.has("promotionImg")) {
				if (!object.optString("promotionImg").equalsIgnoreCase("null")) {
					promotionImg = object.optString("promotionImg");
				}
			}
			if (object.has("attribute")) {
				if (!object.optString("attribute").equalsIgnoreCase("null")) {
					attribute = object.optString("attribute");
				}
			}
			if (object.has("number")) {
				if (!object.optString("number").equalsIgnoreCase("null")) {
					number = object.optString("number");
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
			if (object.has("numbers")) {
				if (!object.optString("numbers").equalsIgnoreCase("null")) {
					numbers = object.optInt("numbers");
				}
			}
			if (object.has("price")) {
				if (!object.optString("price").equalsIgnoreCase("null")) {
					price = object.optDouble("price");
				}
			}
			if (object.has("salePrice")) {
				if (!object.optString("salePrice").equalsIgnoreCase("null")) {
					salePrice = object.optDouble("salePrice");
				}
			}
			if (object.has("spNo")) {
				if (!object.optString("spNo").equalsIgnoreCase("null")) {
					spNo = object.optString("spNo");
				}
			}
			if (object.has("spId")) {
				if (!object.optString("spId").equalsIgnoreCase("null")) {
					spId = object.optString("spId");
				}
			}
			JSONArray imageArray = object.optJSONArray("productImg");
			if (imageArray != null) {
				for (int i = 0; i < imageArray.length(); i++) {
					images.add(new ModelLocalServiceImage(imageArray
							.optJSONObject(i)));
				}
			}
		}
	}

	public String getSalePromotionName() {
		return salePromotionName;
	}

	public String getProductDetais() {
		return productDetais;
	}

	public String getPromotionImg() {
		return promotionImg;
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

	public int getNumbers() {
		return numbers;
	}

	public double getPrice() {
		return price;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public List<ModelLocalServiceImage> getImages() {
		return images;
	}

	public String getProductId() {
		return productId;
	}

	public String getSpId() {
		return spId;
	}

	public String getSpNo() {
		return spNo;
	}
}
