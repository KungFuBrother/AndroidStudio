package yitgogo.consumer.home.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 购物车商品价格数据
 * 
 * @author Tiger
 * 
 */
public class ModelPriceCar {

	private long productNum = 0;
	private double productPrice = 0;

	public ModelPriceCar(JSONObject object) throws JSONException {
		// TODO Auto-generated constructor stub
		if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
			JSONObject data = object.getJSONObject("dataMap");
			if (data.has("num")) {
				if (!data.getString("num").equalsIgnoreCase("null")) {
					productNum = data.optLong("num");
				}
			}
			if (data.has("price")) {
				if (!data.getString("price").equalsIgnoreCase("null")) {
					productPrice = data.optDouble("price");
				}
			}
		}
	}

	public long getProductNum() {
		return productNum;
	}

	public double getProductPrice() {
		return productPrice;
	}

}
