package yitgogo.consumer.money.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json {"amount":1.00,"area":"四川-成都","datatime": "2015-08-18
 *       22:42:37" ,"description
 *       ":"取点钱","id":1,"memberNo" :"13032889558","orderno
 *       " :"TX15081822420001","
 *       payaccount":"15081711040001" ,"realname" :"雷小武","
 *       state":"处理中","userbank":"中国邮政储蓄","userbankid" :"6210986731007566422" }
 */
public class ModelTakeOutHistory {

	double amount = 0;
	String area = "", datatime = "", description = "", id = "", memberNo = "",
			orderno = "", payaccount = "", realname = "", state = "",
			userbank = "", userbankid = "";

	public ModelTakeOutHistory() {
	}

	public ModelTakeOutHistory(JSONObject object) {
		if (object != null) {
			if (object.has("area")) {
				if (!object.optString("area").equalsIgnoreCase("null")) {
					area = object.optString("area");
				}
			}
			if (object.has("datatime")) {
				if (!object.optString("datatime").equalsIgnoreCase("null")) {
					datatime = object.optString("datatime");
				}
			}
			if (object.has("description")) {
				if (!object.optString("description").equalsIgnoreCase("null")) {
					description = object.optString("description");
				}
			}
			if (object.has("id")) {
				if (!object.optString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("memberNo")) {
				if (!object.optString("memberNo").equalsIgnoreCase("null")) {
					memberNo = object.optString("memberNo");
				}
			}
			if (object.has("orderno")) {
				if (!object.optString("orderno").equalsIgnoreCase("null")) {
					orderno = object.optString("orderno");
				}
			}
			if (object.has("payaccount")) {
				if (!object.optString("payaccount").equalsIgnoreCase("null")) {
					payaccount = object.optString("payaccount");
				}
			}
			if (object.has("realname")) {
				if (!object.optString("realname").equalsIgnoreCase("null")) {
					realname = object.optString("realname");
				}
			}
			if (object.has("state")) {
				if (!object.optString("state").equalsIgnoreCase("null")) {
					state = object.optString("state");
				}
			}
			if (object.has("userbank")) {
				if (!object.optString("userbank").equalsIgnoreCase("null")) {
					userbank = object.optString("userbank");
				}
			}
			if (object.has("userbankid")) {
				if (!object.optString("userbankid").equalsIgnoreCase("null")) {
					userbankid = object.optString("userbankid");
				}
			}
			if (object.has("amount")) {
				if (!object.optString("amount").equalsIgnoreCase("null")) {
					amount = object.optDouble("amount");
				}
			}
		}
	}

	public double getAmount() {
		return amount;
	}

	public String getArea() {
		return area;
	}

	public String getDatatime() {
		return datatime;
	}

	public String getDescription() {
		return description;
	}

	public String getId() {
		return id;
	}

	public String getMemberNo() {
		return memberNo;
	}

	public String getOrderno() {
		return orderno;
	}

	public String getPayaccount() {
		return payaccount;
	}

	public String getRealname() {
		return realname;
	}

	public String getState() {
		return state;
	}

	public String getUserbank() {
		return userbank;
	}

	public String getUserbankid() {
		return userbankid;
	}

}
