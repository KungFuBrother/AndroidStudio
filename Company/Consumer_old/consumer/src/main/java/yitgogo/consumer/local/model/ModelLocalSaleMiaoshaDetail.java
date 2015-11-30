package yitgogo.consumer.local.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 本地秒杀促销
 * 
 * @author Tiger
 * 
 * @json result={"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
 *       "totalCount"
 *       :1,"dataMap":{"seckillName":"啥地方啥地方刚","memberNumber":1,"seckillNUmber"
 *       :1,"attribute":"32fsdfsadfasd22","number":"YT91351520118","id":11998,
 *       "startTime"
 *       :"2015-10-15 08:30:00","salePromotionName":"啥地方啥地方刚","price":
 *       31.0,"seckillPrice":1.0,"numbers":3123,"productDetais":
 *       "33333333333333333333333333333333333333"
 *       ,"spNo":"YT292879022253","productImg":[{"imgName":
 *       "http://images.yitos.net/images/public/20150915/70151442315433535.jpg"
 *       }],"spId":2,"salePrice":1.0,"productName":"123"},"object":null}
 */
public class ModelLocalSaleMiaoshaDetail {
	String startTime = "", seckillName = "", id = "", productDetais = "",
			attribute = "", number = "", productName = "", spNo = "",
			spId = "";
	int memberNumber = 0, numbers = 0;
	double price = 0, seckillPrice = 0;
	JSONObject jsonObject = new JSONObject();
	List<ModelLocalServiceImage> images = new ArrayList<ModelLocalServiceImage>();

	public ModelLocalSaleMiaoshaDetail(JSONObject object) {
		if (object != null) {
			jsonObject = object;
			if (object.has("startTime")) {
				if (!object.optString("startTime").equalsIgnoreCase("null")) {
					startTime = object.optString("startTime");
				}
			}
			if (object.has("seckillName")) {
				if (!object.optString("seckillName").equalsIgnoreCase("null")) {
					seckillName = object.optString("seckillName");
				}
			}
			if (object.has("id")) {
				if (!object.optString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("productDetais")) {
				if (!object.optString("productDetais").equalsIgnoreCase("null")) {
					productDetais = object.optString("productDetais");
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
			if (object.has("memberNumber")) {
				if (!object.optString("memberNumber").equalsIgnoreCase("null")) {
					memberNumber = object.optInt("memberNumber");
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
			if (object.has("seckillPrice")) {
				if (!object.optString("seckillPrice").equalsIgnoreCase("null")) {
					seckillPrice = object.optDouble("seckillPrice");
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

	public ModelLocalSaleMiaoshaDetail() {
	}

	public String getStartTime() {
		return startTime;
	}

	public String getSeckillName() {
		return seckillName;
	}

	public String getId() {
		return id;
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

	public String getProductName() {
		return productName;
	}

	public int getMemberNumber() {
		return memberNumber;
	}

	public int getNumbers() {
		return numbers;
	}

	public double getPrice() {
		return price;
	}

	public double getSeckillPrice() {
		return seckillPrice;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public List<ModelLocalServiceImage> getImages() {
		return images;
	}

	public String getSpId() {
		return spId;
	}

	public String getSpNo() {
		return spNo;
	}
}
