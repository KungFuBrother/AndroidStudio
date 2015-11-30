package yitgogo.consumer.order.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json { "message": "快递公司网络异常，请稍后查询.", "person": "锦冈机械", "personPhone":
 *       "15023704575", "deliveryTime": "2015-09-11 17:50:13", "state": "ERROR",
 *       "companyName": "圆通速递", "wayBill": "wwwwqqq" }
 */
public class ModelWuliu {

	String message = "", person = "", personPhone = "", deliveryTime = "",
			companyName = "", wayBill = "";
	boolean suceess = false;
	ModelWuliuObject wuliuObject;

	public ModelWuliu(JSONObject object) throws JSONException {
		if (object != null) {
			message = object.optString("message");
			person = object.optString("person");
			personPhone = object.optString("personPhone");
			deliveryTime = object.optString("deliveryTime");
			companyName = object.optString("companyName");
			wayBill = object.optString("wayBill");
			if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
				suceess = true;
				wuliuObject = new ModelWuliuObject(new JSONObject(
						object.optString("message", "{}")));
			}
		}
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getMessage() {
		return message;
	}

	public String getWayBill() {
		return wayBill;
	}

	public String getPerson() {
		return person;
	}

	public String getPersonPhone() {
		return personPhone;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	public boolean isSuceess() {
		return suceess;
	}

	public ModelWuliuObject getWuliuObject() {
		return wuliuObject;
	}

	@Override
	public String toString() {
		return "ModelWuliu [companyName=" + companyName + ", message="
				+ message + ", wayBill=" + wayBill + ", deliveryTime="
				+ deliveryTime + ", suceess=" + suceess + "]";
	}

}
