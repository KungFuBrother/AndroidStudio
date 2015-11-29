package yitgogo.smart.tools.weather.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json "cityInfo": { "c1": "101010700", "c10": "3", "c11": "010", "c12":
 *       "102200", "c15": "80", "c16": "AZ9010", "c17": "+8", "c2": "changping",
 *       "c3": "昌平", "c4": "beijing", "c5": "北京", "c6": "beijing", "c7": "北京",
 *       "c8": "china", "c9": "中国", "latitude": 40.206, "longitude": 116.165 }
 */
public class ModelWeatherCityInfo {

	String district = "", city = "", province = "", country = "";

	public ModelWeatherCityInfo(JSONObject jsonObject) {
		if (jsonObject != null) {
			if (jsonObject.has("c3")) {
				if (!jsonObject.optString("c3").equalsIgnoreCase("null")) {
					district = jsonObject.optString("c3");
				}
			}
			if (jsonObject.has("c5")) {
				if (!jsonObject.optString("c5").equalsIgnoreCase("null")) {
					city = jsonObject.optString("c5");
				}
			}

			if (jsonObject.has("c7")) {
				if (!jsonObject.optString("c7").equalsIgnoreCase("null")) {
					province = jsonObject.optString("c7");
				}
			}

			if (jsonObject.has("c9")) {
				if (!jsonObject.optString("c9").equalsIgnoreCase("null")) {
					country = jsonObject.optString("c9");
				}
			}
		}
	}

	public ModelWeatherCityInfo() {
	}

	public String getDistrict() {
		return district;
	}

	public String getCity() {
		return city;
	}

	public String getProvince() {
		return province;
	}

	public String getCountry() {
		return country;
	}

}
