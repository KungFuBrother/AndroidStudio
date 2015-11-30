package yitgogo.consumer.money.model;

import org.json.JSONObject;

/**
 * 银行类型
 * 
 * @author Tiger
 * 
 * @Json {"bandnameadds":"南充支行","bank" :{"code":"PSBC","icon":"17","id"
 *       :17,"name":"中国邮政储蓄"},"banknumber":"6210986731007566422"
 *       ,"cardType":"储蓄卡"
 *       ,"cradname":"雷小武","id":5,"idCard":"513030199311056012",
 *       "mobile":"13032889558","org":"13032889558","validation":false}
 */
public class ModelBankCard {

	String bandnameadds = "", banknumber = "", cardType = "", cradname = "",
			id = "", idCard = "", org = "", mobile = "";

	String expiredDate = "", cvv2 = "";

	ModelBank bank = new ModelBank();
	boolean validation = false;
	JSONObject jsonObject = new JSONObject();

	public ModelBankCard() {
	}

	public ModelBankCard(String moneyAccount) {
		this.cardType = "钱袋子余额";
		this.id = "moneyAccount";
	}

	public ModelBankCard(JSONObject object) {
		if (object != null) {
			jsonObject = object;
			if (object.has("bandnameadds")) {
				if (!object.optString("bandnameadds").equalsIgnoreCase("null")) {
					bandnameadds = object.optString("bandnameadds");
				}
			}
			if (object.has("banknumber")) {
				if (!object.optString("banknumber").equalsIgnoreCase("null")) {
					banknumber = object.optString("banknumber");
				}
			}
			if (object.has("cardType")) {
				if (!object.optString("cardType").equalsIgnoreCase("null")) {
					cardType = object.optString("cardType");
				}
			}
			if (object.has("cradname")) {
				if (!object.optString("cradname").equalsIgnoreCase("null")) {
					cradname = object.optString("cradname");
				}
			}
			if (object.has("id")) {
				if (!object.optString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("idCard")) {
				if (!object.optString("idCard").equalsIgnoreCase("null")) {
					idCard = object.optString("idCard");
				}
			}
			if (object.has("org")) {
				if (!object.optString("org").equalsIgnoreCase("null")) {
					org = object.optString("org");
				}
			}
			if (object.has("mobile")) {
				if (!object.optString("mobile").equalsIgnoreCase("null")) {
					mobile = object.optString("mobile");
				}
			}
			JSONObject bankObject = object.optJSONObject("bank");
			bank = new ModelBank(bankObject);
			validation = object.optBoolean("validation");
		}
	}

	public String getBandnameadds() {
		return bandnameadds;
	}

	public String getBanknumber() {
		return banknumber;
	}

	public String getCardType() {
		return cardType;
	}

	public String getCradname() {
		return cradname;
	}

	public String getId() {
		return id;
	}

	public String getIdCard() {
		return idCard;
	}

	public String getOrg() {
		return org;
	}

	public String getMobile() {
		return mobile;
	}

	public ModelBank getBank() {
		return bank;
	}

	public boolean isValidation() {
		return validation;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public String getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getCvv2() {
		return cvv2;
	}

	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}

}
