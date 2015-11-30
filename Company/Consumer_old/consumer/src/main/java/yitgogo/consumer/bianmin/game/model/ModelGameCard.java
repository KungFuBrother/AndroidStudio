package yitgogo.consumer.bianmin.game.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json { "cardid": "229509", "cardname": "七雄争霸100元宝充值", "pervalue": "10",
 *       "inprice": "9.64", "innum": "30", "amounts": [ "1", "2", "3", "4", "5",
 *       "6", "7", "8", "9", "10" ], "sellprice": "9.89" }
 */
public class ModelGameCard {

	String cardid = "", cardname = "", pervalue = "";
	double inprice = -1, sellprice = -1;
	int innum = -1;
	List<Integer> amounts = new ArrayList<Integer>();

	public ModelGameCard() {
	}

	public ModelGameCard(JSONObject object) {
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
			if (object.has("pervalue")) {
				if (!object.optString("pervalue").equalsIgnoreCase("null")) {
					pervalue = object.optString("pervalue");
				}
			}
			if (object.has("inprice")) {
				if (!object.optString("inprice").equalsIgnoreCase("null")) {
					inprice = object.optDouble("inprice");
				}
			}
			if (object.has("sellprice")) {
				if (!object.optString("sellprice").equalsIgnoreCase("null")) {
					sellprice = object.optDouble("sellprice");
				}
			}
			if (object.has("innum")) {
				if (!object.optString("innum").equalsIgnoreCase("null")) {
					innum = object.optInt("innum");
				}
			}
			JSONArray array = object.optJSONArray("amounts");
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					int amount = array.optInt(i);
					if (amount > 0) {
						amounts.add(amount);
					}
				}
			}
		}
	}

	public String getCardid() {
		return cardid;
	}

	public String getCardname() {
		return cardname;
	}

	public String getPervalue() {
		return pervalue;
	}

	public double getInprice() {
		return inprice;
	}

	public double getSellprice() {
		return sellprice;
	}

	public int getInnum() {
		return innum;
	}

	public List<Integer> getAmounts() {
		return amounts;
	}

}
