package yitgogo.consumer.money.model;

import org.json.JSONObject;

import com.smartown.yitian.gogo.R;

/**
 * 银行类型
 * 
 * @author Tiger
 * 
 * @Json {"code":"ICBC" , "icon":"1","id" :1,"name":"中国工商银行"}
 */
public class ModelBank {

	String code = "", icon = "drawable://" + R.drawable.ic_money_pag, id = "",
			name = "";

	public ModelBank() {
	}

	public ModelBank(JSONObject object) {
		if (object != null) {
			if (object.has("code")) {
				if (!object.optString("code").equalsIgnoreCase("null")) {
					code = object.optString("code");
				}
			}
			if (object.has("icon")) {
				if (!object.optString("icon").equalsIgnoreCase("null")) {
					icon = object.optString("icon");
				}
			}
			if (object.has("id")) {
				if (!object.optString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("name")) {
				if (!object.optString("name").equalsIgnoreCase("null")) {
					name = object.optString("name");
				}
			}
		}
	}

	public String getCode() {
		return code;
	}

	public String getIcon() {
		return icon;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
