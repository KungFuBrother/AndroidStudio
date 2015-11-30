package yitgogo.consumer.home.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Tiger
 * 
 * @JsonObject { "id": 11, "imgs":
 *             "http://image.yitos.net/images/public/20150715/32601436959713318.jpg"
 *             , "jifenjia": 10, "neirong": "测试积分商城AAA", "name":
 *             "迈途2015春夏防紫外线皮肤风衣 男女防水防晒衫 情侣款透气皮肤衣服男款19011", "number":
 *             "YT38314613177", "jifen": 10 }
 * 
 */
public class ModelScoreProduct {

	String id = "", imgs = "", neirong = "", name = "", number = "";
	long jifen = 0;
	double jifenjia = 0;

	public ModelScoreProduct() {
	}

	public ModelScoreProduct(JSONObject object) throws JSONException {
		if (object != null) {
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("imgs")) {
				if (!object.getString("imgs").equalsIgnoreCase("null")) {
					imgs = object.optString("imgs");
				}
			}
			if (object.has("jifenjia")) {
				if (!object.getString("jifenjia").equalsIgnoreCase("null")) {
					jifenjia = object.optDouble("jifenjia");
				}
			}
			if (object.has("neirong")) {
				if (!object.getString("neirong").equalsIgnoreCase("null")) {
					neirong = object.optString("neirong");
				}
			}
			if (object.has("name")) {
				if (!object.getString("name").equalsIgnoreCase("null")) {
					name = object.optString("name");
				}
			}
			if (object.has("number")) {
				if (!object.getString("number").equalsIgnoreCase("null")) {
					number = object.optString("number");
				}
			}
			if (object.has("jifen")) {
				if (!object.getString("jifen").equalsIgnoreCase("null")) {
					jifen = object.optLong("jifen");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getImgs() {
		return imgs;
	}

	public double getJifenjia() {
		return jifenjia;
	}

	public String getNeirong() {
		return neirong;
	}

	public String getName() {
		return name;
	}

	public String getNumber() {
		return number;
	}

	public long getJifen() {
		return jifen;
	}

	@Override
	public String toString() {
		return "ModelScoreProduct [id=" + id + ", imgs=" + imgs + ", jifenjia="
				+ jifenjia + ", neirong=" + neirong + ", name=" + name
				+ ", number=" + number + ", jifen=" + jifen + "]";
	}

}
