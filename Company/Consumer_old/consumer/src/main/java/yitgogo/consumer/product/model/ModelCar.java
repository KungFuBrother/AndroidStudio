package yitgogo.consumer.product.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 交易记录数据
 * 
 * @author Tiger
 * 
 */
public class ModelCar {

	private long productCount = 1;
	private boolean isSelected = true;
	private JSONObject jsonObject;
	private ModelProduct product = new ModelProduct();

	public ModelCar(JSONObject object) throws JSONException {
		if (object != null) {
			jsonObject = object;
			if (object.has("productCount")) {
				if (!object.getString("productCount").equalsIgnoreCase("null")) {
					productCount = object.optLong("productCount");
				}
			}
			if (object.has("isSelected")) {
				if (!object.getString("isSelected").equalsIgnoreCase("null")) {
					isSelected = object.optBoolean("isSelected");
				}
			}
			product = new ModelProduct(object.optJSONObject("product"));
		}
	}

	public ModelProduct getProduct() {
		return product;
	}

	public long getProductCount() {
		return productCount;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

}
