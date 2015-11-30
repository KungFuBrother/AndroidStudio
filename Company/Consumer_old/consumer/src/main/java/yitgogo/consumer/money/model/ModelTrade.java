package yitgogo.consumer.money.model;

import org.json.JSONObject;

/**
 * 银行类型
 * 
 * @author Tiger
 * 
 * @Json {"account":"HY048566511863","amount":116.00,"amountFlow":true,
 *       "datatime":"2015-08-19 21:25:16","description":"参与活动中奖","id":
 *       "15081921250003" ,"realname":"赵晋","sourceid":"YT1519063489"}
 */
public class ModelTrade {

	String account = "", datatime = "", description = "", id = "",
			realname = "", sourceid = "";
	double amount = 0;
	boolean amountFlow = false;

	public ModelTrade() {
	}

	public ModelTrade(JSONObject object) {
		if (object != null) {
			if (object.has("account")) {
				if (!object.optString("account").equalsIgnoreCase("null")) {
					account = object.optString("account");
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
			if (object.has("realname")) {
				if (!object.optString("realname").equalsIgnoreCase("null")) {
					realname = object.optString("realname");
				}
			}
			if (object.has("sourceid")) {
				if (!object.optString("sourceid").equalsIgnoreCase("null")) {
					sourceid = object.optString("sourceid");
				}
			}
			if (object.has("amount")) {
				if (!object.optString("amount").equalsIgnoreCase("null")) {
					amount = object.optDouble("amount");
				}
			}
			amountFlow = object.optBoolean("amountFlow");
		}
	}

	public String getAccount() {
		return account;
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

	public String getRealname() {
		return realname;
	}

	public String getSourceid() {
		return sourceid;
	}

	public double getAmount() {
		return amount;
	}

	public boolean isAmountFlow() {
		return amountFlow;
	}

}
