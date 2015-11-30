package yitgogo.consumer.store.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 定位加盟店数据类
 * 
 * @author Tiger
 * 
 */
public class ModelStoreLocated {
	/**
	 * { "status": 0, "total": 1, "size": 1, "contents": [ { "title":
	 * "重庆梁平县杨章富加盟商", "location": [ 107.6893239, 30.66286445 ], "city": "重庆市",
	 * "create_time": 1427430742, "geotable_id": 96314, "address": "重庆市梁平县仁贤镇",
	 * "tags": "", "province": "重庆市", "district": "梁平县", "icon_style_id":
	 * "sid1", "jmdNo": "YT134280925480", "bossName": "杨章富", "phone":
	 * "13594748768", "jmdId": "777", "uid": 723033154, "coord_type": 3, "type":
	 * 0, "distance": 0, "weight": 0 } ] }
	 */

	private String title = "", city = "", address = "", province = "",
			district = "", jmdNo = "", bossName = "", phone = "", jmdId = "";
	private long distance = 0;
	private ModelLocation location = new ModelLocation();
	private JSONObject jsonObject;

	public ModelStoreLocated(JSONObject object) throws JSONException {
		if (object != null) {
			jsonObject = object;
			if (object.has("title")) {
				if (!object.getString("title").equalsIgnoreCase("null")) {
					title = object.optString("title");
				}
			}
			if (object.has("city")) {
				if (!object.getString("city").equalsIgnoreCase("null")) {
					city = object.optString("city");
				}
			}
			if (object.has("address")) {
				if (!object.getString("address").equalsIgnoreCase("null")) {
					address = object.optString("address");
				}
			}
			if (object.has("province")) {
				if (!object.getString("province").equalsIgnoreCase("null")) {
					province = object.optString("province");
				}
			}
			if (object.has("district")) {
				if (!object.getString("district").equalsIgnoreCase("null")) {
					district = object.optString("district");
				}
			}
			if (object.has("jmdNo")) {
				if (!object.getString("jmdNo").equalsIgnoreCase("null")) {
					jmdNo = object.optString("jmdNo");
				}
			}
			if (object.has("bossName")) {
				if (!object.getString("bossName").equalsIgnoreCase("null")) {
					bossName = object.optString("bossName");
				}
			}
			if (object.has("phone")) {
				if (!object.getString("phone").equalsIgnoreCase("null")) {
					phone = object.optString("phone");
				}
			}
			if (object.has("jmdId")) {
				if (!object.getString("jmdId").equalsIgnoreCase("null")) {
					jmdId = object.optString("jmdId");
				}
			}
			if (object.has("distance")) {
				if (!object.getString("distance").equalsIgnoreCase("null")) {
					distance = object.optInt("distance");
				}
			}
			if (object.has("location")) {
				if (!object.getString("location").equalsIgnoreCase("null")) {
					JSONArray locationArray = object.optJSONArray("location");
					if (locationArray != null) {
						location = new ModelLocation(locationArray);
					}
				}
			}
		}
	}

	public String getTitle() {
		return title;
	}

	public String getCity() {
		return city;
	}

	public String getAddress() {
		return address;
	}

	public String getProvince() {
		return province;
	}

	public String getDistrict() {
		return district;
	}

	public String getJmdNo() {
		return jmdNo;
	}

	public String getBossName() {
		return bossName;
	}

	public String getPhone() {
		return phone;
	}

	public String getJmdId() {
		return jmdId;
	}

	public long getDistance() {
		return distance;
	}

	public ModelLocation getLocation() {
		return location;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

}
