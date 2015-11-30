package yitgogo.consumer.local.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 收货地址
 * 
 * @author Tiger
 * 
 * @Json { "id": 35, "personName": "雷霆战机", "areaId": 2421, "areaAddress":
 *       "四川省成都市金牛区", "detailedAddress": "开心闯江湖儿女", "phone": "13032889558",
 *       "fixPhone": null, "postcode": null, "email": null, "isDefault": 1,
 *       "memberAccount": "13032889558", "millis": 1437630250010 }
 */
public class ModelAddress {

	String id = "", personName = "", areaId = "", areaAddress = "",
			detailedAddress = "", phone = "", fixPhone = "", postcode = "",
			email = "", memberAccount = "";
	boolean isDefault = false;

	public ModelAddress() {
	}

	public ModelAddress(JSONObject object) throws JSONException {
		if (object != null) {
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("personName")) {
				if (!object.getString("personName").equalsIgnoreCase("null")) {
					personName = object.optString("personName");
				}
			}
			if (object.has("areaId")) {
				if (!object.getString("areaId").equalsIgnoreCase("null")) {
					areaId = object.optString("areaId");
				}
			}
			if (object.has("areaAddress")) {
				if (!object.getString("areaAddress").equalsIgnoreCase("null")) {
					areaAddress = object.optString("areaAddress");
				}
			}
			if (object.has("detailedAddress")) {
				if (!object.getString("detailedAddress").equalsIgnoreCase(
						"null")) {
					detailedAddress = object.optString("detailedAddress");
				}
			}
			if (object.has("phone")) {
				if (!object.getString("phone").equalsIgnoreCase("null")) {
					phone = object.optString("phone");
				}
			}
			if (object.has("fixPhone")) {
				if (!object.getString("fixPhone").equalsIgnoreCase("null")) {
					fixPhone = object.optString("fixPhone");
				}
			}
			if (object.has("postcode")) {
				if (!object.getString("postcode").equalsIgnoreCase("null")) {
					postcode = object.optString("postcode");
				}
			}
			if (object.has("email")) {
				if (!object.getString("email").equalsIgnoreCase("null")) {
					email = object.optString("email");
				}
			}
			if (object.has("memberAccount")) {
				if (!object.getString("memberAccount").equalsIgnoreCase("null")) {
					memberAccount = object.optString("memberAccount");
				}
			}
			switch (object.optInt("isDefault")) {
			case 0:
				isDefault = false;
				break;

			case 1:
				isDefault = true;
				break;
			default:
				break;
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getPersonName() {
		return personName;
	}

	public String getAreaId() {
		return areaId;
	}

	public String getAreaAddress() {
		return areaAddress;
	}

	public String getDetailedAddress() {
		return detailedAddress;
	}

	public String getPhone() {
		return phone;
	}

	public String getFixPhone() {
		return fixPhone;
	}

	public String getPostcode() {
		return postcode;
	}

	public String getEmail() {
		return email;
	}

	public String getMemberAccount() {
		return memberAccount;
	}

	public boolean isDefault() {
		return isDefault;
	}

	@Override
	public String toString() {
		return "ModelAddress [id=" + id + ", personName=" + personName
				+ ", areaId=" + areaId + ", areaAddress=" + areaAddress
				+ ", detailedAddress=" + detailedAddress + ", phone=" + phone
				+ ", fixPhone=" + fixPhone + ", postcode=" + postcode
				+ ", email=" + email + ", memberAccount=" + memberAccount
				+ ", isDefault=" + isDefault + "]";
	}

}
