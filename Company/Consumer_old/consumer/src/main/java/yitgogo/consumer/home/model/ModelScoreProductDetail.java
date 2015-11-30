package yitgogo.consumer.home.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Tiger
 * 
 * @JsonObject { "id": 3, "imgs": [
 *             "http://yttestother.oss-cn-qingdao.aliyuncs.com/other/public/20150805/51451438741627460.jpg"
 *             ], "addtime": "2015-08-04", "gonghuojia": 10, "price": 0,
 *             "jifenjia": 10, "pifajia": 10, "no": 984, "neirong": "测试四川1",
 *             "name": "测试供应商一测试产品3", "xiangqing": "
 *             <p>
 *             秉承诚信经营的理念，用优质的产品换取客户的好评！！！！
 *             </p>
 *             ", "attr": "黑色24吋", "number": "YT71958329368", "jifen": 10,
 *             "caigoujia": 10, "productId": 37
 * 
 * 
 * 
 */
public class ModelScoreProductDetail {

	String id = "", addtime = "", neirong = "", name = "", xiangqing = "",
			attr = "", number = "", productId = "";
	double gonghuojia = 0, price = 0, jifenjia = 0, pifajia = 0, caigoujia = 0;
	long jifen = 0, no = 0;
	List<String> imgs = new ArrayList<String>();

	public ModelScoreProductDetail() {
	}

	public ModelScoreProductDetail(JSONObject object) throws JSONException {
		if (object != null) {
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			JSONArray imageArray = object.optJSONArray("imgs");
			if (imageArray != null) {
				for (int i = 0; i < imageArray.length(); i++) {
					imgs.add(imageArray.optString(i));
				}
			}
			if (object.has("addtime")) {
				if (!object.getString("addtime").equalsIgnoreCase("null")) {
					addtime = object.optString("addtime");
				}
			}
			if (object.has("no")) {
				if (!object.getString("no").equalsIgnoreCase("null")) {
					no = object.optLong("no");
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
			if (object.has("xiangqing")) {
				if (!object.getString("xiangqing").equalsIgnoreCase("null")) {
					xiangqing = object.optString("xiangqing");
				}
			}
			if (object.has("attr")) {
				if (!object.getString("attr").equalsIgnoreCase("null")) {
					attr = object.optString("attr");
				}
			}
			if (object.has("number")) {
				if (!object.getString("number").equalsIgnoreCase("null")) {
					number = object.optString("number");
				}
			}
			if (object.has("productId")) {
				if (!object.getString("productId").equalsIgnoreCase("null")) {
					productId = object.optString("productId");
				}
			}
			if (object.has("gonghuojia")) {
				if (!object.getString("gonghuojia").equalsIgnoreCase("null")) {
					gonghuojia = object.optDouble("gonghuojia");
				}
			}
			if (object.has("price")) {
				if (!object.getString("price").equalsIgnoreCase("null")) {
					price = object.optDouble("price");
				}
			}
			if (object.has("jifenjia")) {
				if (!object.getString("jifenjia").equalsIgnoreCase("null")) {
					jifenjia = object.optDouble("jifenjia");
				}
			}
			if (object.has("pifajia")) {
				if (!object.getString("pifajia").equalsIgnoreCase("null")) {
					pifajia = object.optDouble("pifajia");
				}
			}
			if (object.has("caigoujia")) {
				if (!object.getString("caigoujia").equalsIgnoreCase("null")) {
					caigoujia = object.optDouble("caigoujia");
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

	public String getAddtime() {
		return addtime;
	}

	public long getNo() {
		return no;
	}

	public String getNeirong() {
		return neirong;
	}

	public String getName() {
		return name;
	}

	public String getXiangqing() {
		return xiangqing;
	}

	public String getAttr() {
		return attr;
	}

	public String getNumber() {
		return number;
	}

	public double getGonghuojia() {
		return gonghuojia;
	}

	public double getPrice() {
		return price;
	}

	public double getJifenjia() {
		return jifenjia;
	}

	public double getPifajia() {
		return pifajia;
	}

	public double getCaigoujia() {
		return caigoujia;
	}

	public long getJifen() {
		return jifen;
	}

	public List<String> getImgs() {
		return imgs;
	}

	public String getProductId() {
		return productId;
	}

	@Override
	public String toString() {
		return "ModelScoreProductDetail [id=" + id + ", addtime=" + addtime
				+ ", neirong=" + neirong + ", name=" + name + ", xiangqing="
				+ xiangqing + ", attr=" + attr + ", number=" + number
				+ ", gonghuojia=" + gonghuojia + ", price=" + price
				+ ", jifenjia=" + jifenjia + ", pifajia=" + pifajia
				+ ", caigoujia=" + caigoujia + ", jifen=" + jifen + ", no="
				+ no + ", imgs=" + imgs + "]";
	}

}
