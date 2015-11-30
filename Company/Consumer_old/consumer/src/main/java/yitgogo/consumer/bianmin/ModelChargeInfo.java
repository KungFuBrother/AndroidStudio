package yitgogo.consumer.bianmin;

import org.json.JSONObject;

/**
 * 充值信息
 * 
 * @author Tiger
 * 
 * @Json {"cardid":"170227","cardname" :"全国电信话费100元快充（活动通道）"
 *       ,"inprice":null,"sellprice":"100.00","area":"四川南充电信"}
 */
public class ModelChargeInfo {
	String cardid = "", cardname = "", area = "";
	double sellprice = -1;

	public ModelChargeInfo(JSONObject object) {
		if (object != null) {
			if (object.has("cardid")) {
				if (!object.optString("cardid").equalsIgnoreCase("null")) {
					cardid = object.optString("cardid");
				}
			}
			if (object.has("cardname")) {
				if (!object.optString("cardname").equalsIgnoreCase("null")) {
					cardname = object.optString("cardname");
				}
			}
			if (object.has("area")) {
				if (!object.optString("area").equalsIgnoreCase("null")) {
					area = object.optString("area");
				}
			}
			if (object.has("sellprice")) {
				if (!object.optString("sellprice").equalsIgnoreCase("null")) {
					sellprice = object.optDouble("sellprice");
				}
			}
		}

	}

	public ModelChargeInfo() {
	}

	public String getArea() {
		return area;
	}

	public String getCardid() {
		return cardid;
	}

	public String getCardname() {
		return cardname;
	}

	public double getSellprice() {
		return sellprice;
	}
}
