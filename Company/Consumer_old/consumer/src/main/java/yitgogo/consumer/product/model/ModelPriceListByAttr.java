package yitgogo.consumer.product.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelPriceListByAttr {
	// {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount":1,"dataMap":{"imgName":"http://images.yitos.net/images/public/20150511/67331431320632162.png","num":50,"price":124.0,"yuanjia":null,"pname":"玖尼 2015年2015夏装新款黑色条纹阔腿裤松紧腰休闲裤  XY01901","state":"1"},"object":null}
	String imgName = "", pname = "", stateString = "";
	long num = 0;
	double price = 0, yuanjia = 0;
	int state = 2;

	public ModelPriceListByAttr(JSONObject object) throws JSONException {
		// TODO Auto-generated constructor stub
		if (object.has("imgName")) {
			if (!object.getString("imgName").equalsIgnoreCase("null")) {
				imgName = object.optString("imgName");
			}
		}
		if (object.has("pname")) {
			if (!object.getString("pname").equalsIgnoreCase("null")) {
				pname = object.optString("pname");
			}
		}
		if (object.has("state")) {
			if (!object.getString("state").equalsIgnoreCase("null")) {
				state = object.optInt("state");
				switch (state) {
				case 1:
					stateString = "有货";
					break;

				case 2:
					stateString = "无货";
					break;
				case 3:
					stateString = "无货,可预订";
					break;
				default:
					stateString = "无货";
					break;
				}
			}
		}
		if (object.has("num")) {
			if (!object.getString("num").equalsIgnoreCase("null")) {
				num = object.optLong("num");
			}
		}
		if (object.has("price")) {
			if (!object.getString("price").equalsIgnoreCase("null")) {
				price = object.optDouble("price");
			}
		}
		if (object.has("yuanjia")) {
			if (!object.getString("yuanjia").equalsIgnoreCase("null")) {
				yuanjia = object.optDouble("yuanjia");
			}
		}
	}

	public String getImgName() {
		return imgName;
	}

	public String getPname() {
		return pname;
	}

	public String getStateString() {
		return stateString;
	}

	public long getNum() {
		return num;
	}

	public double getPrice() {
		return price;
	}

	public double getYuanjia() {
		return yuanjia;
	}

	public int getState() {
		return state;
	}
}
