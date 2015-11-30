package yitgogo.consumer.product.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @JsonObject "listzu": [ { "id": "33512", "attName": "白色37码", "img":
 *             "http://images.yitos.net/images/public/20150608/52991433747828259.png"
 *             , "number": "YT81764199692" }, { "id": "33513", "attName":
 *             "白色37码", "img":
 *             "http://images.yitos.net/images/public/20150608/52991433747828259.png"
 *             , "number": "YT81764243379" } ]
 * 
 * @Description 商品详情关联产品
 */
/**
 * @author Tiger
 * 
 */
public class ModelProductRelation {

	String id = "", attName = "", img = "", number = "";

	public ModelProductRelation() {

	}

	public ModelProductRelation(JSONObject object) throws JSONException {
		if (object != null) {
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("attName")) {
				if (!object.getString("attName").equalsIgnoreCase("null")) {
					attName = object.optString("attName");
				}
			}
			if (object.has("img")) {
				if (!object.getString("img").equalsIgnoreCase("null")) {
					img = object.optString("img");
				}
			}
			if (object.has("number")) {
				if (!object.getString("number").equalsIgnoreCase("null")) {
					number = object.optString("number");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getAttName() {
		return attName;
	}

	public String getImg() {
		return img;
	}

	public String getNumber() {
		return number;
	}

	@Override
	public String toString() {
		return "ModelProductRelation [id=" + id + ", attName=" + attName
				+ ", img=" + img + ", number=" + number + "]";
	}

}
