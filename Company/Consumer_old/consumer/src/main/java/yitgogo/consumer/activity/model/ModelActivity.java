package yitgogo.consumer.activity.model;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.store.model.ModelStoreSelected;

/**
 * 
 * @author Tiger
 * 
 * @Json { "id": 4, "activityName": "七夕时刻", "activityImg":
 *       "http://192.168.8.98:8087/images/public/20150819/55901439975585460.jpg"
 *       , "titleImg":
 *       "http://images.yitos.net/images/public/20150819/54901439975595835.jpg,http://192.168.8.98:8087/images/public/20150819/54901439975595835.jpg"
 *       , "totalMoney": 5000, "surplusMoney": 5000, "lowestMoney": 100,
 *       "highestMoney": 500, "activityNum": "489799", "activityState": "启用",
 *       "activityStartTime": "2015-08-20 08:00:46", "progress": 1, "addUser":
 *       "测试一", "service": { "id": 1, "no": "YT613630259926", "brevitycode":
 *       "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *       "VB11122220000", "contacts": "易田", "cardnumber": "111111111111111111",
 *       "serviceaddress": "成都市金牛区", "contactphone": "13076063079",
 *       "contacttelephone": "028-83222680", "email": "qqqqq@qq.com", "reva": {
 *       "id": 3253, "valuename": "中国", "valuetype": { "id": 1, "typename": "国"
 *       }, "onid": 0, "onname": null, "brevitycode": null }, "contractno":
 *       "SC11111100000", "contractannex": "", "onservice": null, "state": "启用",
 *       "addtime": "2014-09-04 16:01:36", "starttime": 1409760000000, "sptype":
 *       "1", "endtime": 1457712000000, "supply": true, "imghead": "",
 *       "longitude": null, "latitude": null }, "addTime":
 *       "2015-08-19 17:13:59", "winExtent": 50, "winNum": 10 }
 */
public class ModelActivity {

	String id = "", activityName = "", activityImg = "", titleImg = "",
			activityNum = "", activityState = "", activityStartTime = "",
			progress = "", addUser = "", addTime = "";
	double totalMoney = 0, surplusMoney = 0, lowestMoney = 0, highestMoney = 0;
	ModelStoreSelected service = new ModelStoreSelected();
	int winExtent = 0, winNum = 0;
	JSONObject jsonObject = new JSONObject();

	public ModelActivity() {
	}

	public ModelActivity(JSONObject object) throws JSONException {
		if (object != null) {
			jsonObject = object;
			if (object.has("id")) {
				if (!object.optString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("activityName")) {
				if (!object.optString("activityName").equalsIgnoreCase("null")) {
					activityName = object.optString("activityName");
				}
			}
			if (object.has("activityImg")) {
				if (!object.optString("activityImg").equalsIgnoreCase("null")) {
					activityImg = object.optString("activityImg");
				}
			}
			if (object.has("titleImg")) {
				if (!object.optString("titleImg").equalsIgnoreCase("null")) {
					titleImg = object.optString("titleImg");
				}
			}
			if (object.has("activityNum")) {
				if (!object.optString("activityNum").equalsIgnoreCase("null")) {
					activityNum = object.optString("activityNum");
				}
			}
			if (object.has("activityState")) {
				if (!object.optString("activityState").equalsIgnoreCase("null")) {
					activityState = object.optString("activityState");
				}
			}
			if (object.has("activityStartTime")) {
				if (!object.optString("activityStartTime").equalsIgnoreCase(
						"null")) {
					activityStartTime = object.optString("activityStartTime");
				}
			}
			if (object.has("progress")) {
				if (!object.optString("progress").equalsIgnoreCase("null")) {
					progress = object.optString("progress");
				}
			}
			if (object.has("addUser")) {
				if (!object.optString("addUser").equalsIgnoreCase("null")) {
					addUser = object.optString("addUser");
				}
			}
			if (object.has("addTime")) {
				if (!object.optString("addTime").equalsIgnoreCase("null")) {
					addTime = object.optString("addTime");
				}
			}
			if (object.has("totalMoney")) {
				if (!object.optString("totalMoney").equalsIgnoreCase("null")) {
					totalMoney = object.optDouble("totalMoney");
				}
			}
			if (object.has("surplusMoney")) {
				if (!object.optString("surplusMoney").equalsIgnoreCase("null")) {
					surplusMoney = object.optDouble("surplusMoney");
				}
			}
			if (object.has("lowestMoney")) {
				if (!object.optString("lowestMoney").equalsIgnoreCase("null")) {
					lowestMoney = object.optDouble("lowestMoney");
				}
			}
			if (object.has("highestMoney")) {
				if (!object.optString("highestMoney").equalsIgnoreCase("null")) {
					highestMoney = object.optDouble("highestMoney");
				}
			}
			service = new ModelStoreSelected(object.optJSONObject("service"));
			winExtent = object.optInt("winExtent");
			winNum = object.optInt("winNum");
		}
	}

	public String getId() {
		return id;
	}

	public String getActivityName() {
		return activityName;
	}

	public String getActivityImg() {
		return activityImg;
	}

	public String getTitleImg() {
		return titleImg;
	}

	public String getActivityNum() {
		return activityNum;
	}

	public String getActivityState() {
		return activityState;
	}

	public String getActivityStartTime() {
		return activityStartTime;
	}

	public String getProgress() {
		return progress;
	}

	public String getAddUser() {
		return addUser;
	}

	public String getAddTime() {
		return addTime;
	}

	public double getTotalMoney() {
		return totalMoney;
	}

	public double getSurplusMoney() {
		return surplusMoney;
	}

	public double getLowestMoney() {
		return lowestMoney;
	}

	public double getHighestMoney() {
		return highestMoney;
	}

	public ModelStoreSelected getService() {
		return service;
	}

	public int getWinExtent() {
		return winExtent;
	}

	public int getWinNum() {
		return winNum;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

}
