package yitgogo.consumer.local.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelMemberList {
	String id="",totalBonus="",memberAccount="";

	public ModelMemberList(JSONObject object) throws JSONException {
		if(object!=null){
			if(object.has("id")){
				if(!object.getString("id").equalsIgnoreCase("null")){
					id=object.optString("id");
				}
			}
			if(object.has("totalBonus")){
				if(!object.getString("totalBonus").equalsIgnoreCase("null")){
					totalBonus=object.optString("totalBonus");
				}
			}
			if(object.has("memberAccount")){
				if(!object.getString("memberAccount").equalsIgnoreCase("null")){
					memberAccount=object.optString("memberAccount");
				}
			}
		}

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTotalBonus() {
		return totalBonus;
	}

	public void setTotalBonus(String totalBonus) {
		this.totalBonus = totalBonus;
	}

	public String getMemberAccount() {
		return memberAccount;
	}

	public void setMemberAccount(String memberAccount) {
		this.memberAccount = memberAccount;
	}
	
}
