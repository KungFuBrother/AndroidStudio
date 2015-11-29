package yitgogo.smart.tools.weather.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json "now": { "aqi": "13", "sd": "32%", "temperature": "22",
 *       "temperature_time": "11:30", "weather": "晴", "weather_pic":
 *       "http://appimg.showapi.com/images/weather/icon/day/00.png",
 *       "wind_direction": "西风", "wind_power": "3级" },
 */
public class ModelWeatherData {

	String temperature = "", temperature_time = "", weather = "",
			weather_pic = "";

	public ModelWeatherData(JSONObject jsonObject) {
		if (jsonObject != null) {
			if (jsonObject.has("temperature")) {
				if (!jsonObject.optString("temperature").equalsIgnoreCase(
						"null")) {
					temperature = jsonObject.optString("temperature");
				}
			}
			if (jsonObject.has("temperature_time")) {
				if (!jsonObject.optString("temperature_time").equalsIgnoreCase(
						"null")) {
					temperature_time = jsonObject.optString("temperature_time");
				}
			}

			if (jsonObject.has("weather")) {
				if (!jsonObject.optString("weather").equalsIgnoreCase("null")) {
					weather = jsonObject.optString("weather");
				}
			}

			if (jsonObject.has("weather_pic")) {
				if (!jsonObject.optString("weather_pic").equalsIgnoreCase(
						"null")) {
					weather_pic = jsonObject.optString("weather_pic");
				}
			}
		}
	}

	public ModelWeatherData() {
	}

	public String getTemperature() {
		return temperature;
	}

	public String getTemperature_time() {
		return temperature_time;
	}

	public String getWeather() {
		return weather;
	}

	public String getWeather_pic() {
		return weather_pic;
	}

}
