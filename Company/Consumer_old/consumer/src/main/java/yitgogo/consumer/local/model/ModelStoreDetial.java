package yitgogo.consumer.local.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelStoreDetial {
	String id="";
	String address="";
	String img="";
	String number="";
	String shopname="";
	
	public ModelStoreDetial(JSONObject jsonObject) throws JSONException {
		if(jsonObject!=null){
			if(jsonObject.has("id")){
				if(jsonObject.getString("id").equalsIgnoreCase("null")){
					id=jsonObject.getString("id");
				}	
			}
			if(jsonObject.has("address")){
				if(jsonObject.getString("address").equalsIgnoreCase("null")){
					address=jsonObject.getString("address");
				}	
			}
			if(jsonObject.has("img")){
				if(jsonObject.getString("img").equalsIgnoreCase("null")){
					img=jsonObject.getString("img");
				}	
			}
			if(jsonObject.has("number")){
				if(jsonObject.getString("number").equalsIgnoreCase("null")){
					number=jsonObject.getString("number");
				}	
			}
			if(jsonObject.has("shopname")){
				if(jsonObject.getString("shopname").equalsIgnoreCase("null")){
					shopname=jsonObject.getString("shopname");
				}	
			}
		}
		
	}
	public String getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}

	public String getImg() {
		return img;
	}

	public String getNumber() {
		return number;
	}

	public String getShopname() {
		return shopname;
	}

	
}
