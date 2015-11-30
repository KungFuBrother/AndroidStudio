package yitgogo.consumer.store.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @JsonObject { "id": 1068, "no": "YT117351516493", "brevitycode": "csyyzxy",
 *             "servicename": "测试运营中心一", "businessno": "SB789456", "contacts":
 *             "测试运营中心一", "cardnumber": "123456789789789789", "serviceaddress":
 *             "四川省成都市金牛区解放路二段", "contactphone": "15878978945",
 *             "contacttelephone": "028-2356895623", "email":
 *             "45645645656@qq.com", "reva": { "id": 2421, "valuename": "金牛区",
 *             "valuetype": { "id": 4, "typename": "区县" }, "onid": 269,
 *             "onname": null, "brevitycode": null }, "contractno":
 *             "SB15614616", "contractannex": "", "onservice": { "id": 1064,
 *             "no": "YT117351165326", "brevitycode": "csfwsy", "servicename":
 *             "测试服务商一", "businessno": "SB123456", "contacts": "测试服务商一",
 *             "cardnumber": "513012121212121212", "serviceaddress":
 *             "四川省成都市金牛区解放路二段六号", "contactphone": "15823565656",
 *             "contacttelephone": "028-23562356", "email": "145615616@qq.com",
 *             "reva": { "id": 3253, "valuename": "中国", "valuetype": { "id": 1,
 *             "typename": "国" }, "onid": 0, "onname": null, "brevitycode": null
 *             }, "contractno": "SB123115321531", "contractannex": "",
 *             "onservice": null, "state": "启用", "addtime":
 *             "2015-05-30 11:03:07", "starttime": 1432915200000, "sptype": "1",
 *             "endtime": 1564416000000, "supply": false, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2015-05-30 11:22:27", "starttime": 1432915200000, "sptype": "2",
 *             "endtime": 1577462400000, "supply": false, "imghead": "",
 *             "longitude": "104.08902889614", "latitude": "30.686814874166" }
 */
public class ModelStoreSelected {

	String id = "", no = "", brevitycode = "", servicename = "",
			businessno = "", contacts = "", cardnumber = "",
			serviceaddress = "", contactphone = "", contacttelephone = "",
			email = "", contractno = "", contractannex = "", state = "",
			addtime = "", starttime = "", sptype = "", endtime = "",
			equip = "", bankSet = "", supply = "", imghead = "";
	ModelArea area = new ModelArea();
	ModelStoreSelected onService;
	ModelLocation location = new ModelLocation();
	JSONObject jsonObject = new JSONObject();

	public ModelStoreSelected() {
	}

	public ModelStoreSelected(JSONObject object) throws JSONException {
		if (object != null) {
			jsonObject = object;
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("no")) {
				if (!object.getString("no").equalsIgnoreCase("null")) {
					no = object.optString("no");
				}
			}
			if (object.has("brevitycode")) {
				if (!object.getString("brevitycode").equalsIgnoreCase("null")) {
					brevitycode = object.optString("brevitycode");
				}
			}
			if (object.has("servicename")) {
				if (!object.getString("servicename").equalsIgnoreCase("null")) {
					servicename = object.optString("servicename");
				}
			}
			if (object.has("businessno")) {
				if (!object.getString("businessno").equalsIgnoreCase("null")) {
					businessno = object.optString("businessno");
				}
			}
			if (object.has("contacts")) {
				if (!object.getString("contacts").equalsIgnoreCase("null")) {
					contacts = object.optString("contacts");
				}
			}
			if (object.has("cardnumber")) {
				if (!object.getString("cardnumber").equalsIgnoreCase("null")) {
					cardnumber = object.optString("cardnumber");
				}
			}
			if (object.has("serviceaddress")) {
				if (!object.getString("serviceaddress")
						.equalsIgnoreCase("null")) {
					serviceaddress = object.optString("serviceaddress");
				}
			}
			if (object.has("contactphone")) {
				if (!object.getString("contactphone").equalsIgnoreCase("null")) {
					contactphone = object.optString("contactphone");
				}
			}
			if (object.has("contacttelephone")) {
				if (!object.getString("contacttelephone").equalsIgnoreCase(
						"null")) {
					contacttelephone = object.optString("contacttelephone");
				}
			}
			if (object.has("email")) {
				if (!object.getString("email").equalsIgnoreCase("null")) {
					email = object.optString("email");
				}
			}
			JSONObject reva = object.optJSONObject("reva");
			if (reva != null) {
				area = new ModelArea(reva);
			}
			if (object.has("contractno")) {
				if (!object.getString("contractno").equalsIgnoreCase("null")) {
					contractno = object.optString("contractno");
				}
			}
			if (object.has("contractno")) {
				if (!object.getString("contractno").equalsIgnoreCase("null")) {
					contractno = object.optString("contractno");
				}
			}
			if (object.has("contractannex")) {
				if (!object.getString("contractannex").equalsIgnoreCase("null")) {
					contractannex = object.optString("contractannex");
				}
			}
			JSONObject onservice = object.optJSONObject("onservice");
			if (onservice != null) {
				onService = new ModelStoreSelected(onservice);
			}
			if (object.has("state")) {
				if (!object.getString("state").equalsIgnoreCase("null")) {
					state = object.optString("state");
				}
			}
			if (object.has("addtime")) {
				if (!object.getString("addtime").equalsIgnoreCase("null")) {
					addtime = object.optString("addtime");
				}
			}
			if (object.has("starttime")) {
				if (!object.getString("starttime").equalsIgnoreCase("null")) {
					starttime = object.optString("starttime");
				}
			}
			if (object.has("sptype")) {
				if (!object.getString("sptype").equalsIgnoreCase("null")) {
					sptype = object.optString("sptype");
				}
			}
			if (object.has("endtime")) {
				if (!object.getString("endtime").equalsIgnoreCase("null")) {
					endtime = object.optString("endtime");
				}
			}
			if (object.has("supply")) {
				if (!object.getString("supply").equalsIgnoreCase("null")) {
					supply = object.optString("supply");
				}
			}
			if (object.has("imghead")) {
				if (!object.getString("imghead").equalsIgnoreCase("null")) {
					imghead = object.optString("imghead");
				}
			}

			if (object.has("longitude")) {
				if (!object.getString("longitude").equalsIgnoreCase("null")) {
					location.setLongitude(object.optDouble("longitude"));
				}
			}
			if (object.has("latitude")) {
				if (!object.getString("latitude").equalsIgnoreCase("null")) {
					location.setLatitude(object.optDouble("latitude"));
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getNo() {
		return no;
	}

	public String getBrevitycode() {
		return brevitycode;
	}

	public String getServicename() {
		return servicename;
	}

	public String getBusinessno() {
		return businessno;
	}

	public String getContacts() {
		return contacts;
	}

	public String getCardnumber() {
		return cardnumber;
	}

	public String getServiceaddress() {
		return serviceaddress;
	}

	public String getContactphone() {
		return contactphone;
	}

	public String getContacttelephone() {
		return contacttelephone;
	}

	public String getEmail() {
		return email;
	}

	public String getContractno() {
		return contractno;
	}

	public String getContractannex() {
		return contractannex;
	}

	public String getState() {
		return state;
	}

	public String getAddtime() {
		return addtime;
	}

	public String getStarttime() {
		return starttime;
	}

	public String getSptype() {
		return sptype;
	}

	public String getEndtime() {
		return endtime;
	}

	public String getEquip() {
		return equip;
	}

	public String getBankSet() {
		return bankSet;
	}

	public String getSupply() {
		return supply;
	}

	public String getImghead() {
		return imghead;
	}

	public ModelArea getArea() {
		return area;
	}

	public ModelStoreSelected getOnService() {
		return onService;
	}

	public ModelLocation getLocation() {
		return location;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public String toString() {
		return "ModelStoreSelected [id=" + id + ", no=" + no + ", brevitycode="
				+ brevitycode + ", servicename=" + servicename
				+ ", businessno=" + businessno + ", contacts=" + contacts
				+ ", cardnumber=" + cardnumber + ", serviceaddress="
				+ serviceaddress + ", contactphone=" + contactphone
				+ ", contacttelephone=" + contacttelephone + ", email=" + email
				+ ", contractno=" + contractno + ", contractannex="
				+ contractannex + ", state=" + state + ", addtime=" + addtime
				+ ", starttime=" + starttime + ", sptype=" + sptype
				+ ", endtime=" + endtime + ", equip=" + equip + ", bankSet="
				+ bankSet + ", supply=" + supply + ", imghead=" + imghead
				+ ", area=" + area + ", onService=" + onService + ", location="
				+ location + ", jsonObject=" + jsonObject + "]";
	}

}
