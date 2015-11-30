package yitgogo.consumer.home.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelHomeBrand {

	private String dataId = "", brandLogo = "", brandName = "", brandId = "",
			sequenceId = "";

	public ModelHomeBrand(JSONObject object) {
		if (object != null) {

			try {
				if (object.has("id")) {
					if (!object.getString("id").equals("null")) {
						setDataId(object.getString("id"));
					}
				}
				if (object.has("brandLogo")) {
					if (!object.getString("brandLogo").equals("null")) {
						setBrandLogo(object.getString("brandLogo"));
					}
				}
				if (object.has("brandName")) {
					if (!object.getString("brandName").equals("null")) {
						setBrandName(object.getString("brandName"));
					}
				}
				if (object.has("brandId")) {
					if (!object.getString("brandId").equals("null")) {
						setBrandId(object.getString("brandId"));
					}
				}
				if (object.has("sequenceId")) {
					if (!object.getString("sequenceId").equals("null")) {
						setSequenceId(object.getString("sequenceId"));
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public void setBrandLogo(String brandLogo) {
		this.brandLogo = brandLogo;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getBrandId() {
		return brandId;
	}

	public String getBrandLogo() {
		return brandLogo;
	}

	public String getBrandName() {
		return brandName;
	}

	public String getDataId() {
		return dataId;
	}

	public String getSequenceId() {
		return sequenceId;
	}

}
