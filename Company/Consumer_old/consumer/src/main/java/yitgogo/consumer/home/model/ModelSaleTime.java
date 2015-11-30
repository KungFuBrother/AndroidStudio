package yitgogo.consumer.home.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelSaleTime {

	private String saleClassId = "", saleClassName = "", saleClassImage = "";

	public ModelSaleTime(JSONObject object) throws JSONException {
		if (object != null) {
			if (object.has("id")) {
				if (!object.getString("id").equals("null")) {
					setSaleClassId(object.getString("id"));
				}
				if (!object.getString("pcname").equals("null")) {
					setSaleClassName(object.getString("pcname"));
				}
				if (!object.getString("moblieImg").equals("null")) {
					setSaleClassImage(object.getString("moblieImg"));
				}
			}
		}
	}

	public void setSaleClassId(String saleClassId) {
		this.saleClassId = saleClassId;
	}

	public void setSaleClassImage(String saleClassImage) {
		this.saleClassImage = saleClassImage;
	}

	public void setSaleClassName(String saleClassName) {
		this.saleClassName = saleClassName;
	}

	public String getSaleClassId() {
		return saleClassId;
	}

	public String getSaleClassImage() {
		return saleClassImage;
	}

	public String getSaleClassName() {
		return saleClassName;
	}

}
