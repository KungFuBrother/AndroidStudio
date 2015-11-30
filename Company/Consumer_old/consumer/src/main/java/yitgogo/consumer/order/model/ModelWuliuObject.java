package yitgogo.consumer.order.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 快递100返回的物流对象
 * 
 * @see http://www.kuaidi100.com/openapi/api_post.shtml
 * 
 * @author Tiger
 * 
 */
public class ModelWuliuObject {

	String wuliuStates[] = { "在途", "揽件", "疑难", "签收", "退签", "派件", "退回" };
	String nu = "", message = "", comcontact = "", ischeck = "", com = "",
			condition = "", status = "", comurl = "";
	List<ModelWuliuDetail> wuliuDetails = new ArrayList<ModelWuliuDetail>();

	public ModelWuliuObject(JSONObject object) throws JSONException {
		if (object != null) {
			nu = object.optString("nu");
			message = object.optString("message");
			comcontact = object.optString("comcontact");
			ischeck = object.optString("ischeck");
			com = object.optString("com");
			condition = object.optString("condition");
			comurl = object.optString("comurl");
			switch (object.optInt("status")) {
			case 0:
				status = "暂无物流信息";
				break;
			case 1:
				status = wuliuStates[object.optInt("state")];
				JSONArray array = object.optJSONArray("data");
				if (array != null) {
					if (array.length() > 0) {
						for (int i = 0; i < array.length(); i++) {
							wuliuDetails.add(new ModelWuliuDetail(array
									.getJSONObject(i)));
						}
					}
				}
				break;
			case 2:
				status = "暂无物流信息";
				break;
			default:
				break;
			}
		}
	}

	public String getNu() {
		return nu;
	}

	public String getMessage() {
		return message;
	}

	public String getComcontact() {
		return comcontact;
	}

	public String getIscheck() {
		return ischeck;
	}

	public String getCom() {
		return com;
	}

	public String getCondition() {
		return condition;
	}

	public String getStatus() {
		return status;
	}

	public String getComurl() {
		return comurl;
	}

	public String[] getWuliuStates() {
		return wuliuStates;
	}

	public List<ModelWuliuDetail> getWuliuDetails() {
		return wuliuDetails;
	}

	@Override
	public String toString() {
		return "ModelWuliuObject [wuliuStates=" + Arrays.toString(wuliuStates)
				+ ", nu=" + nu + ", message=" + message + ", comcontact="
				+ comcontact + ", ischeck=" + ischeck + ", com=" + com
				+ ", condition=" + condition + ", status=" + status
				+ ", comurl=" + comurl + ", wuliuDetails=" + wuliuDetails + "]";
	}

}