package yitgogo.consumer.user.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json { "id": 1, "bonusAmount": 500, "recordTime": "2015-06-22 17:22:41",
 *       "details": "扶老奶奶过马路奖励", "memberAccount": "18602887932", "storeId":
 *       1072, "bonusType": "收入" }
 */
public class ModelScoreDetail {

	String id = "", recordTime = "", details = "", memberAccount = "",
			storeId = "", bonusType = "";
	long bonusAmount = 0;

	public ModelScoreDetail() {
	}

	public ModelScoreDetail(JSONObject object) throws JSONException {

		if (object != null) {
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("recordTime")) {
				if (!object.getString("recordTime").equalsIgnoreCase("null")) {
					recordTime = object.optString("recordTime");
				}
			}
			if (object.has("details")) {
				if (!object.getString("details").equalsIgnoreCase("null")) {
					details = object.optString("details");
				}
			}
			if (object.has("memberAccount")) {
				if (!object.getString("memberAccount").equalsIgnoreCase("null")) {
					memberAccount = object.optString("memberAccount");
				}
			}
			if (object.has("storeId")) {
				if (!object.getString("storeId").equalsIgnoreCase("null")) {
					storeId = object.optString("storeId");
				}
			}
			if (object.has("bonusType")) {
				if (!object.getString("bonusType").equalsIgnoreCase("null")) {
					bonusType = object.optString("bonusType");
				}
			}
			if (object.has("bonusAmount")) {
				if (!object.getString("bonusAmount").equalsIgnoreCase("null")) {
					bonusAmount = object.optLong("bonusAmount");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getRecordTime() {
		return recordTime;
	}

	public String getDetails() {
		return details;
	}

	public String getMemberAccount() {
		return memberAccount;
	}

	public String getStoreId() {
		return storeId;
	}

	public String getBonusType() {
		return bonusType;
	}

	public long getBonusAmount() {
		return bonusAmount;
	}

	@Override
	public String toString() {
		return "ModelScoreDetail [id=" + id + ", recordTime=" + recordTime
				+ ", details=" + details + ", memberAccount=" + memberAccount
				+ ", storeId=" + storeId + ", bonusType=" + bonusType
				+ ", bonusAmount=" + bonusAmount + "]";
	}

}
