package yitgogo.smart.home.model;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.product.model.ModelProduct;

/**
 * 交易记录数据
 * 
 * @author Tiger
 * 
 */
public class ModelCar {

	private long productCount = 1;
	private boolean isSelected = true;
	private JSONObject carJsonObject;
	private ModelProduct product = new ModelProduct();

	public ModelCar(JSONObject object) throws JSONException {
		if (object != null) {
			carJsonObject = object;
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

	public JSONObject getCarJsonObject() {
		return carJsonObject;
	}

}
