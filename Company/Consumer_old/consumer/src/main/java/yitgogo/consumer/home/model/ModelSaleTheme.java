package yitgogo.consumer.home.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @JsonObject { "id": 4, "themeName": "111111111111111111111", "terminalImg":
 *             "http://images.yitos.net/images/public/20150715/13641436961578839.jpg"
 *             , "moblieImg":
 *             "http://images.yitos.net/images/public/20150715/33821436961575763.jpg"
 *             , "area": null, "terminalUrl": null, "moblieUrl": null,
 *             "orderno": 20150715181717, "state": true, "sp": { "id": 3, "no":
 *             "YT444453664072", "brevitycode": "ytcsfws1", "servicename":
 *             "易田测试服务商1", "businessno": "112233333333", "contacts": "订单",
 *             "cardnumber": "222222222222222233", "serviceaddress": "dfggdgg",
 *             "contactphone": "13544443212", "contacttelephone":
 *             "0830-2222222", "email": "ddd@qqj.com", "reva": { "id": 122,
 *             "valuename": "宁波市", "valuetype": { "id": 3, "typename": "市" },
 *             "onid": 11, "onname": null, "brevitycode": null }, "contractno":
 *             "32432424", "contractannex": "", "onservice": { "id": 1, "no":
 *             "YT613630259926", "brevitycode": "scytsmyxgs", "servicename":
 *             "四川易田商贸有限公司", "businessno": "VB11122220000", "contacts": "易田",
 *             "cardnumber": "111111111111111111", "serviceaddress": "成都市金牛区",
 *             "contactphone": "13076063079", "contacttelephone":
 *             "028-83222680", "email": "qqqqq@qq.com", "reva": { "id": 3253,
 *             "valuename": "中国", "valuetype": { "id": 1, "typename": "国" },
 *             "onid": 0, "onname": null, "brevitycode": null }, "contractno":
 *             "SC11111100000", "contractannex": "", "onservice": null, "state":
 *             "启用", "addtime": "2014-09-04 16:01:36", "starttime":
 *             1409760000000, "sptype": "1", "endtime": 1457712000000, "supply":
 *             true, "imghead": "", "longitude": null, "latitude": null },
 *             "state": "启用", "addtime": "2014-09-05 15:09:07", "starttime":
 *             1409846400000, "sptype": "1", "endtime": 1420128000000, "supply":
 *             false, "imghead": null, "longitude": null, "latitude": null } }
 */
public class ModelSaleTheme {

	String id = "", themeName = "", terminalImg = "", moblieImg = "",
			area = "", terminalUrl = "", moblieUrl = "", orderno = "";
	boolean state = false;
	JSONObject jsonObject;

	public ModelSaleTheme(JSONObject object) {
		if (object != null) {
			this.jsonObject = object;
			if (object.has("id")) {
				if (!object.optString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("themeName")) {
				if (!object.optString("themeName").equalsIgnoreCase("null")) {
					themeName = object.optString("themeName");
				}
			}
			if (object.has("terminalImg")) {
				if (!object.optString("terminalImg").equalsIgnoreCase("null")) {
					terminalImg = object.optString("terminalImg");
				}
			}
			if (object.has("moblieImg")) {
				if (!object.optString("moblieImg").equalsIgnoreCase("null")) {
					moblieImg = object.optString("moblieImg");
				}
			}
			if (object.has("area")) {
				if (!object.optString("area").equalsIgnoreCase("null")) {
					area = object.optString("area");
				}
			}
			if (object.has("terminalUrl")) {
				if (!object.optString("terminalUrl").equalsIgnoreCase("null")) {
					terminalUrl = object.optString("terminalUrl");
				}
			}
			if (object.has("moblieUrl")) {
				if (!object.optString("moblieUrl").equalsIgnoreCase("null")) {
					moblieUrl = object.optString("moblieUrl");
				}
			}
			if (object.has("orderno")) {
				if (!object.optString("orderno").equalsIgnoreCase("null")) {
					orderno = object.optString("orderno");
				}
			}
			if (object.has("state")) {
				if (!object.optString("state").equalsIgnoreCase("null")) {
					state = object.optBoolean("state");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getThemeName() {
		return themeName;
	}

	public String getTerminalImg() {
		return terminalImg;
	}

	public String getMoblieImg() {
		return moblieImg;
	}

	public String getArea() {
		return area;
	}

	public String getTerminalUrl() {
		return terminalUrl;
	}

	public String getMoblieUrl() {
		return moblieUrl;
	}

	public String getOrderno() {
		return orderno;
	}

	public boolean isState() {
		return state;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public String toString() {
		return "ModelThemeActivity [id=" + id + ", themeName=" + themeName
				+ ", terminalImg=" + terminalImg + ", moblieImg=" + moblieImg
				+ ", area=" + area + ", terminalUrl=" + terminalUrl
				+ ", moblieUrl=" + moblieUrl + ", orderno=" + orderno
				+ ", state=" + state + ", jsonObject=" + jsonObject + "]";
	}

}
