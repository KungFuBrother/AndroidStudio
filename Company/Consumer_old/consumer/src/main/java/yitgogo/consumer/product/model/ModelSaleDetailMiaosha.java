package yitgogo.consumer.product.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 秒杀详情
 * 
 * @author Tiger
 * 
 * @json { "message": "ok", "state": "SUCCESS", "cacheKey": null, "dataList":
 *       [], "totalCount": 1, "dataMap": { "startTime": "", "seckillName":
 *       "发顺丰说", "id": 11562, "price": 4799, "memberNumber": 2, "seckillPrice":
 *       5, "seckillNUmber": 21, "productDetais": "", "productImg": [ { "id":
 *       323357, "imgName":
 *       "http://images.yitos.net/images/public/20150124/90521422081736620.png"
 *       }, { "id": 323359, "imgName":
 *       "http://images.yitos.net/images/public/20150124/37701422081736874.png"
 *       }, { "id": 323358, "imgName":
 *       "http://images.yitos.net/images/public/20150124/50981422081737108.png"
 *       } ], "attribute": "白色37码", "number": "YT34814659557", "productName":
 *       "奥克斯（AUX） KFR-51LW/BPSFD+3 正2匹 柜式冷暖新APF变频空调（纯铜管）" }, "object": null }
 */
public class ModelSaleDetailMiaosha {

	String type = "秒杀";
	long startTime = 0, memberNumber = 0, seckillNUmber;
	double price = -1, seckillPrice = -1;
	String id = "", seckillName = "", productDetais = "", attribute = "",
			number = "", productName = "";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");
	List<ModelProductImage> images = new ArrayList<ModelProductImage>();

	public ModelSaleDetailMiaosha() {
	}

	public ModelSaleDetailMiaosha(String result) throws JSONException {
		if (result.length() > 0) {
			JSONObject resultObject = new JSONObject(result);
			if (resultObject != null) {
				if (resultObject.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONObject object = resultObject.optJSONObject("dataMap");
					if (object != null) {
						if (object.has("startTime")) {
							if (!object.optString("startTime")
									.equalsIgnoreCase("null")) {
								try {
									startTime = simpleDateFormat.parse(
											object.optString("startTime"))
											.getTime();
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
						}
						if (object.has("memberNumber")) {
							if (!object.optString("memberNumber")
									.equalsIgnoreCase("null")) {
								memberNumber = object.optLong("memberNumber");
							}
						}
						if (object.has("seckillNUmber")) {
							if (!object.optString("seckillNUmber")
									.equalsIgnoreCase("null")) {
								seckillNUmber = object.optLong("seckillNUmber");
							}
						}
						if (object.has("price")) {
							if (!object.optString("price").equalsIgnoreCase(
									"null")) {
								price = object.optDouble("price");
							}
						}
						if (object.has("seckillPrice")) {
							if (!object.optString("seckillPrice")
									.equalsIgnoreCase("null")) {
								seckillPrice = object.optDouble("seckillPrice");
							}
						}
						if (object.has("id")) {
							if (!object.optString("id")
									.equalsIgnoreCase("null")) {
								id = object.optString("id");
							}
						}
						if (object.has("seckillName")) {
							if (!object.optString("seckillName")
									.equalsIgnoreCase("null")) {
								seckillName = object.optString("seckillName");
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

	public long getStartTime() {
		return startTime;
	}

	public long getMemberNumber() {
		return memberNumber;
	}

	public long getSeckillNUmber() {
		return seckillNUmber;
	}

	public double getPrice() {
		return price;
	}

	public double getSeckillPrice() {
		return seckillPrice;
	}

	public String getId() {
		return id;
	}

	public String getSeckillName() {
		return seckillName;
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

	public List<ModelProductImage> getImages() {
		return images;
	}

	@Override
	public String toString() {
		return "ModelSaleDetailMiaosha [type=" + type + ", startTime="
				+ startTime + ", memberNumber=" + memberNumber
				+ ", seckillNUmber=" + seckillNUmber + ", price=" + price
				+ ", seckillPrice=" + seckillPrice + ", id=" + id
				+ ", seckillName=" + seckillName + ", productDetais="
				+ productDetais + ", attribute=" + attribute + ", number="
				+ number + ", productName=" + productName + ", images="
				+ images + "]";
	}

}
