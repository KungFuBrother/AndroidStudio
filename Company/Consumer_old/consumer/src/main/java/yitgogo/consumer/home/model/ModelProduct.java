package yitgogo.consumer.home.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json { "id": 33423, "number": "YT43892693235", "huohao": null,
 *       "productName": "玖尼 2015夏装新款黑色条纹阔腿裤松紧腰休闲裤  XY0191", "classValueBean":
 *       null, "attributeValueSet": null, "attributeValueExtendSet": [], "unit":
 *       null, "place": "中国", "providerBean": null, "fuwushangId": 1, "price":
 *       null, "purchasePrice": null, "tradePrice": null, "supplyPrice": null,
 *       "gonghuoPrice": null, "productNum": 50, "img":
 *       "http://images.yitos.net/images/public/20150511/67331431320632162.png"
 *       , "imgs": null, "details": null, "adddate": null, "updateTime":
 *       20150511132533, "regionalValueSet": null, "userName": null, "state":
 *       "上架", "brandBean": null, "sort": 0, "sortDate": 20150511130958,
 *       "brieFcode": "jn2015xzxkhstwktksjyxxkXY0191", "beizhuName":
 *       "玖尼 2015夏装新款黑色条纹阔腿裤松紧腰休闲裤  XY0191玖尼 2015年2015夏装新款黑色条纹阔腿裤松紧腰休闲裤  XY01901玖尼 2015年2015夏装新款黑色条纹阔腿裤松紧腰休闲裤  XY01901玖尼 2015年2015夏装新款黑色条纹阔腿裤松紧腰休闲裤  XY01901玖尼 2015年2015夏装新款黑色条纹阔腿裤松紧腰休闲裤  XY01901玖尼 2015年2015夏装新款黑色条纹阔腿裤松紧腰休闲裤  XY01901玖尼 2015年2015夏装新款黑色条纹阔腿裤松紧腰休闲裤  XY01901"
 *       , "productAddType": "4", "caigouYN": "否", "caigouJG": null, "pifaJG":
 *       null, "stateYD": null, "classValueIdD": "347", "classValueIdZ": "349",
 *       "classValueIdX": "362" }
 */
public class ModelProduct {

	String id = "", number = "", huohao = "", productName = "",
			classValueBean = "", attributeValueSet = "",
			attributeValueExtendSet = "", unit = "", place = "",
			providerBean = "", fuwushangId = "", price = "",
			purchasePrice = "", tradePrice = "", supplyPrice = "",
			gonghuoPrice = "", productNum = "", img = "", imgs = "",
			details = "", adddate = "", updateTime = "", regionalValueSet = "",
			userName = "", state = "", brandBean = "", sort = "",
			sortDate = "", brieFcode = "", beizhuName = "",
			productAddType = "", caigouYN = "", caigouJG = "", pifaJG = "",
			stateYD = "", classValueIdD = "", classValueIdZ = "",
			classValueIdX = "";

	public ModelProduct(JSONObject object) throws JSONException {
		// TODO Auto-generated constructor stub
		if (object.has("id")) {
			if (!object.getString("id").equalsIgnoreCase("null")) {
				id = object.optString("id");
			}
		}
		if (object.has("number")) {
			if (!object.getString("number").equalsIgnoreCase("null")) {
				number = object.optString("number");
			}
		}
		if (object.has("productName")) {
			if (!object.getString("productName").equalsIgnoreCase("null")) {
				productName = object.optString("productName");
			}
		}
		if (object.has("img")) {
			if (!object.getString("img").equalsIgnoreCase("null")) {
				img = object.optString("img");
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getNumber() {
		return number;
	}

	public String getHuohao() {
		return huohao;
	}

	public String getProductName() {
		return productName;
	}

	public String getClassValueBean() {
		return classValueBean;
	}

	public String getAttributeValueSet() {
		return attributeValueSet;
	}

	public String getAttributeValueExtendSet() {
		return attributeValueExtendSet;
	}

	public String getUnit() {
		return unit;
	}

	public String getPlace() {
		return place;
	}

	public String getProviderBean() {
		return providerBean;
	}

	public String getFuwushangId() {
		return fuwushangId;
	}

	public String getPrice() {
		return price;
	}

	public String getPurchasePrice() {
		return purchasePrice;
	}

	public String getTradePrice() {
		return tradePrice;
	}

	public String getSupplyPrice() {
		return supplyPrice;
	}

	public String getGonghuoPrice() {
		return gonghuoPrice;
	}

	public String getProductNum() {
		return productNum;
	}

	public String getImg() {
		return img;
	}

	public String getImgs() {
		return imgs;
	}

	public String getDetails() {
		return details;
	}

	public String getAdddate() {
		return adddate;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public String getRegionalValueSet() {
		return regionalValueSet;
	}

	public String getUserName() {
		return userName;
	}

	public String getState() {
		return state;
	}

	public String getBrandBean() {
		return brandBean;
	}

	public String getSort() {
		return sort;
	}

	public String getSortDate() {
		return sortDate;
	}

	public String getBrieFcode() {
		return brieFcode;
	}

	public String getBeizhuName() {
		return beizhuName;
	}

	public String getProductAddType() {
		return productAddType;
	}

	public String getCaigouYN() {
		return caigouYN;
	}

	public String getCaigouJG() {
		return caigouJG;
	}

	public String getPifaJG() {
		return pifaJG;
	}

	public String getStateYD() {
		return stateYD;
	}

	public String getClassValueIdD() {
		return classValueIdD;
	}

	public String getClassValueIdZ() {
		return classValueIdZ;
	}

	public String getClassValueIdX() {
		return classValueIdX;
	}

}
