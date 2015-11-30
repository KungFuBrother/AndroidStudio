package yitgogo.consumer.home.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Description 秒杀商品类
 * 
 * @JsonObject { "seckillTime": 1439654400000, "seckillName": "测试", "shuxName":
 *             null, "seckillNumber": 20, "productName":
 *             "保时捷 cayenne 2011款 越野车", "produtId": 244, "seckillImg":
 *             "http://images.yitos.net/images/public/20150815/32681439638236168.jpg"
 *             }
 */
public class ModelSaleMiaosha {

	String seckillName = "", shuxName = "", productName = "", produtId = "",
			seckillImg = "";
	long seckillTime = 0, seckillNumber = 0;
	JSONObject jsonObject;

	public ModelSaleMiaosha(JSONObject object) {
		if (object != null) {
			this.jsonObject = object;
			if (object.has("seckillName")) {
				if (!object.optString("seckillName").equalsIgnoreCase("null")) {
					seckillName = object.optString("seckillName");
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
		}
	}

	public ModelSaleMiaosha() {
	}

	public String getSeckillName() {
		return seckillName;
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

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public String toString() {
		return "ModelSaleMiaosha [seckillName=" + seckillName + ", shuxName="
				+ shuxName + ", productName=" + productName + ", produtId="
				+ produtId + ", seckillImg=" + seckillImg + ", seckillTime="
				+ seckillTime + ", seckillNumber=" + seckillNumber
				+ ", jsonObject=" + jsonObject + "]";
	}

}
