package yitgogo.consumer.user.model;

import org.json.JSONObject;

/**
 * @author Tiger
 * @Json {"id" :null, "totalBonus" :0,"memberAccount":"15882972602"}
 */
public class ModelRecommend {

	String id = "", totalBonus = "", memberAccount = "";

	public ModelRecommend(JSONObject object) {
		if (object != null) {
			if (object.has("id")) {
				if (!object.optString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("totalBonus")) {
				if (!object.optString("totalBonus").equalsIgnoreCase("null")) {
					totalBonus = object.optString("totalBonus");
				}
			}
			if (object.has("memberAccount")) {
				if (!object.optString("memberAccount").equalsIgnoreCase("null")) {
					memberAccount = object.optString("memberAccount");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getTotalBonus() {
		return totalBonus;
	}

	public String getMemberAccount() {
		return memberAccount;
	}

}
