package yitgogo.smart.tools.weather.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json { "showapi_res_code": 0, "showapi_res_error": "", "showapi_res_body": {
 *       "cityInfo": { "c1": "101010700", "c10": "3", "c11": "010", "c12":
 *       "102200", "c15": "80", "c16": "AZ9010", "c17": "+8", "c2": "changping",
 *       "c3": "昌平", "c4": "beijing", "c5": "北京", "c6": "beijing", "c7": "北京",
 *       "c8": "china", "c9": "中国", "latitude": 40.206, "longitude": 116.165 },
 *       "f1": { "day": "20150912", "day_air_temperature": "25", "day_weather":
 *       "晴", "day_weather_pic":
 *       "http://appimg.showapi.com/images/weather/icon/day/00.png",
 *       "day_wind_direction": "北风", "day_wind_power": "3-4级10~17m/h",
 *       "night_air_temperature": "12", "night_weather": "晴",
 *       "night_weather_pic":
 *       "http://appimg.showapi.com/images/weather/icon/night/00.png",
 *       "night_wind_direction": "无持续风向", "night_wind_power": "微风<10m/h",
 *       "sun_begin_end": "05:52|18:32", "weekday": 6 }, "f2": { "day":
 *       "20150913", "day_air_temperature": "28", "day_weather": "晴",
 *       "day_weather_pic":
 *       "http://appimg.showapi.com/images/weather/icon/day/00.png",
 *       "day_wind_direction": "无持续风向", "day_wind_power": "微风<10m/h",
 *       "night_air_temperature": "14", "night_weather": "多云",
 *       "night_weather_pic":
 *       "http://appimg.showapi.com/images/weather/icon/night/01.png",
 *       "night_wind_direction": "无持续风向", "night_wind_power": "微风<10m/h",
 *       "sun_begin_end": "05:52|18:32", "weekday": 7 }, "f3": { "day":
 *       "20150914", "day_air_temperature": "25", "day_weather": "多云",
 *       "day_weather_pic":
 *       "http://appimg.showapi.com/images/weather/icon/day/01.png",
 *       "day_wind_direction": "无持续风向", "day_wind_power": "微风<10m/h",
 *       "night_air_temperature": "15", "night_weather": "多云",
 *       "night_weather_pic":
 *       "http://appimg.showapi.com/images/weather/icon/night/01.png",
 *       "night_wind_direction": "无持续风向", "night_wind_power": "微风<10m/h",
 *       "sun_begin_end": "05:52|18:32", "weekday": 1 }, "now": { "aqi": "13",
 *       "sd": "32%", "temperature": "22", "temperature_time": "11:30",
 *       "weather": "晴", "weather_pic":
 *       "http://appimg.showapi.com/images/weather/icon/day/00.png",
 *       "wind_direction": "西风", "wind_power": "3级" }, "ret_code": 0, "time":
 *       "20150912080000" } }
 */
public class ModelWeatherInfo {

	int showapi_res_code = 0;
	String showapi_res_error = "";
	ModelWeatherData weatherData = new ModelWeatherData();
	ModelWeatherCityInfo weatherCityInfo = new ModelWeatherCityInfo();

	public ModelWeatherInfo(JSONObject jsonObject) {
		if (jsonObject != null) {
			showapi_res_code = jsonObject.optInt("showapi_res_code");
			if (jsonObject.has("showapi_res_error")) {
				if (!jsonObject.optString("showapi_res_error")
						.equalsIgnoreCase("null")) {
					showapi_res_error = jsonObject
							.optString("showapi_res_error");
				}
			}
			JSONObject object = jsonObject.optJSONObject("showapi_res_body");
			if (object != null) {
				weatherData = new ModelWeatherData(object.optJSONObject("now"));
				weatherCityInfo = new ModelWeatherCityInfo(
						object.optJSONObject("cityInfo"));
			}
		}
	}

	public ModelWeatherInfo() {
	}

	public int getShowapi_res_code() {
		return showapi_res_code;
	}

	public String getShowapi_res_error() {
		return showapi_res_error;
	}

	public ModelWeatherData getWeatherData() {
		return weatherData;
	}

	public ModelWeatherCityInfo getWeatherCityInfo() {
		return weatherCityInfo;
	}

}
