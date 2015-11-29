package yitgogo.smart.home.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Description 秒杀商品类
 * 
 * @JsonObject 
 *             {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[{"seckillTime"
 *             :1437116427000,"seckillName":"测试","price":67.0,"originalPrice":
 *             1890.0
 *             ,"shuxId":"474","shuxName":null,"seckillNumber":12,"productName"
 *             :"Haier/海尔 BC/BD-272SE卧式商用冷柜"
 *             ,"produtId":3,"seckillImg":""},{"seckillTime"
 *             :1437116445000,"seckillName"
 *             :"测试","price":7.0,"originalPrice":4258.0
 *             ,"shuxId":"475","shuxName":null,"seckillNumber":12,"productName":
 *             "Haier/海尔 BC/BD-719H 719升 卧式冷柜","produtId":4,"seckillImg":
 *             "http://images.yitos.net/images/public/20150717/85731437114259952.jpg"
 *             }],"totalCount":1,"dataMap":{},"object":null}
 */
public class ModelSaleMiaosha {

	String seckillName = "", shuxId = "", shuxName = "", productName = "",
			produtId = "", seckillImg = "";
	long seckillTime = 0, seckillNumber = 0;
	double price = -1, originalPrice = -1;
	JSONObject jsonObject;

	public ModelSaleMiaosha(JSONObject object) {
		if (object != null) {
			this.jsonObject = object;
			if (object.has("seckillName")) {
				if (!object.optString("seckillName").equalsIgnoreCase("null")) {
					seckillName = object.optString("seckillName");
				}
			}
			if (object.has("shuxId")) {
				if (!object.optString("shuxId").equalsIgnoreCase("null")) {
					shuxId = object.optString("shuxId");
				}
			}
			if (object.has("shuxName")) {
				if (!object.optString("shuxName").equalsIgnoreCase("null")) {
					shuxName = object.optString("shuxName");
				}
			}
			if (object.has("productName")) {
				if (!object.optString("productName").equalsIgnoreCase("null")) {
					productName = object.optString("productName");
				}
			}
			if (object.has("produtId")) {
				if (!object.optString("produtId").equalsIgnoreCase("null")) {
					produtId = object.optString("produtId");
				}
			}
			if (object.has("seckillImg")) {
				if (!object.optString("seckillImg").equalsIgnoreCase("null")) {
					seckillImg = object.optString("seckillImg");
				}
			}
			if (object.has("seckillTime")) {
				if (!object.optString("seckillTime").equalsIgnoreCase("null")) {
					seckillTime = object.optLong("seckillTime");
				}
			}
			if (object.has("seckillNumber")) {
				if (!object.optString("seckillNumber").equalsIgnoreCase("null")) {
					seckillNumber = object.optLong("seckillNumber");
				}
			}
			if (object.has("price")) {
				if (!object.optString("price").equalsIgnoreCase("null")) {
					price = object.optDouble("price");
				}
			}
			if (object.has("originalPrice")) {
				if (!object.optString("originalPrice").equalsIgnoreCase("null")) {
					originalPrice = object.optDouble("originalPrice");
				}
			}
		}
	}

	public ModelSaleMiaosha() {
	}

	public String getSeckillName() {
		return seckillName;
	}

	public String getShuxId() {
		return shuxId;
	}

	public String getShuxName() {
		return shuxName;
	}

	public String getProductName() {
		return productName;
	}

	public String getProdutId() {
		return produtId;
	}

	public String getSeckillImg() {
		return seckillImg;
	}

	public long getSeckillTime() {
		return seckillTime;
	}

	public long getSeckillNumber() {
		return seckillNumber;
	}

	public double getPrice() {
		return price;
	}

	public double getOriginalPrice() {
		return originalPrice;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public String toString() {
		return "ModelSaleMiaosha [seckillName=" + seckillName + ", shuxId="
				+ shuxId + ", shuxName=" + shuxName + ", productName="
				+ productName + ", produtId=" + produtId + ", seckillImg="
				+ seckillImg + ", seckillTime=" + seckillTime
				+ ", seckillNumber=" + seckillNumber + ", price=" + price
				+ ", originalPrice=" + originalPrice + ", jsonObject="
				+ jsonObject + "]";
	}

}
